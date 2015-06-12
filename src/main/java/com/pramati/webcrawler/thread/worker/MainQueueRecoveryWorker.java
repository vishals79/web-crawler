package com.pramati.webcrawler.thread.worker;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pramati.webcrawler.common.RecoveryData;
import com.pramati.webcrawler.recovery.MainQueueAccess;
import com.pramati.webcrawler.thread.trigger.Trigger;

public class MainQueueRecoveryWorker implements Runnable{
	
	private RecoveryData recoveryData;
	private BlockingQueue<String> adddedURLQueue;
	
	FileWriter fileWriter;
			
	private Trigger trigger = null;
	
	private static final String COMMA_DELIMITER = ",";
	MainQueueAccess mainQueueAccess;
	
	private static Log logger = LogFactory.getLog(MainQueueRecoveryWorker.class);
	
	private void initialize(){
		if(recoveryData != null){
			this.adddedURLQueue = recoveryData.getAdddedURLQueue();
		}
		if (mainQueueAccess != null) {
			fileWriter = mainQueueAccess.getFileWriter();
		}
	}
	
	public void run() {
		String url = null;
		try{
			initialize();
			while(true){
				if(trigger.isTaskComplete()){
					logger.info(Thread.currentThread().getName()+" isTaskComplete :"+trigger.isTaskComplete());
					if (mainQueueAccess != null) {
						synchronized (mainQueueAccess) {
							mainQueueAccess.cleanUp();
						}
					}
					break;
				}
				url = fetchURL();
				if(url != null){
					writeToFile(url);
				}
			}
		}catch(InterruptedException e){
				
		}
	}
	
	private void writeToFile(String url) {

		synchronized (mainQueueAccess) {
			if (!mainQueueAccess.isStreamClosed()) {
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
			mainQueueAccess.notifyAll();
		}
	}
	
	private String fetchURL() throws InterruptedException{
		String url = null;
		if(adddedURLQueue != null){
			url = adddedURLQueue.poll(5000, TimeUnit.MILLISECONDS);
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

	public MainQueueAccess getMainQueueAccess() {
		return mainQueueAccess;
	}

	public void setMainQueueAccess(MainQueueAccess mainQueueAccess) {
		this.mainQueueAccess = mainQueueAccess;
	}
}
