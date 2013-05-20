package localization.backend.cores;

import localization.backend.feeders.Feeder;
import localization.backend.listeners.Listener;
import localization.backend.localizers.*;
import localization.backend.storagers.Storager;
import localization.backend.utils.Position;
import localization.backend.utils.Util;

/**
 * This class is the main Core class, inherited
 * by all other Cores.
 * 
 * @author lorenzo grespan
 *
 */
public abstract class Core {

	protected Feeder feeder;
	protected Feeder debugFeeder;
	protected Listener listener;
	protected Localizer localizer;
	protected Storager storager;
	protected Storager debugStorager;

	public void setListener(Listener listener) {
		if ( listener != null ) 
			this.listener = listener;
		else
			Util.err("null listener");
		this.listener.setCore(this);
	}

	public void setLocalizer(Localizer localizer) {
		if ( localizer != null ) 
			this.localizer = localizer;
		else
			Util.err("null localizer");
		this.localizer.setCore(this);
	}

	public void setFeeder(Feeder feeder) {
		if ( feeder != null )
			this.feeder = feeder;
		else
			Util.err("null feeder");
	}
	
	public void setDebugFeeder(Feeder debugFeeder) { 
		if ( debugFeeder != null ) 
			this.debugFeeder = debugFeeder;
		else
			Util.err("null debug feeder");
	}

	public void setStorager(Storager storager) {
		if ( storager != null ) 
			this.storager = storager;
		else
			Util.err("null storager");
	}
	
	public void setDebugStorager(Storager debugStorager) { 
		if ( debugStorager != null ) 
			this.debugStorager = debugStorager;
		else
			Util.err("null debugStorager");
	}

	/* must implement */
	public abstract void newComputedPosition(Position position);
	public abstract void newRawData(byte[] rawData, long timestamp);
}
