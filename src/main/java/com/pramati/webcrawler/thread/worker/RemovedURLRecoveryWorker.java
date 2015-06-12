package com.pramati.webcrawler.thread.worker;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pramati.webcrawler.common.RecoveryData;
import com.pramati.webcrawler.recovery.RemovedURLAccess;
import com.pramati.webcrawler.thread.trigger.Trigger;

public class RemovedURLRecoveryWorker implements Runnable{

	private RecoveryData recoveryData;
	private BlockingQueue<String> removedURLQueue;

	private Trigger trigger = null;
	
	FileWriter fileWriter = null;

	private static final String COMMA_DELIMITER = ",";
	RemovedURLAccess removedURLAccess;

	private static Log logger = LogFactory
			.getLog(RemovedURLRecoveryWorker.class);

	private void initialize() {
		if (recoveryData != null) {
			this.removedURLQueue = recoveryData.getRemovedURLQueue();
		}
		if (removedURLAccess != null) {
			fileWriter = removedURLAccess.getFileWriter();
		}
	}

	public void run() {
		String url = null;
		try {
			initialize();
			while (true) {
				if (trigger.isTaskComplete()) {
					logger.info(Thread.currentThread().getName()
							+ " isTaskComplete :" + trigger.isTaskComplete());
					if (removedURLAccess != null) {
						synchronized (removedURLAccess) {
							removedURLAccess.cleanUp();
						}
					}
					break;
				}
				url = fetchURL();
				if (url != null) {
					writeToFile(url);
				}
			}
		} catch (InterruptedException e) {

		}
	}

	private void writeToFile(String url) {

		synchronized (removedURLAccess) {
			if (!removedURLAccess.isStreamClosed()) {
				try {
					if (fileWriter != null) {
						fileWriter.append(url);
						fileWriter.append(COMMA_DELIMITER);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						fileWriter.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			removedURLAccess.notifyAll();
		}
	}

	private String fetchURL() throws InterruptedException {
		String url = null;
		if (removedURLQueue != null) {
			url = removedURLQueue.poll(5000, TimeUnit.MILLISECONDS);
		}
		return url;
	}
	
	public RecoveryData getRecoveryData() {
		return recoveryData;
	}

	public void setRecoveryData(RecoveryData recoveryData) {
		this.recoveryData = recoveryData;
	}

	public Trigger getTrigger() {
		return trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	public RemovedURLAccess getRemovedURLAccess() {
		return removedURLAccess;
	}

	public void setRemovedURLAccess(RemovedURLAccess removedURLAccess) {
		this.removedURLAccess = removedURLAccess;
	}
}
