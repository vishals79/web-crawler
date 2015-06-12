package com.pramati.webcrawler.thread.manager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pramati.webcrawler.common.InputData;
import com.pramati.webcrawler.recovery.MainQueueAccess;

public class ThreadManager implements Runnable{


	private URLProcessorManager urlProcessorManager;
	private RecoveryManager recoveryManager;
	private InputData inputData;
	
	private static final String COMMA_DELIMITER = ",";
	
	private static Log logger = LogFactory.getLog(ThreadManager.class);

	public ThreadManager(InputData inputData) {
		super();
		this.inputData = inputData;
	}

	public void run() {
		Thread urlProcessorMangrThread = null;
		Thread recoveryManagerThread = null;
		
		try {
			if (urlProcessorManager != null && recoveryManager != null && inputData != null) {
				
				urlProcessorManager.setInputData(inputData);
				
				updateStatusForRecovery("N",inputData.getBaseUrl());
				
				urlProcessorMangrThread = new Thread(urlProcessorManager);
				urlProcessorMangrThread.start();
				
				logger.info("URLProcessorManager started");
				
				recoveryManagerThread = new Thread(recoveryManager);
				recoveryManagerThread.start();
				
				logger.info("Recovery Manager started");
				
				urlProcessorMangrThread.join();
				
				logger.info("URLProcessorManager completed task.");
				
				recoveryManagerThread.join();
				updateStatusForRecovery("Y",inputData.getBaseUrl());
				
				logger.info("Recovery Manager completed task.");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private FileWriter getFileWriter(String fileKey){
		
		Properties configFile;
		InputStream inputStream;
		String recoveryFileName;
		String recoveryDirName;
		File file = null;
		File dir = null;
		String userCurrentDir = null;
		StringBuilder temp = new StringBuilder();
		FileWriter fileWriter = null;
		
		inputStream = MainQueueAccess.class.getClassLoader()
				.getResourceAsStream("application.properties");
		if (inputStream != null) {
			try {
				configFile = new Properties();
				configFile.load(inputStream);
				
				recoveryDirName = configFile.getProperty("recovery.dir");
				recoveryFileName = configFile
						.getProperty(fileKey);
				
				userCurrentDir = System.getProperty("user.dir");
				
				temp = temp.append(userCurrentDir).append("/").append(recoveryDirName);
				dir = new File(temp.toString());
				if (!dir.exists()) {
					dir.mkdir();
				}
				temp.setLength(0);
				temp = temp.append(userCurrentDir).append("/").append(recoveryDirName).append("/").append(recoveryFileName);

				file = new File(temp.toString());
				file.createNewFile();
				
				fileWriter = new FileWriter(temp.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileWriter;
	}
	
	private void updateStatusForRecovery(String status,String baseUrl){
		FileWriter fileWriter = null;
		fileWriter = getFileWriter("recovery.status.file");
		if(fileWriter != null){
			try {
				fileWriter.append("isDownloadComplete=");
				fileWriter.append(status);
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append("baseUrl=");
				fileWriter.append(baseUrl);
			} catch (IOException e) {
			}finally {
				try {
					fileWriter.flush();
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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

	/**
	 * @return the recoveryManager
	 */
	public RecoveryManager getRecoveryManager() {
		return recoveryManager;
	}

	/**
	 * @param recoveryManager the recoveryManager to set
	 */
	public void setRecoveryManager(RecoveryManager recoveryManager) {
		this.recoveryManager = recoveryManager;
	}
}