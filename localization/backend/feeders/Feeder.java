package localization.backend.feeders;

import localization.backend.utils.Position;
import localization.backend.utils.Util;

public abstract class Feeder implements Runnable {
	
	protected boolean isReady = false;
	
	/* when it receives new data from the core */
	public abstract void newData(Position newPosition); 
	
	public void run() {
		Util.dbg("Feeder run()");
	}
	
	public boolean isReady() {
		return isReady;
	}
}
