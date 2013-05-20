package localization.backend.localizers;

import localization.backend.utils.Util;

public class NullLocalizer extends Localizer {

	@Override
	public void newData(byte[] readBuffer, long timestamp) {
		Util.dbg("Null Localizer got new byte[] data");
	}


}
