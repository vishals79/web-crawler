package com.pramati.webcrawler.thread;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.pramati.webcrawler.common.InputData;
import com.pramati.webcrawler.common.URLProcessorData;
import com.pramati.webcrawler.factory.BeanFactory;
import com.pramati.webcrawler.thread.trigger.Trigger;

public class URLProcessorManager implements Runnable{

	private BlockingQueue<String> urlsQueue = null;
	private Map<String, String> seenUrls = null;

	private String baseUrl = null;

	private int minThreadsCount = 0;
	private int maxThreadsCount = 0;
	private int waitTime = 0;
	
	private InputData inputData;
	private URLProcessorData urlProcessorData;

	private List<Thread> threadPool = new ArrayList<Thread>();

	private Trigger trigger = null;

	private static Log logger = LogFactory.getLog(ThreadManager.class);

	public URLProcessorManager() {
		super();
	}

	private int initialize() {
		Properties configFile = new Properties();
		InputStream inputStream = null;
		int response = 0;

		try {
			inputStream = URLProcessorManager.class.getClassLoader()
					.getResourceAsStream("application.properties");
			if (inputStream != null) {
				configFile.load(inputStream);
				minThreadsCount = Integer.valueOf(configFile
						.getProperty("min.threads"));
				maxThreadsCount = Integer.valueOf(configFile
						.getProperty("max.threads"));
				waitTime = Integer.valueOf(configFile.getProperty("wait.time"));

				logger.info("Task Manager initialized with minThreadsCount = "
						+ minThreadsCount + " maxThreadsCount = "
						+ maxThreadsCount + " waitTime = " + waitTime);
				
				if (inputData != null) {
					this.baseUrl = inputData.getBaseUrl();
				}
				
				if (urlProcessorData != null) {
					urlsQueue = urlProcessorData.getUrlsQueue();
					seenUrls = urlProcessorData.getSeenUrls();
				}
				
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

	public void run() {
		long start = System.currentTimeMillis();
		long end = 0;
		long timeTaken = 0;
		int response = 0;

		try {
			response = initialize();
			if (response != 0) {
				logger.info(" Thread Manager successfully initialized ");
				response = startThreads();
				if (response != 0) {
					logger.info(" Threads successfully started ");
					response = startInvigilator();
					if (response != 0) {
						logger.info(" Invigilator successfully started ");
					} else {
						logger.error(" Error occurred while starting Invigilator ");
						return;
					}
				} else {
					logger.error(" Error occurred while starting threads ");
					return;
				}
			} else {
				logger.error(" Error occurred while initialization ");
				return;
			}

			for (Thread thread : threadPool) {
				thread.join();
			}
			
			end = System.currentTimeMillis();
			timeTaken = end - start;
			System.out.println("Time taken by URLProcessorManager = " + timeTaken);
			logger.info("Time taken by URLProcessorManager = " + timeTaken);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
		ApplicationContext context = BeanFactory.getContext();
		Thread thread = null;
		URLProcessorWorker urlProcessor = (URLProcessorWorker) context
				  .getBean("urlProcessorWorker", new Object[]{inputData});
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
					logger.info(Thread.currentThread().getName()
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

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public Trigger getTrigger() {
		return trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	public InputData getInputData() {
		return inputData;
	}

	public void setInputData(InputData inputData) {
		this.inputData = inputData;
	}

	public URLProcessorData getUrlProcessorData() {
		return urlProcessorData;
	}

	public void setUrlProcessorData(URLProcessorData urlProcessorData) {
		this.urlProcessorData = urlProcessorData;
	}

}