package localization.backend.cores;

import localization.backend.utils.Position;

public class NetworkCore extends Core {

	/**
	 * when we receive a computed position, feed it to
	 * a) the real-time feeder
	 * b) the storager
	 */
	@Override
	public void newComputedPosition(Position newPosition) {
		synchronized(feeder) {
			feeder.newData(newPosition);
		}
		synchronized(storager) {
			storager.newData(newPosition);
		}
	}

	/** when we receive new raw data, 
	 * 1) we save it and 
	 * 2) we pass it to the localizer to obtain the position
	 * 
	 * @see localization.backend.cores.Core#newRawData(byte[])
	 */
	@Override
	public void newRawData(byte[] rawData, long timestamp) {
		/* store the raw data somewhere */
		synchronized(debugStorager) {
			debugStorager.newData(new String(rawData), timestamp);
		}
		/* localize the position with the new data */
		synchronized(localizer) {
			localizer.newData(rawData, timestamp);
		}
	}
}
