package localization.backend.localizers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import localization.backend.Main.PACKET_TYPE;
import localization.backend.utils.Position;
import localization.backend.utils.Util;

public class GpsLocalizer extends Localizer {

	private PACKET_TYPE PACKET_TYPE;
	private final String REGEXP = "GPS:ID=(\\d+);time=(\\d+);long=(\\d+\\.\\d+);lat=(\\d+\\.\\d+);alt=(\\d+\\.\\d+);spd=(\\d+\\.\\d+);crs=(\\d+\\.\\d+)";
	Pattern p;
	Matcher m;

	long globalId;

	int id = -1;
	long time = -1;
	float lat = 0f, longit = 0f, alt = 0f, spd = 0f, crs = 0f;

	GpsLocalizer() {
		p = Pattern.compile(REGEXP);
		isReady = true;
		Util.dbg("GPS localizer algorithm ready");
	}

	public GpsLocalizer(PACKET_TYPE PACKET_TYPE) {
		this();
		this.PACKET_TYPE = PACKET_TYPE;
	}

	@Override
	public void newData(byte[] readBuffer, long timestamp) {

		Util.dbg("GPS coords received as byte[], calling overloaded method");

		String s = new String(readBuffer);
		Util.dbg(s);
		// note: we discarded the received timestamp because we are reading it
		// from the GPS packet 
		newData(s);
	}

	public void newData(String s) {
		Util.dbg("GPS coords received as String");

		m = p.matcher(s);
		if (m.matches()) {
			id = Integer.parseInt(m.group(1));
			//time = Long.parseLong(m.group(2));
			time = System.currentTimeMillis();
			longit = Float.parseFloat(m.group(3));
			lat = Float.parseFloat(m.group(4));
			alt = Float.parseFloat(m.group(5));
			spd = Float.parseFloat(m.group(6));
			crs = Float.parseFloat(m.group(7));
			Util.dbg("id: " + id + " time: " + time + " long: " + longit
					+ " lat: " + lat + " alt: " + alt + " spd: " + spd
					+ " crs: " + crs);

			/*
			 * TODO: match received gps iD with global id globalId =
			 * findGlobalId(id);
			 */

			/* new position: with the timestamp received by the backend */
			Position pos = new Position(time);
			pos.setType(PACKET_TYPE);
			/* should be global id... */
			pos.setGlobalId(id);
			pos.setCoords(lat, longit, alt);
			core.newComputedPosition(pos);
		} else
			Util.dbg("received string did not match");
	}

	/**
	 * @see ChipconLocalizer:findGlobalId
	 */
	/*
	 * private int findGlobalId(int blindId) { return blindId; }
	 */

}
