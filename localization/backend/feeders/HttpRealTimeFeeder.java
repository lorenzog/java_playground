package localization.backend.feeders;

import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import localization.backend.utils.Position;
import localization.backend.utils.Util;

import com.google.gson.Gson;
import com.sun.net.httpserver.*;

/**
 * Use this class to feed the latest received positions to a 
 * remote client (ideally, a webapp). 
 * 
 * This class creates a tiny, lightweit webserver using com.sun.net.httpserver.*
 * in order to serve GET requests done from a client. Returned data
 * is in JSON notation.
 * 
 * @author lorenzo grespan
 *
 */
public class HttpRealTimeFeeder extends Feeder {

	Map<Long, Position> locations = new HashMap<Long, Position>();

	HttpServer server = null;

	public HttpRealTimeFeeder(int httpPort) {
		if ( prepareServer(httpPort) ) {
			server.start();
			System.out.println("Real-Time feeder HTTP server started on port: "
					+ httpPort);
			isReady = true;
		}
		else
			Util.err("Cannot start Real-Time HTTP server");
	}

	private boolean prepareServer(int httpPort) {

		try {
			server = HttpServer.create(new InetSocketAddress(httpPort), 0);
		} catch (BindException e) {
			Util.err("Kill other local webservers on port "
					+ httpPort + " please");
			return false;
		} catch (IOException e) {
			// e.printStackTrace();
			Util.err("I/O exception on RT http server");
			return false;
		}

		if (server == null) {
			Util.err("Server not initialised");
			return false;
		}
		server.createContext("/", new MyHandler());
		server.setExecutor(null); // creates a default executor
		return true;
	}

	/*
	 * updates the location of a given ID
	 */
	@Override
	public synchronized void newData(Position newPosition) {
		 synchronized (locations) {
			 locations.put(newPosition.getGlobalId(), newPosition);
		}
	}

	/**
	 * An inner anonymous class to serve as the executor for the http GET
	 * requests.
	 * 
	 * @author lorenzo
	 * 
	 */
	class MyHandler implements HttpHandler {

		MyHandler() {
			super();
		}

		public void handle(HttpExchange exchange) throws IOException {

			/* to determine the command */
			String requestMethod = exchange.getRequestMethod();
			
			Gson gson = new Gson();

			if (requestMethod.equalsIgnoreCase("GET")) {
				Util.dbg("http server got GET");

				// SET the response headers, no cotent-length
				Headers responseHeaders = exchange.getResponseHeaders();
				responseHeaders.set("Content-Type", "text/plain");

				String response;

				synchronized (locations) {
					/* convert to JSON to be sent via http */
					response = gson.toJson(locations);
				}

				Util.dbg("http sending: " + response);

				// must happen before getResponseBody
				exchange.sendResponseHeaders(200, response.length());
				// send the response body
				OutputStream responseBody = exchange.getResponseBody();

				responseBody.write(response.getBytes());

				// close exchange and consume data from input
				responseBody.close();
			}

			if (requestMethod.equalsIgnoreCase("PUT")) {
				Util.dbg("http server got PUT");

				/* get the body */
				//URI uri = exchange.getRequestURI();
				// set response headers (do we?)
				// Headers responseHeaders = exchange.getResponseHeaders();
				// set 200 - OK; set to 0 for chunked sending
				exchange.sendResponseHeaders(200, -1);
			}
		}
	}


}
