package localization.backend.feeders;

import localization.backend.utils.Position;
import localization.backend.utils.Util;

public class NullFeeder extends Feeder {

	@Override
	public void newData(Position newPosition) {
		Util.dbg("received position");
	}

}
