package localization.backend;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import localization.backend.cores.SerialCore;
import localization.backend.cores.NetworkCore;
import localization.backend.feeders.Feeder;
import localization.backend.feeders.HttpRealTimeFeeder;
import localization.backend.listeners.SerialListener;
import localization.backend.listeners.TcpListener;
import localization.backend.listeners.TestSerialListener;
import localization.backend.listeners.UdpListener;
import localization.backend.localizers.AveragedChipconLocalizer;
import localization.backend.localizers.GpsLocalizer;
import localization.backend.localizers.SimpleChipconLocalizer;
import localization.backend.storagers.CouchDbStorager;
import localization.backend.storagers.DebugCouchDbStorager;
import localization.backend.storagers.FileStorager;
import localization.backend.storagers.Storager;
import localization.backend.utils.Util;

/**
 * Main launcher for localization backend.
 * 
 * @author lorenzo grespan
 */
public class Main {

	/* the socket for the TCP listener */
	protected static int TCP_LISTEN_PORT = 9999;
	protected static int UDP_LISTEN_PORT = 9999;
	/* for the HTTP real-time feeding */
	protected static int FEED_PORT = 8080;
	/* for the serial port */
	protected static String SERIAL_PORT = "/dev/ttyS0";
	/* careful! different values will screw up eeeeverything */
	protected static int BAUD_RATE = 115200;
	/* for the chipcon algorithm */
	protected static int RSSI_OFFSET = 45;

	/* for the file debugger */
	protected static String logFileName = "tmpLog.txt";
	protected static int writingInterval = 1; /* in sec */
	/* for the couchdb database */
	protected static String COUCHDB_HOST = "localhost";
	protected static int COUCHDB_PORT = 5984;
	protected static String COUCHDB_NAME = "localization";
	protected static String DBGCOUCHDB_NAME = "localization_debug";

	/* used for the chipcon localizer */
	protected static int NUM_BLINDS = 42;
	protected static int NUM_REFNODES = 6;

	protected static String refnodeFileInfo = "refnodes.txt";
	protected static int REFNODE_GATEWAY = 1;

	/* Position types */
	public static enum PACKET_TYPE {
		VOID, CHIPCON, GPS
	};

	/**
	 * runs the system
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * change here to have debugging logs
		 */
		Util.DEBUG = false;

		/*
		 * load config parameters from file
		 * 
		 * TODO: pass the config param list to classes that will need it
		 * (instead of parsing the parameters and passing them back and forth)
		 */
		Properties config = loadConfigurations(args);

		/* prepare the main storager */
		// Storager couchDbStorager =
		// new CouchDbStorager(COUCHDB_HOST, COUCHDB_NAME, COUCHDB_PORT);
		/* prepare the debug storager(s) */
		// Storager debugCouchDbStorager =
		// new DebugCouchDbStorager(COUCHDB_HOST, DBGCOUCHDB_NAME,
		// COUCHDB_PORT);
		Storager fileStorager = new FileStorager(logFileName, writingInterval);
		Storager dbfileStorager =
				new FileStorager("db" + logFileName, writingInterval);

		/* prepare the feeder */
		Feeder httpRealTimeFeeder = new HttpRealTimeFeeder(FEED_PORT);

		/*
		 * run the system: first param: the main storager second param: the
		 * debug storager third param: the realtime feeder
		 */
		runChipcon(fileStorager, dbfileStorager, httpRealTimeFeeder);
		// runGps(couchDbStorager, fileStorager, httpRealTimeFeeder);

