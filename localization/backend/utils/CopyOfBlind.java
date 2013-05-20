package localization.backend.utils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.Map.Entry;

import localization.backend.localizers.AveragedChipconLocalizer;

/**
 * The main Blind class: corresponds to each device carried by each user.
 * 
 * When a packet is received by the system, (i) the blind id is extracted (ii)
 * the packet is passed to the respective Blind object via the newData([])
 * method (iii) the packet is then saved into the appropriate refnode buffer
 * 
 * Also,
 * 
 * (j) a cleaning thread runs every CLEANER_PERIOD interval and removes old
 * packets from the buffers; (jj) an 'inspector' thread runs every
 * INSPECTOR_PERIOD and extracts the ref node with the highest average: this
 * will determine the blind position
 * 
 * @author lorenzo grespan
 * 
 *         TODO measure incoming packet frequency; modulate CLEANER_PERIOD
 *         accordingly. 
 * 
 */
public class CopyOfBlind {

	/**
	 * for callback
	 */
	AveragedChipconLocalizer loc;

	/* coming from the main config file */
	private int RSSI_OFFSET;
	private int REFNODE_GATEWAY;
	
	final int blindId;

	private final long RSSI_MIN_VALUE = -255;

	/* in ms */
	final long CLEANER_PERIOD = 1000;
	final long INSPECTOR_PERIOD = 2000;
	final int HISTORY_MAXSIZE = 5;

	/**
	 * each entry is a refnode packet buffer where every time we store a new
	 * value, the average is recomputed
	 */
	Map<Integer, RefnodeBuffer> refnodeBuffers = new HashMap<Integer, RefnodeBuffer>();

	public CopyOfBlind(final AveragedChipconLocalizer loc, final int blindId,
			int RSSI_OFFSET, int REFNODE_GATEWAY) {
		/* for callback */
		this.loc = loc;
		this.blindId = blindId;
		
		this.RSSI_OFFSET = RSSI_OFFSET;
		this.REFNODE_GATEWAY = REFNODE_GATEWAY;

		/**
		 * this thread walks through the received packet buffers and removes the
		 * latest entry
		 * 
		 * TODO halt cleaner thread if history is empty?
		 */
		boolean isDaemon = true;
		long cleanerDelay = 2000; /* in ms */
		Timer cleaner = new Timer(isDaemon);
		cleaner.schedule(new Cleaner(), cleanerDelay, CLEANER_PERIOD);

		/**
		 * this thread walks through the precomputed averages and extracts the
		 * highest; then it does a callback to update the position of the blind
		 * 
		 * TODO: halt inspector thread when history is empty? (check perf first)
		 */
		long inspectorDelay = 3000; /* in ms */
		Timer inspector = new Timer(isDaemon);
		inspector.schedule(new Inspector(loc, blindId), inspectorDelay,
				INSPECTOR_PERIOD);
		
		
	}

	/**
	 * a new packet is received
	 * 
	 * @param readBuffer
	 * @param timestamp
	 */
	public void newData(byte[] readBuffer, long timestamp) {
		/*
		 * readBuffer[1] was the blind id, already specified in caller class
		 */
		// unused:
		// int blindBatt = readBuffer[2];

		long progressive = readBuffer[3] & 0xFF;
		int refId = readBuffer[4] & 0xFF;

		// unused:
		// int lqi = readBuffer[5];

		long rssi_tmp = readBuffer[6] & 0xFF;
		long rssi = calculateRSSI(rssi_tmp);
		Util.dbg("rssi_tmp: " + rssi_tmp + ", rssi: " + rssi);
		System.out.println("rssi_tmp: " + rssi_tmp + ", rssi: " + rssi);
		
		/* quick hack: don't consider the gateway a valid refnode */
		if ( refId == REFNODE_GATEWAY ) return;
		
		/* add the new packet to the buffer (this will recompute the average) */
		synchronized (refnodeBuffers) {
			/* if reference node id is yet unknown, init new object */
			if (!refnodeBuffers.containsKey(refId)) {
				RefnodeBuffer tmp = new RefnodeBuffer();
				tmp.newData(progressive, rssi, timestamp);
				refnodeBuffers.put(refId, tmp);
			} else
				refnodeBuffers.get(refId).newData(progressive, rssi, timestamp);
		}
	}

	/**
	 * 
	 * @param refRssi_temp
	 * @return
	 * 
	 * @author alberto valente
	 */
	private long calculateRSSI(long refRssi_temp) {
		long refRssi;

		if (refRssi_temp >= 128) {
			refRssi = (refRssi_temp - 256) / 2 - RSSI_OFFSET;
		} else {
			refRssi = refRssi_temp / 2 - RSSI_OFFSET;
		}
		return refRssi;
	}

	/**
	 * 
	 * attempt: removed /2
	 * 
	 * @param refRssi_temp
	 * @return
	 * 
	 * @author lorenzo grespan
	 */
	private long calculateRSSI2(long refRssi_temp) {
		long refRssi;

		if (refRssi_temp >= 128) {
			refRssi = (refRssi_temp - 256) - RSSI_OFFSET;
		} else {
			refRssi = refRssi_temp - RSSI_OFFSET;
		}
		return refRssi;
	}


	private final class Cleaner extends TimerTask {
		@Override
		public void run() {
			synchronized (refnodeBuffers) {
				// pop the element with the lowest progressive number
				// (it will recalculate the average)
				for (RefnodeBuffer b : refnodeBuffers.values())
					b.removeLast();

			}
		}
	}

