package localization.backend.cores;

import localization.backend.utils.Position;
import localization.backend.utils.Util;

public class TestCore extends Core {

	@Override
	public void newComputedPosition(Position position) {
		Util.dbg("Test core received position: " + position.toString());
	}

	@Override
	public void newRawData(byte[] rawData, long timestamp) {
		Util.dbg("Test core received rawData: " + rawData.toString());
	}

}
