package localization.backend.storagers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import localization.backend.utils.Util;

public class FileStorager extends Storager {

	private BufferedWriter outFile;

	/* interval between flush(), in seconds */

	public FileStorager(String logFileName, int writingInterval) {
		try {
			outFile = new BufferedWriter(new FileWriter(logFileName));
			isReady = true;
			Util.dbg("File storager ready");
		} catch (IOException e) {
			Util.err("Requested file cannot be opened, cannot be "
					+ "created or is a directory with same name");
		}

		/* schedule execution every writingInterval secs */
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				if (isReady)
					try {
						outFile.flush();
					} catch (IOException e) {
						System.err
								.println("Cannot flush output file in storager");
					}
			}
		}, writingInterval, writingInterval * 1000L);
	}

	/**
	 * when receiving data to be saved the output file
	 * is synchronized between threads
	 */
	@Override
	public void newData(byte[] rawData, long timestamp) {
		if (!isReady)
			return;

		String data = new String();

		for (int i = 0; i < rawData.length; i++)
			data += "0x" + Util.byteToHex(rawData[i]) + " ";
		Util.dbg("got new data: (hex)" + data);
		data = "" + timestamp + ": " + data.trim() + "\n";

		synchronized(outFile) {
			try {
				outFile.write(data);
				outFile.flush();
			} catch (IOException e) {
				Util.err("IO error on file");
				isReady = false;
			}
		}
	}

}
