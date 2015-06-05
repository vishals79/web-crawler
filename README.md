# Generic Web Crawler in Java.

## Introduction

A simple generic web crawler to download emails from a website. 
* Crawl from the start point.
* A page is identified as an email on the basis of filter (A page should contain "From", "Date" and "Subject" for it to be considered as an email.) 
* Emails are downloaded in user provided path.
* If a page is redirected to another domain or outside of parent location, that page is not picked up for crawling.


## How to crawl
* Checkout the project, compile and create a jar.
* Run the jar e.g. 
	java -jar jar-name.jar start-point path-to-download-emails
	e.g. java -jar advancedCrawler-0.0.1.one-jar.jar http://test-site.com /home/user/DownloadEmails



## Configuration

	thread.properties
	* minThreads = minimum number of threads
	* maxThreads = maximum number of threads
	* waitTime = Delay time. Invigilator will wait for 'waitTime' secs to check the difference in url queue size.

	log4j.properties
	* log4j.appender.file.File= Path to save log.


## Class Diagram

![alt text](https://github.com/vishals79/web-crawler/blob/master/etc/web-crawler-class-diagram.jpg "Class Diagram")

## Flow Diagram

### Main Flow

![alt text](https://github.com/vishals79/web-crawler/blob/master/etc/main-flow.jpg "Main Flow")

### URLProcessor Flow

![alt text](https://github.com/vishals79/web-crawler/blob/master/etc/url-processor.jpg "URL Processor")

### Invigilator Flow

![alt text](https://github.com/vishals79/web-crawler/blob/master/etc/invigilator.jpg "Invigilator")

