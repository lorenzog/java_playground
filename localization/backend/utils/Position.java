package localization.backend.utils;

import localization.backend.Main.PACKET_TYPE;

/**
 * This object serves as a container for all positions data; it is filled by: 1)
 * the listener (when it receives raw data) 2) the localizer (when it elaborates
 * the data)
 * 
 * then it is read by the feeder and the storager
 * 
 * should be discarded by the garbage collector once it's done
 * 
 * @author lorenzo
 * 
 */
public class Position {

	/* this is the global id of the person */
	long globalId;
	
	/* type describes the type of position
	 * type = 1 position from chipcon
	 * type = 2 position from gps
	 */
	PACKET_TYPE PACKET_TYPE;

	long timestamp;
	float lat, longit, z;

	/* never read locally but used when printing it with JSON */
	int closestRefnodeId;
	
	@Override
	public String toString() {
		return "type: " + PACKET_TYPE + " timestamp: " + timestamp + " lat: " + lat + " lon: " + longit + " z: " + z 
		+ " refId: " + closestRefnodeId;
	}

	public Position(long timestamp) {
		globalId = -1;
		this.timestamp = timestamp;
		PACKET_TYPE = PACKET_TYPE.VOID;
		lat = 0l;
		longit = 0l;
		z = 0l;
	}

	public void setGlobalId(long id) {
		this.globalId = id;
	}

	public void setType(PACKET_TYPE packet_type) {
		this.PACKET_TYPE = packet_type;
	}

	public void setTimeStamp(long currentTime) {
		this.timestamp = currentTime;
	}
	
	public void setCoords(float lat, float longit, float z) {
		this.lat = lat;
		this.longit = longit;
		this.z = z;
	}
	
	public long getGlobalId() {
		return globalId;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public PACKET_TYPE getType() {
		return PACKET_TYPE;
	}
	
	public float getLat() {
		return lat;
	}
	
	public float getLong() {
		return longit;
	}
	
	public float getZ() {
		return z;
	}

	public void setClosestRefnodeId(int refNodeId) {
		this.closestRefnodeId = refNodeId;
	}

	

}
