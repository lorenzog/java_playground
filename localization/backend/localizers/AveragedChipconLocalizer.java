package localization.backend.localizers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import localization.backend.Main.PACKET_TYPE;
import localization.backend.utils.Blind;
import localization.backend.utils.Position;
import localization.backend.utils.Util;

public class AveragedChipconLocalizer extends Localizer {

	private PACKET_TYPE PACKET_TYPE;
	/* maps node id to node object for lat/long lookups */
	Map<Integer, refNode> refnodeMap;

	// the default latitude and longitude of the origin of refNodes Coords System
	private float originX = 45.4515733f;
	private float originY = 11.0218732f;
	private float originZ = 0f;

	/* a list of all blinds available */
	Map<Integer, Blind> blinds = new HashMap<Integer, Blind>();

	private int RSSI_OFFSET;
	private int REFNODE_GATEWAY;
	
	/**
	 * the constructor initializes the mapping refnode id <-> coordinates; it is
	 * not supposed to be called from the outside
	 */
	private AveragedChipconLocalizer(String refnodeFileInfo) {
		Util.dbg("Chipcon average localizer algorithm initialising...");
		refnodeMap = new HashMap<Integer, refNode>();

		Properties config = new Properties();
		boolean readFromFile = false;
		try {
			FileInputStream in = new FileInputStream(refnodeFileInfo);
			config.load(in);
			in.close();
			readFromFile = true;
		} catch (FileNotFoundException e) {
			Util.err("no refnode file info found, using default values");
		} catch (IOException e) {
			Util.err("I/O error in reading refnode file info");
		}

		/**
		 * if we can read config from file, try parsing it
		 * 
		 * line must be REFNODE=1,0.1,0.2,0.3
		 */
		if (readFromFile) {
			/* set the origin if specified in file */
			if (config.containsKey("ORIGIN_X"))
				originX = Float.parseFloat(config.getProperty("ORIGIN_X"));
			if (config.containsKey("ORIGIN_Y"))
				originY = Float.parseFloat(config.getProperty("ORIGIN_Y"));
			if (config.containsKey("ORIGIN_Z"))
				originY = Float.parseFloat(config.getProperty("ORIGIN_Z"));

			Util.dbg("Origin set to: " + originX + "," + originY + ","
					+ originZ);

			/* get everything else */
			Enumeration<?> en = config.propertyNames();
			int count = 0;
			while (en.hasMoreElements()) {
				count++;
				String name = (String) en.nextElement();

				String[] tmp = ((String) config.get(name)).split(",");
				if (tmp.length == 4) {
					// might be a refnode name=x,y,z
					try {
						int refnodeId = Integer.parseInt(tmp[0]);
						float lat = Float.parseFloat(tmp[1]);
						float longit = Float.parseFloat(tmp[2]);
						float alt = Float.parseFloat(tmp[3]);
						refnodeMap.put(refnodeId, new refNode(refnodeId, lat,
								longit, alt, name));
						Util.dbg("Added refnode " + name + " with id: "
								+ refnodeId + " lat: " + lat + " long: "
								+ longit + " alt: " + alt);
					} catch (NumberFormatException ex) {
						Util.err("Cannot parse info in line no. " + count);
						continue;
					}
				}
			}
		} else {
			// default: hard-coding refnodes coordinates
			refnodeMap.put(0, new refNode(0, 10.1f, 0.1f, 0.1f, "zero"));
			refnodeMap.put(1, new refNode(1, 10.1f, 0.1f, 0.1f, "uno"));
			refnodeMap.put(2, new refNode(2, 20.2f, 10.11f, 0.1f, "due"));
			refnodeMap.put(3, new refNode(3, 30.3f, 20.2f, 0.1f, "tre"));
			refnodeMap.put(4, new refNode(4, 40.4f, 30.3f, 0.1f, "quattro"));
			refnodeMap.put(5, new refNode(5, 50.5f, 40.4f, 0.1f, "cinque"));
		}
	}

