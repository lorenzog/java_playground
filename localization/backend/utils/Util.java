package localization.backend.utils;

public class Util {

	public static boolean DEBUG = true;

	public static void dbg(String message) {
		if (DEBUG) {
			String callingClassName = sun.reflect.Reflection.getCallerClass(2)
					.getName();
			System.out.println("DBG: " + callingClassName + ": " + message);
		}
	}

	public static void err(String message) {
			String callingClassName = sun.reflect.Reflection.getCallerClass(2)
					.getName();
			System.err.println("ERR: " + callingClassName + ": " + message);
	}

	/**
	 * thanks to: the Java Trails
	 * http://java.sun.com/docs/books/tutorial/i18n/text/string.html
	 * 
	 * @param the
	 *            byte to transform
	 * @return the byte hex value
	 */
	public static String byteToHex(byte b) {
		// Returns hex String representation of byte b
		char hexDigit[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		char[] array = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };
		return new String(array);
	}

}

/*
 * gps.java questo programma simula la ricezione dati del gps crea un nuovo file
 * gps.java e pasta il codice seguente compilalo ed eseguilo semplicemnte con
 * javac gps.java e java gps
 * 
 * non sapevo se inserirlo come file aggiuntivo al progetto avrebbe causato
 * problemi visto che contiene un mail
 */
/*
 * import java.io.*; import java.lang.*; import java.net.*;
 * 
 * public class gps {
 * 
 * public static void main(String args[]) { String gps_header = "ciasdasdaoo";
 * 
 * try { Socket skt = new Socket("localhost", 4444); PrintWriter out = new
 * PrintWriter(skt.getOutputStream(), true); out.print(gps_header); out.close();
 * skt.close(); }catch(Exception e) { System.out.println(e); } } }
 */
