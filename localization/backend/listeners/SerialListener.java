package localization.backend.listeners;

import java.io.*;
import java.util.*;
import javax.comm.*;

import localization.backend.utils.Util;

/**
 * This class listens for events on the serial port; when data is available, the
 * registered serialEvent handler is invoked. To register a serialEvent handler,
 * use static method serialPort.addEventListener();
 * 
 * @author lorenzo grespan, alberto valente
 * 
 */
public class SerialListener extends Listener implements SerialPortEventListener {

	/* protected so it can be tested from TestSerialListener */
	protected InputStream inputStream;

	/**
	 * this constructor is protected because: the TestSerialListener extends
	 * this SerialListener class in order to run tests.
	 * 
	 * Try commenting out this constructor to see the error.
	 */
	protected SerialListener() {
		Util.dbg("Serial Listener empty constructor");
	}

	SerialPort serialPort;

	public SerialListener(String requestedPort, int requestedBaudRate) {

		CommPortIdentifier portId;
		serialPort = null;

		// should be Enumeration<CommPortIdentifier> portList ..?
		/* get the list of available ports */
		Enumeration<CommPortIdentifier> portList = CommPortIdentifier
				.getPortIdentifiers();

		while (portList.hasMoreElements()) {

			/* get the next port */
			portId = portList.nextElement();

			/* if it's a serial port */
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {

				/* and if it's the port we were looking for */
				if (portId.getName().equals(requestedPort)) {

					System.out
							.println("Serial listening sensor ready on port: "
									+ requestedPort);
					try {
						/* define object */
						serialPort = (SerialPort) portId.open(
								"SerialListenerApp", 2000);
						inputStream = serialPort.getInputStream();
						serialPort.addEventListener(this);

					} catch (PortInUseException e) {
						Util.err("Requested port already in use");
						continue;
					} catch (IOException e) {
						Util.err("IOException on input stream request");
						continue;
					} catch (TooManyListenersException e) {
						Util.err("Event listener already associated "
								+ "with port");
						continue;
					} catch (RuntimeException e) {
						Util.err("Serial port already in use");
						break;
					}

					/* if we reach here the serial port has been initialized */
					serialPort.notifyOnDataAvailable(true);

					try {
						serialPort.setSerialPortParams(requestedBaudRate,
								SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
								SerialPort.PARITY_NONE);

						/* if we reach here everything went fine */
						isReady = true;

					} catch (UnsupportedCommOperationException e) {
						System.err
								.println("Setting of parameters not possible; "
										+ "reverting to old parameters");
					}

					/* we found the port we need, bail out */
					break;

				} else {
					/*
					 * those are not the droids you are looking for: keep
					 * searching
					 */
					System.out.println("Requested port not found; instead, "
							+ "found port: " + portId.getName());
				}
			} /* end while (port is type serial) */
		} /* end while(portlist has more elements */
	} /* end constructor */

	/**
	 * this event is invoked every time the serial has some data
	 */
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {

		/* discard those events */
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			Util.err("Serial Port err");
			break;

		/* this is the interesting one */
		case SerialPortEvent.DATA_AVAILABLE:

			byte[] readBuffer = new byte[7];
			int numBytes = 0;

			try {
				while (inputStream.available() > 0) {
					/*
					 * this method will block until 7 bytes have been read or
					 * until the end of the stream is reached
					 */
					numBytes = inputStream.read(readBuffer);
					if (numBytes < 0) {
						Util.err("End of stream reached");
						break;
					} else {
/*						System.out.println("BlindId= " + readBuffer[1] + " BlindBatt= "
								+ readBuffer[2] + " Prog= " + readBuffer[3]
								+ " RefId= " + readBuffer[4] + " LQI= "
								+ readBuffer[5] + " RSSI= "
								+ (readBuffer[6] & 0xFF));*/

						/* pass the raw data to the core */
						core.newRawData(readBuffer, System.currentTimeMillis());
					}
				}

			} catch (IOException e) {
				Util.err("IOException in serial port event");
				e.printStackTrace();
			}
			break;
		} /* end switch */
	} /* end serialEvent method */

}
