package localization.backend.listeners;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

import localization.backend.utils.Position;
import localization.backend.utils.Util;

public class UdpListener extends Listener implements Runnable {

	Position newPosition = null;
	DatagramSocket serverSocket = null;

	public UdpListener(int listeningPort) {
		try {
			serverSocket = new DatagramSocket(listeningPort);
			System.out.println("UDP listening sensor ready on port: "
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
		byte[] inputLine;

		while (isReady) {

			try {
				byte[] buf = new byte[256];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				serverSocket.receive(packet);
				//clientSocket = serverSocket.accept();
				//in = new BufferedReader(new InputStreamReader(clientSocket
								//.getInputStream()));

				//while (!in.ready()) {}
				//inputLine = in.readLine();
				inputLine = packet.getData();
				/* convert to byte array and feed to the core */
				core.newRawData(inputLine, System.currentTimeMillis());
				/* print for debug */
				Util.dbg(new String(inputLine));
				//in.close();
				//clientSocket.close();

			} catch (IOException e) {
				Util.err("Accept failed"+e);
				continue; // considering break instead
			}
		}
		serverSocket.close();
	}

}
