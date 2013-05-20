package localization.backend.localizers;

import localization.backend.cores.TestCore;

public class GpsLocalizerTest extends GpsLocalizer {

	public static void main(String[] args) {
		long timestamp = System.currentTimeMillis();
		String testStrings[] = {
				"GPS:ID=1;time=" + timestamp + ";long=11.0218732;lat=45.4515733;alt=68.842;spd=0.03000084;crs=275.40747",
				"GPS:ID=1;time=" + timestamp + ";long=11.0219822;lat=45.4515559;alt=85.778;spd=0.24000672;crs=264.2735" };

		GpsLocalizer g = new GpsLocalizer();
		g.core = new TestCore();

		for (String s : testStrings) {
			System.out.println("Feeding string: " + s);
			// the second arg is later discarded because it is read from the packet
			g.newData(s.getBytes(), timestamp);
		}

	}
}
