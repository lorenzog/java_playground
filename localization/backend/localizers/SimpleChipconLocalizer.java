package localization.backend.localizers;

import java.util.HashMap;
import java.util.Map;

import localization.backend.Main.PACKET_TYPE;
import localization.backend.utils.Position;
import localization.backend.utils.Util;

public class SimpleChipconLocalizer extends Localizer {

	private int RSSI_OFFSET;
	private PACKET_TYPE PACKET_TYPE;
	Map<Long, refNode> refNodeMap;
	float originX, originY, originZ;

	public SimpleChipconLocalizer() {
		Util.dbg("Chipcon localizer algorithm ready");
		refNodeMap = new HashMap<Long, refNode>();

		// latitude and longitude of the origin of refNodes Coords System
		originX = 45.4515733f;
		originY = 11.0218732f;
		originZ = 0f;
		
		// reference nodes' coordinates have to be hardcoded here
		refNodeMap.put(0l, new refNode(0, 10.1f, 0.1f, 0.1f));
		refNodeMap.put(1l, new refNode(1, 10.1f, 0.1f, 0.1f));
		refNodeMap.put(2l, new refNode(2, 20.2f, 10.11f, 0.1f));
		refNodeMap.put(3l, new refNode(3, 30.3f, 20.2f, 0.1f));
		refNodeMap.put(4l, new refNode(4, 40.4f, 30.3f, 0.1f));
		refNodeMap.put(5l, new refNode(5, 50.5f, 40.4f, 0.1f));

		isReady = true;
	}

	public SimpleChipconLocalizer(int RSSI_OFFSET, PACKET_TYPE PACKET_TYPE) {
		this();
		this.RSSI_OFFSET = RSSI_OFFSET;
		this.PACKET_TYPE = PACKET_TYPE;
	}

	@Override
	public void newData(byte[] readBuffer, long timestamp) {
		float lat, longit, z;
		refNode sourceRefNode = new refNode();

		Util.dbg("Chipcon packet received");

		long blindId = readBuffer[1];
		/* long blindBatt = readBuffer[2]; */
		/* long progressive = readBuffer[3]; */
		long refId = readBuffer[4];
		/* long lqi = readBuffer[5]; */
		long rssi_tmp = readBuffer[6] & 0xFF;
		long rssi = calculateRSSI(rssi_tmp);
		long globalId = blindId;

		// 1st algorithm: set coords to refNode Id
		/*
		 * x = refId; y = refId; z = refId;
		 */

		// 2nd algorithm: set coords to refNode coords
		sourceRefNode = refNodeMap.get(refId);
		lat = sourceRefNode.getLat();
		longit = sourceRefNode.getLongit();
		z = sourceRefNode.getZ();

		/* we have a new position, let's pass it on! */
		Position pos = new Position(timestamp);
		pos.setType(PACKET_TYPE);
		pos.setGlobalId(globalId);
		pos.setCoords(lat, longit, z);
		core.newComputedPosition(pos);
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
	 * This anonymous inner class is used when creating the hashmap id <->
	 * refnode for faster lookups.
	 * 
	 * @author alberto valente
	 * 
	 */
	class refNode {
		long id;
		float lat, longit, z;
	
		public refNode() {
	
		}
	
		public refNode(long id, float latTmp, float longitTmp, float zTmp) {
			this.id = id;
			this.lat = latTmp+originX;
			this.longit = longitTmp+originX;
			this.z = zTmp+originZ;
			Util.dbg("Added new reference node with coords (" + lat + ";" + longit
					+ ";" + z + ")");
		}
	
		public long getId() {
			return id;
		}
	
		public float getLat() {
			return lat;
		}
	
		public float getLongit() {
			return longit;
		}
	
		public float getZ() {
			return z;
		}
	}

}