	/**
	 * this class walks the buffers and selects the refnode that has the highest
	 * average ( = closest); if found, it will callback and update the blind
	 * position
	 * 
	 * @author lorenzo grespan
	 * 
	 */
	private final class Inspector extends TimerTask {
		private final AveragedChipconLocalizer loc;
		private final int blindId;
		long previousHighestAverageRSSI;// = RSSI_MIN_VALUE;
		int previousHighestAverageNode;// = -1;

		private Inspector(AveragedChipconLocalizer loc, int blindId) {
			this.loc = loc;
			this.blindId = blindId;
		}

		@Override
		public void run() {
			previousHighestAverageRSSI = RSSI_MIN_VALUE;
			previousHighestAverageNode = -1;
			
			// for each refnodeBuffer, get the computed average;
			boolean mustCallbackPosition = false;
			synchronized (refnodeBuffers) {
				// iterate through each buffer
				Iterator<Entry<Integer, RefnodeBuffer>> i = refnodeBuffers
						.entrySet().iterator();
				Map.Entry<Integer, RefnodeBuffer> b = null;
				while (i.hasNext()) {
					b = i.next();

					if (b.getValue().rssiAvg > previousHighestAverageRSSI) {
						previousHighestAverageRSSI = b.getValue().rssiAvg;
						previousHighestAverageNode = b.getKey();
						mustCallbackPosition = true;
					}
				}
			}

			if (mustCallbackPosition) {
				// we're updating the position when the timestamp
				// of the *computed* value. 
				loc.updatePosition(blindId, previousHighestAverageNode,
						previousHighestAverageRSSI, System.currentTimeMillis());
				mustCallbackPosition = false;
			}

		}
	}

	/**
	 * the packet buffer that each refnode has received
	 * 
	 * @author lorenzo grespan
	 * 
	 */
	private class RefnodeBuffer {
		/* a sorted set to keep the received packet in order */
		TreeSet<RSSIHistoryElement> history;

		/* a constantly updated average value */
		long rssiAvg;

		RefnodeBuffer() {
			/* init the history with a proper Comparator */
			history = new TreeSet<RSSIHistoryElement>(
					new Comparator<RSSIHistoryElement>() {
						@Override
						public int compare(RSSIHistoryElement o1,
								RSSIHistoryElement o2) {
							// Util.dbg("comparing (" + o1.progressive + "," +
							// o1.rssi
							// + ") with (" + o2.progressive + "," + o2.rssi +
							// ")");
							if (o1.timestamp < o2.timestamp) {
								// Util.dbg("first is smaller");
								return -1;
							}
							if (o1.timestamp > o2.timestamp) {
								// Util.dbg("second is smaller");
								return 1;
							}
							// Util.dbg("elements are equal");
							return 0;
						}
					});

			rssiAvg = RSSI_MIN_VALUE;
		}

		/**
		 * new entry in the refnode history
		 * 
		 * @param progressive
		 * @param rssi
		 * @param timestamp
		 */
		public void newData(long progressive, long rssi, long timestamp) {
			boolean ret;
			synchronized (history) {
				Util.dbg("adding new element with prog: " + progressive
						+ " rssi: " + rssi);
				ret = history.add(new RSSIHistoryElement(progressive, rssi, timestamp));
				// update average only if an element was added
				if (ret)
				{
					/* [ old average * ( old size ) + new value ] / new size */
					rssiAvg = (rssiAvg * (history.size() - 1) + rssi)
							/ history.size();
					Util.dbg("history size: " + history.size() );
				}
				else
					Util.err("packet already received with progressive "
							+ progressive + " and rssi: " + rssi);
			}
			Util.dbg("packet buffer for blind id " + blindId + " now contains "
					+ history.size() + " elements; new average: " + rssiAvg);
		}

		/**
		 * remove last element in packet buffer and recalculate the average
		 * 
		 * @author lorenzo
		 */
		private void removeLast() {
			long tmpRSSI;
			RSSIHistoryElement tmpRSSIElement;
			synchronized (history) {
				// check history: keep at constant size
				// XXX LORENZO, 2010 04 15
				// remove the last element
				/* TODO: instead of trimming it so brutally, 
				 * we should check the size and adjust the cleaner/inspector frequency
				 * so that the size stays at constant lengths
				 * 
				 * ...one day, one day.
				 */
				do
					tmpRSSIElement = history.pollFirst();
				while ( history.size() > HISTORY_MAXSIZE );
				
				if (tmpRSSIElement == null)
					/* set was empty; nothing to do */
					return;
				tmpRSSI = tmpRSSIElement.rssi;
				// if that was last element, division by zero!
				if (history.isEmpty())
					rssiAvg = RSSI_MIN_VALUE;
				else
					/* [ ( old average * old size - value ) / new size ] */
					rssiAvg = (rssiAvg * (history.size() + 1) - tmpRSSI)
							/ history.size();

			}
			Util.dbg("removed value " + tmpRSSI
					+ " from packet buffer of blind id: " + blindId
					+ "; new size: " + history.size() + "; new average: "
					+ rssiAvg);
		}

	}

	/**
	 * this object contains the tuple packet number <-> RSSI value; it is used
	 * in a sorted collection to keep a sorted buffer of received packet
	 * 
	 * sorting criteria will be: progressive number
	 * 
	 * @author lorenzo grespan
	 * 
	 */
	private class RSSIHistoryElement {
		public long progressive;
		public long rssi;
		public long timestamp;

		RSSIHistoryElement(long progressive, long rssi, long timestamp) {
			this.progressive = progressive;
			this.rssi = rssi;
			this.timestamp = timestamp;
		}
	}

}
