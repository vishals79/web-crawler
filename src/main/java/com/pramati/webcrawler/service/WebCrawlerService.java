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
import com.pramati.webcrawler.pojo.FilterCriteria;

/**
 * 
 * Web Crawler Service
 *
 */
public class WebCrawlerService {
	
	private final String HTTP = "http://";
	private final String HREF = "href";

	private Map<String, String> visitedUrls = null;
	private Map<String, String> fetchedUrls = null;
	private String baseUrl = null;
	private FilterCriteria filterCriteriaObj = null;
	private int criteriaNo = 0;
	private String downloadPath = null;
	private String homeAddress = null;
	private Filter filter = null;
	private Parser parser = null;

	private static Log logger = LogFactory.getLog(WebCrawlerService.class);

	public WebCrawlerService() {
		super();
		initialize();
	}
	
	private int initialize(){
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext(
					"spring.xml");
			this.filter = (Filter) context
					.getBean("filter");
			this.parser = (Parser) context
					.getBean("parser");
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
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

			preProcess(arrayOfinputs, baseUrl, "NO_FILTERING_REQUIRED",
					downloadPath);
			iterateFetchedUrls();
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
		this.baseUrl = baseUrl;
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

			preProcess(arrayOfinputs, baseUrl, "FILTER_BASED_ON_YEAR",
					downloadPath);
			iterateFetchedUrls();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void preProcess(Object[] arrayOfinputs, String baseUrl,
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
		Downloader downloader = null;

		if (isNotEmpty(url)) {
			responseObj = getResponse(url);
			if (responseObj != null) {
				links = parser.parseForAnchors(responseObj);
				populateUrlsCollection(url, links);
				if (filter.isEmail(responseObj)) {
					if (criteriaNo == 0) {
						downloader = new DownloaderImpl();
						downloader.downloadEmail(downloadPath, responseObj);
					}else if(criteriaNo == 1){
						downloader = new DownloaderImpl();
						downloader.downloadEmail(downloadPath, responseObj);
					}
				}
			} else {
				logger.info(" Could not get any response from the url " + url);
			}
		}
	}

	private void populateUrlsCollection(String url, Elements links) {
		String href = null;
		String addUrl = null;
		if (links != null && links.size() > 0) {
			if (fetchedUrls != null) {
				for (Element link : links) {
					href = link.attr(HREF);
					if (href.startsWith(HTTP)) {
						if (href.startsWith(baseUrl)) {
							fetchedUrls.put(href, href);
						}
					} else {
						addUrl = createUrl(url, href);
						if (addUrl.startsWith(baseUrl)) {
							fetchedUrls.put(addUrl, addUrl);
						}
					}
				}
			}
		}
	}

	private String createUrl(String url, String href) {
		StringBuilder newUrl = new StringBuilder();
		if (isNotEmpty(url)) {
			if (href.indexOf('/') == 0) {
				newUrl = newUrl.append(homeAddress).append(href);
			} else {
				newUrl = newUrl.append(
						url.substring(0, url.lastIndexOf('/') + 1))
						.append(href);
			}
		}
		return newUrl.toString();
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

	private Document getResponse(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
		}
		return doc;
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

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public Parser getParser() {
		return parser;
	}

	public void setParser(Parser parser) {
		this.parser = parser;
	}
}
