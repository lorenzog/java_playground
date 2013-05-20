package localization.backend.listeners;

import localization.backend.cores.Core;
import localization.backend.cores.NullCore;

public abstract class Listener {
	protected boolean isReady = false;
	
	/* please keep the following line here;
	 * it's used to avoid race conditions.
	 * 
	 * example: we set up a Listener but the Core is not yet ready
	 * to receive data; when the listener sends data via callback,
	 * it might result in a nullPointer exception.
	 * 
	 * this core object will be overwritten when the real Core 
	 * is set up.
	 */
	protected Core core = new NullCore();
	
	public boolean isReady() {
		return isReady;
	}
	
	public void setCore(Core core) {
		this.core = core;
	}
}
