package localization.backend.storagers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcouchdb.db.Database;
import org.jcouchdb.db.Server;
import org.jcouchdb.db.ServerImpl;
import org.jcouchdb.document.DesignDocument;
import org.jcouchdb.document.View;
import org.jcouchdb.exception.CouchDBException;

import localization.backend.utils.Position;
import localization.backend.utils.Util;

public class CouchDbStorager extends Storager {
	
	/*
	 * (new Timer()).schedule(storager, 1000, storagerTimer * 1000);
	 */
	
	String dbHost;
	int dbPort;
	String dbName;
	Server server;
	Database db;
	
	boolean isReady = false;
	
	public CouchDbStorager(String dbHost, String dbName, int dbPort) {
		this.dbHost = dbHost;
		this.dbName = dbName;
		this.dbPort = dbPort;
		setupServer();
		checkForDatabase();
	}
	
	private void setupServer() {
		server = new ServerImpl(dbHost, dbPort);
//		db = new Database(server, dbName);
		checkForDatabase();
	}
	
	public void checkForDatabase()
	{
		List<String> db_temp = null;
		try { 
			db_temp = server.listDatabases();
		}
		catch ( CouchDBException e ) {
			Util.err("Please turn on couchdb server");
			isReady = false;
			return;
		}
		if (!db_temp.contains(dbName))
		{
			server.createDatabase(dbName);
			db = new Database(server, dbName);
			createDesignDocument();
		}
		else db = new Database(server, dbName);
		isReady = true;
	}
	
	public void createDesignDocument()
	{
		DesignDocument designDocument = new DesignDocument("foo");
		designDocument.addView("byValue", new View("function(doc) {emit([doc.data.globalId, doc.data.timestamp],doc.data)}"));
		db.createDocument(designDocument);
	}
	
	@Override
	public synchronized void newData(Position newPosition) {
		if ( !isReady )
		{
			Util.dbg("couchDB not ready");
			return;
		}
		Util.dbg("Saving new position inside the db");
		
		/*
		 * Map<String, Long> doc = new HashMap<String, Long>();
		 * 
		 * doc.put("globalId", newPosition.getGlobalId()); doc.put("timestamp",
		 * newPosition.getTimestamp()); doc.put("type", newPosition.getType());
		 * doc.put("x", newPosition.getX()); doc.put("y", newPosition.getY());
		 * doc.put("z", newPosition.getZ());
		 */
		
		Map<String, Position> doc = new HashMap<String, Position>();
		doc.put("data", newPosition);
		try {
			db.createDocument(doc);
		} catch (CouchDBException e) {
			Util.err("Exception when putting the document: check server");
		}
		
	}
	
}
