package localization.backend.listeners;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Random;
import java.util.TooManyListenersException;

import javax.comm.*;

import localization.backend.utils.Util;

/**
 * This class is used to generate fake data at semi-regular intervals.
 * 
 * @author lorenzo grespan
 * 
 */
public class TestSerialListener extends SerialListener implements Runnable {

	Random rnd;
	int progressive = 0;
	/* as requested */
	int idBlind, idRefNode, rssiBlind, lqi, prog, lat, longit;

	/* max sleep between insertion of random values (in ms) */
	int SLEEP_MAX = 1000;
	PipedOutputStream out;
	
	private int NUM_BLINDS;
	private int NUM_REFNODES;

	public TestSerialListener(int NUM_BLINDS, int NUM_REFNODES) {
		rnd = new Random();
		this.NUM_BLINDS = NUM_BLINDS;
		System.out.println("test serial listener ready for " + NUM_BLINDS);
		this.NUM_REFNODES = NUM_REFNODES;
		idBlind = rnd.nextInt(NUM_BLINDS);
		idRefNode = rnd.nextInt(NUM_REFNODES);
		rssiBlind = rnd.nextInt();
		lqi = rnd.nextInt();
		prog = 0;
		lat = rnd.nextInt();
		longit = rnd.nextInt();

		System.out
				.println("Test Listener ready to generate random input strings");

		out = new PipedOutputStream();

		/* connect this output stream to the superclass inputstream */
		try {
			inputStream = new PipedInputStream(out);
			isReady = true;
		} catch (IOException e) {
			Util.dbg("Test Serial Listener IO exception");
		}

		new Thread(this).start();
	}

	public void run() {

		while (isReady) {

			/* writes 8 bytes into the serial listener */
			try {
				/* writes some random data into an input buffer */
				byte[] testRawData = generateTestRawData();
				out.write(testRawData, 0, testRawData.length);
				/* generate a new SerialPortEvent to test the superclass */
				super.serialEvent(new SerialPortEvent(new DummySerialPort(),
						SerialPortEvent.DATA_AVAILABLE, false, false));
			} catch (IOException e) {
				Util.dbg("IO Exception in writing data to serial listener");
				isReady = false;
			}

			/* give the debug some rest */
			try {
				Thread.sleep(rnd.nextInt(SLEEP_MAX));
			} catch (InterruptedException e) {
				/*
				 * break out of the while loop and stop running if thread is
				 * interrupted
				 */
				isReady = false;
			}
		}
	}

	/**
	 * This methods generates quickly random data to be fed through the serial
	 * event.
	 */
	private byte[] generateTestRawData() {
		byte[] result = new byte[7];
		result[0] = 3;
		result[1] = (byte)(rnd.nextInt(NUM_BLINDS)); //blindId
		result[2] = (byte)rnd.nextInt(33); // blind battery
		result[3] = (byte)(progressive++); // progressive
		result[4] = (byte)(rnd.nextInt(NUM_REFNODES)); // ref node
		result[5] = (byte)rnd.nextInt(120); //lqi
		result[6] = (byte)rnd.nextInt(230); // rssi

		return result;
	}

	/**
	 * Since we are extending the SerialListener class,
	 * we need the following dummy class.
	 * 
	 * Please ignore.
	 * 
	 * @author lorenzo grespan
	 * 
	 */
	private class DummySerialPort extends SerialPort {

		@Override
		public void addEventListener(SerialPortEventListener arg0)
				throws TooManyListenersException {

		}

		@Override
		public int getBaudRate() {

			return 0;
		}

		@Override
		public int getDataBits() {

			return 0;
		}

		@Override
		public int getFlowControlMode() {

			return 0;
		}

		@Override
		public int getParity() {

			return 0;
		}

		@Override
		public int getStopBits() {

			return 0;
		}

		@Override
		public boolean isCD() {

			return false;
		}

		@Override
		public boolean isCTS() {

			return false;
		}

		@Override
		public boolean isDSR() {

			return false;
		}

		@Override
		public boolean isDTR() {

			return false;
		}

		@Override
		public boolean isRI() {

			return false;
		}

		@Override
		public boolean isRTS() {

			return false;
		}

		@Override
		public void notifyOnBreakInterrupt(boolean arg0) {

		}

		@Override
		public void notifyOnCTS(boolean arg0) {

		}

		@Override
		public void notifyOnCarrierDetect(boolean arg0) {

		}

		@Override
		public void notifyOnDSR(boolean arg0) {

		}

		@Override
		public void notifyOnDataAvailable(boolean arg0) {

		}

		@Override
		public void notifyOnFramingError(boolean arg0) {

		}

		@Override
		public void notifyOnOutputEmpty(boolean arg0) {

		}

		@Override
		public void notifyOnOverrunError(boolean arg0) {

		}

		@Override
		public void notifyOnParityError(boolean arg0) {

		}

		@Override
		public void notifyOnRingIndicator(boolean arg0) {

		}

		@Override
		public void removeEventListener() {

		}

		@Override
		public void sendBreak(int arg0) {

		}

		@Override
		public void setDTR(boolean arg0) {

		}

		@Override
		public void setFlowControlMode(int arg0)
				throws UnsupportedCommOperationException {

		}

		@Override
		public void setRTS(boolean arg0) {

		}

		@Override
		public void setSerialPortParams(int arg0, int arg1, int arg2, int arg3)
				throws UnsupportedCommOperationException {

		}

		@Override
		public void disableReceiveFraming() {

		}

		@Override
		public void disableReceiveThreshold() {

		}

		@Override
		public void disableReceiveTimeout() {

		}

		@Override
		public void enableReceiveFraming(int arg0)
				throws UnsupportedCommOperationException {

		}

		@Override
		public void enableReceiveThreshold(int arg0)
				throws UnsupportedCommOperationException {

		}

		@Override
		public void enableReceiveTimeout(int arg0)
				throws UnsupportedCommOperationException {

		}

		@Override
		public int getInputBufferSize() {
			Util.dbg("asked for input buffer size");
			return 0;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			Util.dbg("asked for input stream");
			return inputStream;
		}

		@Override
		public int getOutputBufferSize() {

			return 0;
		}

		@Override
		public OutputStream getOutputStream() throws IOException {
			Util.dbg("asked for output stream");
			return null;
		}

		@Override
		public int getReceiveFramingByte() {

			return 0;
		}

		@Override
		public int getReceiveThreshold() {

			return 0;
		}

		@Override
		public int getReceiveTimeout() {

			return 0;
		}

		@Override
		public boolean isReceiveFramingEnabled() {

			return false;
		}

		@Override
		public boolean isReceiveThresholdEnabled() {

			return false;
		}

		@Override
		public boolean isReceiveTimeoutEnabled() {

			return false;
		}

		@Override
		public void setInputBufferSize(int arg0) {

		}

		@Override
		public void setOutputBufferSize(int arg0) {

		}

	}
}
