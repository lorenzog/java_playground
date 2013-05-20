package localization.backend.localizers;

import localization.backend.cores.Core;

public abstract class Localizer {
	
	protected Core core;
	protected boolean isReady = false;
	
	public void setCore(Core core) {
		this.core = core;
	}
	
	public boolean isReady() {
		return isReady;
	}
	
	public abstract void newData(byte[] data, long timestamp); 
	
}
