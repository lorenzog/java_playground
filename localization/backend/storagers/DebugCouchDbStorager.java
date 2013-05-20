package localization.backend.storagers;


import java.util.HashMap;
import java.util.Map;

import localization.backend.utils.Util;

/**
 * 
 * @author alberto valente and lorenzo grespan
 *
 */
public class DebugCouchDbStorager extends CouchDbStorager {
	
	public DebugCouchDbStorager(String dbHost, String dbName, int dbPort) {
		super(dbHost, dbName, dbPort);
	}
	
	/*
	public synchronized void newData(byte[] rawData) {
		Util.dbg("Saving new raw data inside the db");
		Map<Long, byte[]> doc = new HashMap<Long, byte[]>();
		doc.put(99l, rawData); //99 for chipcon raw packets
		db.createDocument(doc);
		
	}
	*/
	@Override
	//note: timestamp is lost here
	public synchronized void newData(String rawData, long timestamp) {
		Util.dbg("Saving new raw data inside the db");
		Map<Long, String> doc = new HashMap<Long, String>();
		doc.put(999l, rawData); // 999 for gps raw strings
		db.createDocument(doc);
		
	}
	
}
