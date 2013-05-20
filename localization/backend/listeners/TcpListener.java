package localization.backend.listeners;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import localization.backend.utils.Position;
import localization.backend.utils.Util;

public class TcpListener extends Listener implements Runnable {

	Position newPosition = null;
	ServerSocket serverSocket = null;

	public TcpListener(int listeningPort) {
		try {
			serverSocket = new ServerSocket(listeningPort);
			System.out.println("TCP listening sensor ready on port: "
					+ listeningPort);
			isReady = true;

			new Thread(this).start();

		} catch (IOException e) {
			System.err
					.println("Can't open server socket, please kill other servers first");
		}

	}

	public void run() {
		Socket clientSocket = null;
		BufferedReader in = null;
		String inputLine;

		while (isReady) {

			try {
				clientSocket = serverSocket.accept();
				in = new BufferedReader(new InputStreamReader(clientSocket
								.getInputStream()));

				while (!in.ready()) {}
				inputLine = in.readLine();
				/* convert to byte array and feed to the core */
				core.newRawData(inputLine.getBytes(), System.currentTimeMillis());
				/* print for debug */
				Util.dbg(inputLine);
				in.close();
				clientSocket.close();

			} catch (IOException e) {
				Util.err("Accept failed"+e);
				continue; // considering break instead
			}
		}
	}

}
