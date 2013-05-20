package localization.backend.storagers;

import localization.backend.utils.Position;
import localization.backend.utils.Util;

public abstract class Storager {
	
	protected boolean isReady = false;
	
	public boolean isReady() {
		return isReady;
	}
	
	public void newData(Position newPosition) {
		Util.dbg("Storager got new Position");
	}
	
	public void newData(byte[] rawData, long timestamp) {
		Util.dbg("Storager got new byte[]");
	}
	public void newData(String rawData, long timestamp) {
		Util.dbg("Storager got new String");
	}
}
