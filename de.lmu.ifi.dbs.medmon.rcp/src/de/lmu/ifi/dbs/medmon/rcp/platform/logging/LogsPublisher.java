package de.lmu.ifi.dbs.medmon.rcp.platform.logging;

import java.util.logging.Logger;

public class LogsPublisher {

	private final Logger logger = Logger.getLogger(LogsPublisher.class.getName());
	private Thread taskThread;

	public void start() {
		getTaskThread().start();
		System.out.println("Log Publisher Started.");
	}

	public void stop() {
		getTaskThread().interrupt();
		System.out.println("Log Publisher Stopped.");
	}

	protected Thread getTaskThread() {
		if (taskThread == null) {
			taskThread = new Thread(new Runnable() {
				public void run() {
					while (true) {
						// Stay alive log!
						// logger.info("Publishing Log");

						try {
							Thread.sleep(3000);
						} catch (InterruptedException ex) {
							return;
						}
					}
				}
			});
		}
		return taskThread;
	}

}