	/**
	 * public constructor
	 * 
	 * @param refnodeFileInfo
	 * @param RSSI_OFFSET
	 * @param CHIPCON
	 */
	public AveragedChipconLocalizer(String refnodeFileInfo, int RSSI_OFFSET,
			PACKET_TYPE PACKET_TYPE, int REFNODE_GATEWAY) {
		this(refnodeFileInfo);

		this.RSSI_OFFSET = RSSI_OFFSET;
		this.REFNODE_GATEWAY = REFNODE_GATEWAY;

		this.PACKET_TYPE = PACKET_TYPE;

		Util.dbg("Chipcon localizer algorithm ready");
		isReady = true;
	}

	/**
	 * when new data is received
	 */
	@Override
	public void newData(byte[] readBuffer, long timestamp) {
		// for each new packet,
		// extract its blind number
		int probableBlindId = readBuffer[1] & 0xFF;

		if (!blinds.containsKey(probableBlindId)) {
			/* if it's an unknown blind, create and init it first */
			Blind tmp = new Blind(this, probableBlindId, RSSI_OFFSET, REFNODE_GATEWAY);
			/* pass it new data */
			tmp.newData(readBuffer, timestamp);
			blinds.put(probableBlindId, tmp);
		} else
			/* if not, we can directly pass it the new data */
			(blinds.get(probableBlindId)).newData(readBuffer, timestamp);
	}

	/**
	 * this method is called back from the Blind when a new position has been
	 * computed
	 * 
	 * @param refNodeId
	 * @param rssiAvg
	 */
	public void updatePosition(int blindId, int refNodeId, long rssiAvg,
			long timestamp) {

		Util.dbg("received new high average to publish: blind id " + blindId
				+ " refnode: " + refNodeId + " rssiAvg: " + rssiAvg);
		/* get the coordinates of the closest refnode from the known locations */
		refNode closestRefNode = refnodeMap.get(refNodeId);
		if (closestRefNode == null) {
			// if no node matches the key
			Util.err("no valid refnode for id: " + refNodeId);
			return;
		}
		System.out.println("blind id " + blindId + " now closer to refnode: "
				+ closestRefNode.name + " with rssi average: " + rssiAvg);
		Position pos = new Position(timestamp);
		pos.setType(PACKET_TYPE);

		// TODO lookup for real patient id <-> blind id
		// pos.setGlobalId(findGlobalId(blindId));
		pos.setGlobalId(blindId);
		pos.setClosestRefnodeId(refNodeId);

		// TODO use the rssiAVG to compute statistical probability of being
		// close to that node
		pos.setCoords(closestRefNode.lat, closestRefNode.longit,
				closestRefNode.alt);
		core.newComputedPosition(pos);
	}

	/**
	 * this method looks into an internal DB and determines the id of the person
	 * given his or her sensor ID.
	 * 
	 * why we need this method: because the same person might have a chipcon
	 * sensor AND a gps
	 * 
	 * @param blindId
	 * @return the global ID
	 */
	/*
	 * private int findGlobalId(int blindId) { }
	 */

	/**
	 * This anonymous inner class is used when creating the hashmap id <->
	 * refnode for faster lookups.
	 * 
	 * @author alberto valente
	 * 
	 */
	class refNode {
		int refnodeId;
		float lat, longit, alt;
		String name;

		public refNode(int refnodeId, float baseLat, float baseLong,
				float baseAlt, String name) {
			this.refnodeId = refnodeId;
			this.lat = baseLat + originX;
			this.longit = baseLong + originY; // note: was originX (?)
			this.alt = baseAlt + originZ;
			this.name = name;

			Util.dbg("Added new reference node " + name + " (" + refnodeId
					+ ") with coords (" + lat + ";" + longit + ";" + alt + ")");
		}

	}

}
