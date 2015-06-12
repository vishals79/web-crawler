package com.pramati.webcrawler.thread.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.pramati.webcrawler.factory.BeanFactory;
import com.pramati.webcrawler.thread.trigger.Trigger;
import com.pramati.webcrawler.thread.worker.MainQueueRecoveryWorker;
import com.pramati.webcrawler.thread.worker.RemovedURLRecoveryWorker;
import com.pramati.webcrawler.thread.worker.VisitedURLRecoveryWorker;

public class RecoveryManager implements Runnable{
	
	private int minThreadsCount = 0;
	private int maxThreadsCount = 0;
	private int waitTime = 0;
	
	private List<Thread> threadPool = new ArrayList<Thread>();

	private Trigger trigger = null;

	private static Log logger = LogFactory.getLog(RecoveryManager.class);
	
	private int initialize() {
		Properties configFile = new Properties();
		InputStream inputStream = null;
		int response = 0;

		try {
			inputStream = RecoveryManager.class.getClassLoader()
					.getResourceAsStream("application.properties");
			if (inputStream != null) {
				configFile.load(inputStream);
				minThreadsCount = Integer.valueOf(configFile
						.getProperty("recovery.min.threads"));
				maxThreadsCount = Integer.valueOf(configFile
						.getProperty("recovery.max.threads"));
				waitTime = Integer.valueOf(configFile.getProperty("recovery.wait.time"));

				logger.info("Recovery Manager initialized with minThreadsCount = "
						+ minThreadsCount + " maxThreadsCount = "
						+ maxThreadsCount + " waitTime = " + waitTime);
				
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
			logger.info("Time taken by Recovery Manager = " + timeTaken);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private int createInitialThreadPool() {
		int ret = 1; 
		Thread thread = null;
		for (int count = 0; count < minThreadsCount; count++) {
			thread = createMQRecWorker();
			if (thread != null) {
				threadPool.add(thread);
			} else {
				logger.error(" Error occurred while thread creation ");
				return 0;
			}
		}
		
		for (int count = 0; count < minThreadsCount; count++) {
			thread = createRemovedURLRecWorker();
			if (thread != null) {
				threadPool.add(thread);
			} else {
				logger.error(" Error occurred while thread creation ");
				return 0;
			}
		}
		
		for (int count = 0; count < minThreadsCount; count++) {
			thread = createVisitedURLRecWorker();
			if (thread != null) {
				threadPool.add(thread);
			} else {
				logger.error(" Error occurred while thread creation ");
				return 0;
			}
		}
		return ret;
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
	
	private Thread createMQRecWorker() {
		ApplicationContext context = BeanFactory.getContext();
		Thread thread = null;
		MainQueueRecoveryWorker worker = (MainQueueRecoveryWorker) context
				  .getBean("mainQueueRecoveryWorker");
		thread = new Thread(worker);
		return thread;
	}
	
	private Thread createRemovedURLRecWorker() {
		ApplicationContext context = BeanFactory.getContext();
		Thread thread = null;
		RemovedURLRecoveryWorker worker = (RemovedURLRecoveryWorker) context
				  .getBean("removedURLRecoveryWorker");
		thread = new Thread(worker);
		return thread;
	}
	
	private Thread createVisitedURLRecWorker() {
		ApplicationContext context = BeanFactory.getContext();
		Thread thread = null;
		VisitedURLRecoveryWorker worker = (VisitedURLRecoveryWorker) context
				  .getBean("visitedURLRecoveryWorker");
		thread = new Thread(worker);
		return thread;
	}
	
	/**
	 * @return the threadPool
	 */
	public List<Thread> getThreadPool() {
		return threadPool;
	}
	/**
	 * @param threadPool the threadPool to set
	 */
	public void setThreadPool(List<Thread> threadPool) {
		this.threadPool = threadPool;
	}
	/**
	 * @return the trigger
	 */
	public Trigger getTrigger() {
		return trigger;
	}
	/**
	 * @param trigger the trigger to set
	 */
	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}
	
}