		/*
		 * system ready
		 */
		Util.dbg("System ready");
	}

	public static Properties loadConfigurations(String[] args) {
		Properties config = new Properties();

		FileInputStream in;

		/* load all properties from files supplied on the command line */
		for (int i = 0; i < args.length; i++) {
			try {
				in = new FileInputStream(args[i]);
				config.load(in);
				System.out.println("properties in file " + args[i]);
				config.list(System.out);
				in.close();
			} catch (FileNotFoundException e) {
				// not a valid file
				Util.dbg("Cannot find specified file " + args[i]);
				continue;
			} catch (IOException e) {
				// cannot load
				Util.dbg("Cannot read properties from file " + args[i]);
				continue;
			}
		}

		if (config.containsValue("TCP_LISTEN_PORT"))
			TCP_LISTEN_PORT =
					Integer.parseInt(config.getProperty("TCP_LISTEN_PORT"));
		if (config.containsValue("UDP_LISTEN_PORT"))
			UDP_LISTEN_PORT =
					Integer.parseInt(config.getProperty("UDP_LISTEN_PORT"));
		if (config.containsValue("FEED_PORT"))
			FEED_PORT = Integer.parseInt(config.getProperty("FEED_PORT"));
		if (config.containsValue("SERIAL_PORT"))
			SERIAL_PORT = config.getProperty("SERIAL_PORT");
		if (config.containsValue("BAUD_RATE"))
			BAUD_RATE = Integer.parseInt(config.getProperty("BAUD_RATE"));
		if (config.containsValue("RSSI_OFFSET"))
			RSSI_OFFSET = Integer.parseInt(config.getProperty("RSSI_OFFSET"));
		if (config.containsValue("logfile"))
			logFileName = config.getProperty("logfile");
		if (config.containsValue("DEBUG_WRITING_INTERVAL"))
			writingInterval =
					Integer.parseInt(config
							.getProperty("DEBUG_WRITING_INTERVAL"));
		if (config.containsValue("COUCHDB_HOST"))
			COUCHDB_HOST = config.getProperty("COUCHDB_HOST");
		if (config.containsValue("COUCHDB_PORT"))
			COUCHDB_PORT = Integer.parseInt(config.getProperty("COUCHDB_PORT"));
		if (config.containsValue("COUCHDB_NAME"))
			COUCHDB_NAME = config.getProperty("COUCHDB_NAME");
		if (config.containsValue("DEBUG_COUCHDB_NAME"))
			DBGCOUCHDB_NAME = config.getProperty("DEBUG_COUCHDB_NAME");
		if (config.containsValue("NUM_BLINDS"))
			NUM_BLINDS = Integer.parseInt(config.getProperty("NUM_BLINDS"));
		if (config.containsValue("NUM_REFNODES"))
			NUM_REFNODES = Integer.parseInt(config.getProperty("NUM_REFNODES"));
		if (config.containsValue("REFNODE_FILE"))
			refnodeFileInfo = config.getProperty("REFNODE_FILE");

		if (config.containsValue("REFNODE_GATEWAY"))
			REFNODE_GATEWAY =
					Integer.parseInt(config.getProperty("REFNODE_GATEWAY"));

		return config;
	}

	/**
	 * the serial/chipcon mechanism:
	 * 
	 * @param mainStorager
	 * @param debugStorager
	 * @param feeder
	 */
	public static void runChipcon(Storager mainStorager,
			Storager debugStorager, Feeder feeder) {

		/* 1. core */
		SerialCore serialCore = new SerialCore();

		/* 2. listener */
		serialCore
				.setListener(new TestSerialListener(NUM_BLINDS, NUM_REFNODES));
		// serialCore.setListener(new SerialListener(SERIAL_PORT, BAUD_RATE));

		/* 3. localizer */
		// serialCore.setLocalizer(new TestLocalizer());
		// serialCore.setLocalizer(new SimpleChipconLocalizer(RSSI_OFFSET,
		// PACKET_TYPE.CHIPCON));
		serialCore.setLocalizer(new AveragedChipconLocalizer(refnodeFileInfo,
				RSSI_OFFSET, PACKET_TYPE.CHIPCON, REFNODE_GATEWAY));

		/* 4. main storager */
		serialCore.setStorager(mainStorager);

		/* 5. debug storager */
		serialCore.setDebugStorager(debugStorager);

		/* 6. feeder */
		serialCore.setFeeder(feeder);
	}

	/**
	 * the gps mechanism
	 * 
	 * @param mainStorager
	 * @param debugStorager
	 * @param feeder
	 */
	public static void runGps(Storager mainStorager, Storager debugStorager,
			Feeder feeder) {

		/*
		 * testing the GPS/networking mechanism
		 */

		/* 1. core */
		NetworkCore networkCore = new NetworkCore();

		/* 2. listener */
		networkCore.setListener(new TcpListener(TCP_LISTEN_PORT));
		// networkCore.setListener(new UdpListener(UDP_LISTEN_PORT));

		/* 3. localizer */
		networkCore.setLocalizer(new GpsLocalizer(PACKET_TYPE.GPS));

		/* 4. regular storager */
		networkCore.setStorager(mainStorager);

		/* 5. debug storager */
		networkCore.setDebugStorager(debugStorager);

		/* 6. feeder */
		networkCore.setFeeder(feeder);
	}

}
