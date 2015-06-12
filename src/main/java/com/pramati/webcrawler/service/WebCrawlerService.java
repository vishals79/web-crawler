package com.pramati.webcrawler.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.pramati.webcrawler.common.InputData;
import com.pramati.webcrawler.common.URLProcessorData;
import com.pramati.webcrawler.factory.BeanFactory;
import com.pramati.webcrawler.pojo.FilterCriteria;
import com.pramati.webcrawler.recovery.MainQueueAccess;
import com.pramati.webcrawler.thread.manager.ThreadManager;

/**
 * 
 * Web Crawler Service
 *
 */
public class WebCrawlerService {
	
	private String baseUrl = null;
	private FilterCriteria filterCriteriaObj = null;
	private int criteriaNo = 0;
	private String downloadPath = null;
	private String homeAddress = null;
	private ThreadManager manager = null;
	
	private static final String HTTP = "http://";
	private static final String HTTPS = "https://";
	private String PROTOCOL;
	
	private static final String COMMA_DELIMITER = ",";
	private static final String EQUAL_DELIMITER = "=";
	
	private static Log logger = LogFactory.getLog(WebCrawlerService.class);

	public WebCrawlerService() {
		super();
	}

	public static void main(String[] args) {
		logger.info("-----Starting Crawler-----");
		System.out.println("Web Crawler started");
		System.out.println("Crawling...");
		long start = System.currentTimeMillis();
		long end = 0;

		int processRetVal = 0;
		
		try {
			WebCrawlerService webCrawlerService = new WebCrawlerService();
			
			if(args != null && args.length >= 2){
				processRetVal = webCrawlerService.downloadEmails(args[0],args[1]);
			}else{
				System.out.println("Please provide 'Start Point' and 'Path to download emails'");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		if(processRetVal > 0){
			end = System.currentTimeMillis();
			logger.info("-----Process Complete-----");
			System.out.println("Process Complete");
			System.out.println("Time Taken = "+(end-start));
		}else{
			logger.info("Error occured while downloading emails");
		}
		
	}

	public int downloadEmails(String baseUrl, String downloadPath) {
		Object[] arrayOfinputs = new Object[1];
		Thread threadManager = null;
		try {
			if (isEmpty(baseUrl)) {
				logger.info("Could not proceed further because URL is not present");
				return -1;
			}
			if (!(baseUrl.startsWith(HTTP) || baseUrl.startsWith(HTTPS))) {
				logger.info("Invalid Url");
				return -1;
			}
			if (isEmpty(downloadPath)) {
				logger.info("Please provide the path to download emails.");
				return -1;
			}
			

			initialize(arrayOfinputs, baseUrl, "filtering.not.required",
					downloadPath);
			if(manager != null){
				threadManager = new Thread(manager);
				threadManager.start();
				threadManager.join();
			}
			return 1;

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return -1;
	}

	public void downloadEmailsForYear(String baseUrl, String downloadPath,
			String year) {
		this.setBaseUrl(baseUrl);
		Object[] arrayOfinputs = new Object[1];
		Thread threadManager = null;
		
		try {
			if (isEmpty(baseUrl)) {
				logger.info("Could not proceed further because URL is not present");
				return;
			}
			if (isEmpty(downloadPath)) {
				logger.info("Please provide the path to save emails.");
				return;
			}
			if (isEmpty(year)) {
				logger.info("Please provide the year for downloading emails.");
				return;
			}

			initialize(arrayOfinputs, baseUrl, "filter.based.on.year",
					downloadPath);
			if(manager != null){
				threadManager = new Thread(manager);
				threadManager.start();
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isRecoveryRequired(InputData inputData){
		boolean isRecoveryRequired = false;
		String fileName = getStatusFileName();
		if(inputData != null && isNotEmpty(fileName)){
			isRecoveryRequired = isLastRunInComplete(fileName, inputData);
		}
		return isRecoveryRequired;
	}
	
	private boolean isLastRunInComplete(String fileName,InputData inputData){
		boolean isTaskIncomplete = false;
		BufferedReader fileReader = null;
		String line = "";
		String[] tokens = null;
		String status = null;
		String baseURL = null;
		File file = null;
		try {
			if(isNotEmpty(fileName) && inputData != null){
				file = new File(fileName);
				if(file.exists()){
					fileReader = new BufferedReader(new FileReader(fileName));
					while ((line = fileReader.readLine()) != null) {
						tokens = line.split(COMMA_DELIMITER);
						if (tokens.length > 1) {
							status = tokens[0].split(EQUAL_DELIMITER)[1];
							baseURL = tokens[1].split(EQUAL_DELIMITER)[1];
							break;
						}
					}
					if(isNotEmpty(status) && isNotEmpty(baseURL)){
						if("N".equalsIgnoreCase(status) && baseURL.equalsIgnoreCase(inputData.getBaseUrl())){
							isTaskIncomplete = true;
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if (fileReader != null) {
					fileReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return isTaskIncomplete;
	}
	
	private String getStatusFileName(){
		
		Properties configFile;
		InputStream inputStream;
		String recoveryFileName;
		String recoveryDirName;
		String userCurrentDir = null;
		StringBuilder temp = new StringBuilder();
		String fileName = null;
		
		inputStream = WebCrawlerService.class.getClassLoader()
				.getResourceAsStream("application.properties");
		if (inputStream != null) {
			try {
				configFile = new Properties();
				configFile.load(inputStream);
				
				recoveryDirName = configFile.getProperty("recovery.dir");
				recoveryFileName = configFile
						.getProperty("recovery.status.file");
				
				userCurrentDir = System.getProperty("user.dir");
				
				temp = temp.append(userCurrentDir).append("/").append(recoveryDirName).append("/").append(recoveryFileName);

				fileName = temp.toString();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileName;
	}
	
	
	private void initialize(Object[] arrayOfinputs, String baseUrl,
			String filterCriteriaText, String downloadPath) throws IOException {
		Properties configFile = new Properties();
		InputStream inputStream = null;
		InputData inputData = null;
		ApplicationContext context = BeanFactory.getContext();
		boolean isRecoveryRequired = false;
		if (arrayOfinputs != null) {
			
			this.setBaseUrl(baseUrl);

			inputStream = WebCrawlerService.class.getClassLoader()
					.getResourceAsStream("application.properties");
			if (inputStream != null) {
				configFile.load(inputStream);
				criteriaNo = Integer.valueOf(configFile
						.getProperty(filterCriteriaText));
			}

			this.setDownloadPath(downloadPath);
			this.homeAddress = getHomeAddress(baseUrl);
			inputData = (InputData) context
						.getBean("inputData", new Object[]{criteriaNo,downloadPath,baseUrl,homeAddress});
			isRecoveryRequired = isRecoveryRequired(inputData);
			
			manager = (ThreadManager) context
					  .getBean("manager", new Object[]{inputData});
			
			if(isRecoveryRequired){
				if(manager != null && manager.getUrlProcessorManager() != null){
					populateDataForRecovery(manager.getUrlProcessorManager().getUrlProcessorData());
				}
			}
			
			if (criteriaNo == 1) {
				filterCriteriaObj = new FilterCriteria();
				filterCriteriaObj.setForYear((String) arrayOfinputs[0]);
			}
		}
	}
	
	private int populateDataForRecovery(URLProcessorData data){
		Map<String, String> visitedURLMap = null;
		Map<String, String> queueMap = null;
		
		BlockingQueue<String> urlsQueue = null;
		
		try {
			if (data != null) {
				urlsQueue = data.getUrlsQueue();
				queueMap = getUrlsMap();
				visitedURLMap = getVisitedURLMap();
				if (queueMap != null && visitedURLMap != null) {
					for(Map.Entry<String, String> entry : queueMap.entrySet()){
						urlsQueue.put(entry.getValue());
					}
					data.setUrlsQueue(urlsQueue);
					data.setSeenUrls(visitedURLMap);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 1;
	}
	
	private Map<String, String> getVisitedURLMap(){
		Map<String, String> visitedURLMap = null;
		BufferedReader reader = null;
		String[] tokens = null;
		String line = "";
		reader = getFileReader("recovery.visited.url.queue.file");
		if(reader != null){
			visitedURLMap = new HashMap<String,String>();
			try {
				while ((line = reader.readLine()) != null) {
					tokens = line.split(COMMA_DELIMITER);
					for(String url : tokens){
						visitedURLMap.put(url, url);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return visitedURLMap;
	}
	
	private Map<String, String> getUrlsMap(){
		Map<String, String> queueMap = null;
		BufferedReader mainQueue = null;
		BufferedReader removedUrls = null;
		String[] tokens = null;
		String line = "";
		mainQueue = getFileReader("recovery.main.queue.file");
		if(mainQueue != null){
			queueMap = new HashMap<String,String>();
			try {
				while ((line = mainQueue.readLine()) != null) {
					tokens = line.split(COMMA_DELIMITER);
					for(String url : tokens){
						queueMap.put(url, url);
					}
				}
				removedUrls = getFileReader("recovery.removed.url.queue.file");
				if(removedUrls != null){
					while ((line = removedUrls.readLine()) != null) {
						tokens = line.split(COMMA_DELIMITER);
						for(String url : tokens){
							queueMap.remove(url);
						}
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					if (mainQueue != null) {
						mainQueue.close();
					}
					if (removedUrls != null) {
						removedUrls.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return queueMap;
	}
	
	private BufferedReader getFileReader(String fileKey) {
		BufferedReader fileReader = null;
		Properties configFile;
		String userCurrentDir = null;
		InputStream inputStream;
		String recoveryFileName;
		String recoveryDirName;
		StringBuilder temp = new StringBuilder();
		File file = null;

		inputStream = MainQueueAccess.class.getClassLoader()
				.getResourceAsStream("application.properties");
		try {
			if (inputStream != null) {
				configFile = new Properties();
				configFile.load(inputStream);
				
				recoveryDirName = configFile.getProperty("recovery.dir");
				recoveryFileName = configFile
						.getProperty(fileKey);
				
				userCurrentDir = System.getProperty("user.dir");
				temp = temp.append(userCurrentDir).append("/").append(recoveryDirName).append("/").append(recoveryFileName);
				
				file = new File(temp.toString());
				if(file.exists()){
					fileReader = new BufferedReader(new FileReader(temp.toString()));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileReader;
	}

	private String getHomeAddress(String baseUrl) {
		StringBuilder homeAddress = new StringBuilder();
		String tempString = null;
		if (isNotEmpty(baseUrl)) {
			if(baseUrl.startsWith(HTTP)){
				PROTOCOL = HTTP;
			}else{
				PROTOCOL = HTTPS;
			}
			tempString = baseUrl.replace(PROTOCOL, "");
			if (tempString.indexOf('/') != -1) {
				homeAddress.append(PROTOCOL).append(
						tempString.substring(0, tempString.indexOf('/')));
			} else {
				homeAddress.append(PROTOCOL).append(tempString);
			}
		}
		return homeAddress.toString();
	}

	private boolean isNotEmpty(String str) {
		if (str != null && str.length() > 0) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isEmpty(String str) {
		if (str == null) {
			return true;
		} else {
			if (str.length() == 0) {
				return true;
			} else {
				return false;
			}
		}
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getDownloadPath() {
		return downloadPath;
	}

	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}
}
