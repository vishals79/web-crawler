package com.pramati.webcrawler.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pramati.webcrawler.downloader.Downloader;
import com.pramati.webcrawler.downloader.DownloaderImpl;
import com.pramati.webcrawler.filter.Filter;
import com.pramati.webcrawler.parser.Parser;
import com.pramati.webcrawler.parser.WebCrawlerParserImpl;
import com.pramati.webcrawler.pojo.FilterCriteria;

/**
 * 
 * Web Crawler Service
 *
 */
public class WebCrawlerService {

	private Map<String, String> visitedUrls = null;
	private Map<String, String> fetchedUrls = null;
	private String baseUrl = null;
	private FilterCriteria filterCriteriaObj = null;
	private int criteriaNo = 0;
	private String downloadPath = null;
	private String homeAddress = null;
	private Filter filter= null;
	
	//private static Log logger = LogFactory.getLog(WebCrawlerService.class);

	public WebCrawlerService(Filter filter) {
		super();
		this.filter = filter;
	}

	public static void main(String[] args) {
		System.out.println("-----Starting Crawler-----");
		//logger.info("-----Starting Crawler-----");

		Properties configFile = new Properties();
		InputStream inputStream = null;

		try {
			
			ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
			WebCrawlerService webCrawlerService = (WebCrawlerService) context.getBean("webCrawler");
			
			inputStream = WebCrawlerService.class.getClassLoader()
					.getResourceAsStream("input.properties");
			if (inputStream != null) {
				configFile.load(inputStream);
				webCrawlerService.downloadEmails(
						configFile.getProperty("baseUrl"),
						configFile.getProperty("downloadFolder"));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("-----Process Complete-----");
	}

	public void downloadEmails(String baseUrl, String downloadPath) {
		Object[] arrayOfinputs = new Object[1];

		try {
			if (isEmpty(baseUrl)) {
				System.out
						.println("Could not proceed further because URL is not present");
			}
			if (isEmpty(downloadPath)) {
				System.out
						.println("Please provide the path to download emails.");
			}

			initializeProcess(arrayOfinputs, baseUrl, "NO_FILTERING_REQUIRED",
					downloadPath);
			iterateFetchedUrls();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void downloadEmailsForYear(String baseUrl, String downloadPath,
			String year) {
		this.baseUrl = baseUrl;
		Object[] arrayOfinputs = new Object[1];

		try {
			if (isEmpty(baseUrl)) {
				System.out
						.println("Could not proceed further because URL is not present");
				return;
			}
			if (isEmpty(downloadPath)) {
				System.out.println("Please provide the path to save emails.");
				return;
			}
			if (isEmpty(year)) {
				System.out
						.println("Please provide the year for downloading emails.");
				return;
			}

			initializeProcess(arrayOfinputs, baseUrl, "FILTER_BASED_ON_YEAR",
					downloadPath);
			iterateFetchedUrls();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initializeProcess(Object[] arrayOfinputs, String baseUrl,
			String filterCriteriaText, String downloadPath) throws IOException {
		Properties configFile = new Properties();
		InputStream inputStream = null;
		if (arrayOfinputs != null) {
			this.baseUrl = baseUrl;
			visitedUrls = new HashMap<String, String>();
			fetchedUrls = new HashMap<String, String>();
			fetchedUrls.put(baseUrl, baseUrl);

			inputStream = WebCrawlerService.class.getClassLoader()
					.getResourceAsStream("filterCriteria.properties");
			if (inputStream != null) {
				configFile.load(inputStream);
				criteriaNo = Integer.valueOf(configFile
						.getProperty(filterCriteriaText));
			}

			this.downloadPath = downloadPath;
			this.homeAddress = getHomeAddress(baseUrl);

			if (criteriaNo == 1) {
				filterCriteriaObj = new FilterCriteria();
				filterCriteriaObj.setForYear((String) arrayOfinputs[0]);
			}
		}
	}

	private void iterateFetchedUrls() {
		int size = 0;
		Set<String> setOfKeys = null;
		String url = null;
		if (fetchedUrls != null) {
			size = fetchedUrls.size();
			while (size > 0) {
				setOfKeys = fetchedUrls.keySet();
				for (String key : setOfKeys) {
					url = key;
					if (!visitedUrls.containsKey(url)) {
						processReqForUrl(url);
						visitedUrls.put(url, url);
					}
					break;
				}
				fetchedUrls.remove(url);
				size = fetchedUrls.size();
			}
		}
	}

	private void processReqForUrl(String url) {
		Document responseObj = null;
		Elements links = null;
		Parser parser = null;
		Downloader downloader = null;

		if (isNotEmpty(url)) {
			responseObj = getResponse(url);
			if (responseObj != null) {
				parser = new WebCrawlerParserImpl();
				links = parser.parseForAnchors(responseObj);
				populateUrlsCollection(url,links);
				if (filter.isEmail(responseObj)) {
					downloader = new DownloaderImpl();
					downloader.downloadEmail(downloadPath, responseObj);
				}
			} else {
				System.out.println(" Could not get any response from the url "
						+ url);
			}
		}
	}

	private void populateUrlsCollection(String url,Elements links) {
		String href = null;
		String addUrl = null;
		if (links != null && links.size() > 0) {
			if (fetchedUrls != null) {
				for (Element link : links) {
					href = link.attr("href");
					if (href.startsWith("http://")) {
						if (href.startsWith(baseUrl)) {
							fetchedUrls.put(href, href);
						}
					} else {
						addUrl = createUrl(url, href);
						fetchedUrls.put(addUrl,
								addUrl);
					}
				}
			}
		}
	}
	
	private String createUrl(String url,String href){
		StringBuilder newUrl = new StringBuilder();
		if(isNotEmpty(url)){
			if(href.indexOf('/') == 0){
				newUrl = newUrl.append(homeAddress).append(href);
			}else{
				newUrl = newUrl.append(url.substring(0, url.lastIndexOf('/')+1)).append(href);
			}
		}
		return newUrl.toString();
	}
	
	private String getHomeAddress(String baseUrl){
		StringBuilder homeAddress = new StringBuilder();
		String tempString = null;
		if(isNotEmpty(baseUrl)){
			tempString =  baseUrl.replace("http://", "");
			if(tempString.indexOf('/') != -1){
				homeAddress.append("http://").append(tempString.substring(0, tempString.indexOf('/')));
			}else{
				homeAddress.append("http://").append(tempString);
			}
		}
		return homeAddress.toString();
	}

	private Document getResponse(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
		}
		return doc;
	}

	private static boolean isNotEmpty(String str) {
		if (str != null && str.length() > 0) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean isEmpty(String str) {
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

	/*
	 * private void execute(Set<String> urls, Document responseObj,
	 * FilterCriteria filterCriteria, int criteriaNo) { String url = null; if
	 * (urls != null && urls.size() > 0) { Iterator<String> itr =
	 * urls.iterator(); if (itr.hasNext()) { url = (String) itr.next(); if
	 * (!visitedUrls.containsKey(url)) { responseObj = getResponse(url); if
	 * (responseObj != null) { processResponse(responseObj, url, filterCriteria,
	 * criteriaNo);
	 * 
	 * } else { System.out .println(" Could not get any response from the url "
	 * + baseUrl); } visitedUrls.put(url, url); } fetchedUrls.remove(url); } } }
	 * 
	 * private void processResponse(Document responseObj, String url,
	 * FilterCriteria filterCriteria, int criteriaNo) { Elements anchors = null;
	 * WebCrawlerParser parser = null; boolean isEmail = false;
	 * WebCrawlerFilterImpl filter = null; Downloader downloader = null; String
	 * href = null; String downloadUrl = null;
	 * 
	 * if (responseObj != null) { if (criteriaNo == 0 || criteriaNo == 1) {
	 * parser = new WebCrawlerParser(); anchors =
	 * parser.parseForAnchors(responseObj); filter = new WebCrawlerFilterImpl();
	 * for (Element anchor : anchors) { isEmail = filter.isEmail(responseObj);
	 * href = anchor.attr("href"); if (isEmail) { downloader = new Downloader();
	 * downloadUrl = modifyUrlForDownload(url); if (isNotEmpty(downloadUrl)) {
	 * downloader.downloadEmails(downloadEmailsPath, downloadUrl.concat(href),
	 * filterCriteria, criteriaNo); } } else { if (href.startsWith("http://")) {
	 * if (href.startsWith(baseUrl)) { fetchedUrls.add(href); } } else {
	 * fetchedUrls.add(baseUrl.concat(href)); } } } } } }
	 * 
	 * private String modifyUrlForDownload(String url) { String downloadUrl =
	 * null; if (isNotEmpty(url)) { downloadUrl = url.substring(0,
	 * url.lastIndexOf('/') + 1); } return downloadUrl; }
	 */

}
