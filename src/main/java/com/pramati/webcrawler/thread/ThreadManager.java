package com.pramati.webcrawler.thread;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pramati.webcrawler.common.InputData;

public class ThreadManager implements Runnable{


	private URLProcessorManager urlProcessorManager;
	private InputData inputData;

	private static Log logger = LogFactory.getLog(ThreadManager.class);

	public ThreadManager(InputData inputData) {
		super();
		this.inputData = inputData;
	}

	public void run() {
		Thread urlProcessorMangrThread = null;
		try {
			if (urlProcessorManager != null) {
				if(inputData != null){
					urlProcessorManager.setInputData(inputData);
				}
				urlProcessorMangrThread = new Thread(urlProcessorManager);
				urlProcessorMangrThread.start();
				System.out.println("URLProcessorManager started");
				logger.info("URLProcessorManager started");

				urlProcessorMangrThread.join();
				System.out.println("URLProcessorManager completed task.");
				logger.info("URLProcessorManager completed task.");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public URLProcessorManager getUrlProcessorManager() {
		return urlProcessorManager;
	}

	public void setUrlProcessorManager(URLProcessorManager urlProcessorManager) {
		this.urlProcessorManager = urlProcessorManager;
	}

	public InputData getInputData() {
		return inputData;
	}

	public void setInputData(InputData inputData) {
		this.inputData = inputData;
	}
}