package localization.backend.cores;

import localization.backend.utils.Position;
import localization.backend.utils.Util;

/**
 * this class is used to avoid race conditions and
 * null-pointer exceptions.
 * 
 * if a thread executes a listener or localizer BEFORE
 * their real core is ready, this class will get executed
 * 
 * @author lorenzo grespan
 *
 */
public class NullCore extends Core {

	public boolean isReady() {
		return false;
	}

	@Override
	public void newComputedPosition(Position position) {
		Util.err("race condition: method newComputedPosition " + 
				" called before the actual Core is ready");
	}

	@Override
	public void newRawData(byte[] rawData, long timestamp) {
		Util.err("race condition: method newRawData called " + 
				" called before the actual Core is ready");
	}

}
