package com.pramati.webcrawler.thread.worker;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pramati.webcrawler.common.RecoveryData;
import com.pramati.webcrawler.recovery.VisitedURLAccess;
import com.pramati.webcrawler.thread.trigger.Trigger;

public class VisitedURLRecoveryWorker implements Runnable{
	
	private RecoveryData recoveryData;
	private BlockingQueue<String> visitedUrls;
	
	FileWriter fileWriter = null;
	
	private Trigger trigger = null;
	
	private static final String COMMA_DELIMITER = ",";
	VisitedURLAccess visitedURLAccess;
	
	private static Log logger = LogFactory
			.getLog(VisitedURLRecoveryWorker.class);

	private void initialize() {
		if (recoveryData != null) {
			this.visitedUrls = recoveryData.getVisitedUrls();
		}
		if (visitedURLAccess != null) {
			fileWriter = visitedURLAccess.getFileWriter();
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
					if (visitedURLAccess != null) {
						synchronized (visitedURLAccess) {
							visitedURLAccess.cleanUp();
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

		synchronized (visitedURLAccess) {
			if (!visitedURLAccess.isStreamClosed()) {
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
			visitedURLAccess.notifyAll();
		}
	}

	private String fetchURL() throws InterruptedException {
		String url = null;
		if (visitedUrls != null) {
			url = visitedUrls.poll(5000, TimeUnit.MILLISECONDS);
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

	public VisitedURLAccess getVisitedURLAccess() {
		return visitedURLAccess;
	}

	public void setVisitedURLAccess(VisitedURLAccess visitedURLAccess) {
		this.visitedURLAccess = visitedURLAccess;
	}
}
