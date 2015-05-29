package com.pramati.webcrawler.thread;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pramati.webcrawler.filter.Filter;
import com.pramati.webcrawler.parser.Parser;
import com.pramati.webcrawler.thread.trigger.Trigger;

public class ThreadManager {

	private Parser parser = null;
	private Filter filter = null;

	private BlockingQueue<String> urlsQueue = null;
	private Map<String, String> seenUrls = null;

	private int criteriaNo = 0;
	private String downloadPath = null;
	private String baseUrl = null;
	private String homeAddress = null;

	private int minThreadsCount = 0;
	private int maxThreadsCount = 0;
	private int waitTime = 0;

	private List<Thread> threadPool = new ArrayList<Thread>();

	private Trigger trigger = null;

	private static Log logger = LogFactory.getLog(ThreadManager.class);

	public ThreadManager() {
		urlsQueue = new LinkedBlockingDeque<String>();
		seenUrls = new HashMap<String, String>();
		trigger = new Trigger();

	}

	private int initialize() {
		Properties configFile = new Properties();
		InputStream inputStream = null;
		int response = 0;

		try {
			inputStream = ThreadManager.class.getClassLoader()
					.getResourceAsStream("thread.properties");
			if (inputStream != null) {
				configFile.load(inputStream);
				minThreadsCount = Integer.valueOf(configFile
						.getProperty("minThreads"));
				maxThreadsCount = Integer.valueOf(configFile
						.getProperty("maxThreads"));
				waitTime = Integer.valueOf(configFile.getProperty("waitTime"));

				logger.info("Task Manager initialized with minThreadsCount = "
						+ minThreadsCount + " maxThreadsCount = "
						+ maxThreadsCount + " waitTime = " + waitTime);

				urlsQueue.put(getBaseUrl());
				seenUrls.put(getBaseUrl(), getBaseUrl());

				response = createInitialThreadPool();
				if (response != 0) {
					logger.info(" Thread pool successfully created ");
				} else {
					logger.error(" Error occurred while thread pool creation");
					return 0;
				}

				return 1;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return 0;
	}

	public int startProcess() {
		long start = System.currentTimeMillis();
		long end = 0;
		long timeTaken = 0;
		int response = 0;

		try {
			response = initialize();
			if (response != 0) {
				logger.info(" Thread Manager sucessfully initialized ");
				response = startThreads();
				if (response != 0) {
					logger.info(" Threads sucessfully started ");
					response = startInvigilator();
					if (response != 0) {
						logger.info(" Invigilator sucessfully started ");
					} else {
						logger.error(" Error occurred while starting Invigilator ");
						return 0;
					}
				} else {
					logger.error(" Error occurred while starting threads ");
					return 0;
				}
			} else {
				logger.error(" Error occurred while initialization ");
				return 0;
			}

			for (Thread thread : threadPool) {
				thread.join();
			}

			end = System.currentTimeMillis();
			timeTaken = end - start;
			logger.info("Time taken by Thread manager = " + timeTaken);

			System.out.println(" Time Taken :" + timeTaken);

			return 1;

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private int createInitialThreadPool() {
		int ret = 1;
		Thread thread = null;
		for (int count = 0; count < minThreadsCount; count++) {
			thread = createTask();
			if (thread != null) {
				threadPool.add(thread);
			} else {
				logger.error(" Error occurred while thread creation ");
				return 0;
			}
		}

		return ret;
	}

	private Thread createTask() {

		Thread thread = null;
		URLProcesser urlProcessor = new URLProcesser(urlsQueue, parser, filter,
				criteriaNo, downloadPath, baseUrl, homeAddress, seenUrls,
				trigger);
		thread = new Thread(urlProcessor);

		return thread;
	}

	private int startThreads() {
		if (threadPool != null && threadPool.size() > 0) {
			for (Thread thread : threadPool) {
				thread.start();
			}
		} else {
			logger.error(" Error occurred while starting threads. Thread pool is either Null or Empty ");
			return 0;
		}
		return 1;
	}

	private int startInvigilator() {
		int previousSize = 0;
		int currentSize = 0;
		boolean emptyQueue = false;
		try {
			if (urlsQueue != null) {
				while (true) {
					previousSize = urlsQueue.size();
					Thread.currentThread();
					Thread.sleep(waitTime);
					currentSize = urlsQueue.size();
					if (currentSize == 0 && previousSize == 0) {
						emptyQueue = isTaskComplete();
						if (emptyQueue) {
							break;
						}
					}
					if ((currentSize - previousSize) > 50
							&& threadPool.size() < maxThreadsCount) {
						startNewThread();
					}
				}
				if (emptyQueue) {
					trigger.setTaskComplete(true);
					logger.info(" Task Completed. Trigger generated to stop threads ");
					System.out.println(Thread.currentThread().getName()
							+ " changed isTaskComplete :"
							+ trigger.isTaskComplete());
				}

				return 1;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return 0;
	}

	private boolean isTaskComplete() {
		boolean ret = true;
		int sizeOfQueue = 0;
		try {
			for (int count = 0; count < 5; count++) {
				sizeOfQueue = urlsQueue.size();
				if (sizeOfQueue == 0) {
					Thread.currentThread();
					Thread.sleep(1000);
					;
				} else {
					ret = false;
					break;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return ret;
	}

	private void startNewThread() {

		Thread newThread = createTask();
		newThread.start();
		threadPool.add(newThread);

	}

	public Parser getParser() {
		return parser;
	}

	public void setParser(Parser parser) {
		this.parser = parser;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public int getCriteriaNo() {
		return criteriaNo;
	}

	public void setCriteriaNo(int criteriaNo) {
		this.criteriaNo = criteriaNo;
	}

	public String getDownloadPath() {
		return downloadPath;
	}

	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}

}