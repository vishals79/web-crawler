package com.pramati.webcrawler.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pramati.webcrawler.pojo.FilterCriteria;
import com.pramati.webcrawler.thread.ThreadManager;

/**
 * 
 * Web Crawler Service
 *
 */
public class WebCrawlerTestService {
	
	private String baseUrl = null;
	private FilterCriteria filterCriteriaObj = null;
	private int criteriaNo = 0;
	private String downloadPath = null;
	private String homeAddress = null;
	private ThreadManager manager = null;
	
	private static Log logger = LogFactory.getLog(WebCrawlerService.class);

	public WebCrawlerTestService() {
		super();
	}

	public static void main(String[] args) {
		logger.info("-----Starting Crawler-----");
		System.out.println("-----Starting Crawler-----");
		long start = System.currentTimeMillis();
		long end = 0;

		Properties configFile = new Properties();
		InputStream inputStream = null;
		int processRetVal = 0;
		
		try {
			WebCrawlerService webCrawlerService = new WebCrawlerService();
			
			inputStream = WebCrawlerService.class.getClassLoader()
					.getResourceAsStream("secondInput.properties");
			if (inputStream != null) {
				configFile.load(inputStream);
				processRetVal = webCrawlerService.downloadEmails(
						configFile.getProperty("baseUrl"),
						configFile.getProperty("downloadFolder"));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		if(processRetVal > 0){
			end = System.currentTimeMillis();
			logger.info("-----Process Complete-----");
			System.out.println("-----Process Complete-----");
			System.out.println("-----Time Taken = "+(end-start));
		}else{
			logger.info("-----Error occured while downloading emails-----");
		}
		
	}

	public int downloadEmails(String baseUrl, String downloadPath) {
		Object[] arrayOfinputs = new Object[1];

		try {
			if (isEmpty(baseUrl)) {
				logger.info("Could not proceed further because URL is not present");
				return -1;
			}
			if (isEmpty(downloadPath)) {
				logger.info("Please provide the path to download emails.");
				return -1;
			}

			initialize(arrayOfinputs, baseUrl, "NO_FILTERING_REQUIRED",
					downloadPath);
			if(manager != null){
				manager.startProcess();
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

			initialize(arrayOfinputs, baseUrl, "FILTER_BASED_ON_YEAR",
					downloadPath);
			if(manager != null){
				manager.startProcess();
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void initialize(Object[] arrayOfinputs, String baseUrl,
			String filterCriteriaText, String downloadPath) throws IOException {
		Properties configFile = new Properties();
		InputStream inputStream = null;
		if (arrayOfinputs != null) {
			
			ApplicationContext context = new ClassPathXmlApplicationContext(
					"spring.xml");
			
			this.setBaseUrl(baseUrl);

			inputStream = WebCrawlerService.class.getClassLoader()
					.getResourceAsStream("filterCriteria.properties");
			if (inputStream != null) {
				configFile.load(inputStream);
				criteriaNo = Integer.valueOf(configFile
						.getProperty(filterCriteriaText));
			}

			this.setDownloadPath(downloadPath);
			this.homeAddress = getHomeAddress(baseUrl);
			
			manager = (ThreadManager) context
					.getBean("manager", new Object[]{criteriaNo,downloadPath,baseUrl,homeAddress});
			
			if (criteriaNo == 1) {
				filterCriteriaObj = new FilterCriteria();
				filterCriteriaObj.setForYear((String) arrayOfinputs[0]);
			}
		}
	}

	private String getHomeAddress(String baseUrl) {
		StringBuilder homeAddress = new StringBuilder();
		String tempString = null;
		if (isNotEmpty(baseUrl)) {
			tempString = baseUrl.replace("http://", "");
			if (tempString.indexOf('/') != -1) {
				homeAddress.append("http://").append(
						tempString.substring(0, tempString.indexOf('/')));
			} else {
				homeAddress.append("http://").append(tempString);
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
