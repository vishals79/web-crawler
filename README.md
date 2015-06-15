# Generic Web Crawler in Java.

## Introduction

A simple generic web crawler to download emails from a website. 
* Crawl from the start point.
* A page is identified as an email on the basis of filter (A page should contain "From", "Date" and "Subject" for it to be considered as an email.)
* Emails are downloaded in user provided path.
* If a page is redirected to another domain or outside of parent location, that page is not picked up for crawling.
* Program can survive internet connection loss and can resume from last run.


## How to crawl
* Checkout the project, compile and create a jar.
* Run the jar e.g. 
	java -jar jar-name.jar start-point path-to-download-emails
	e.g. java -jar advancedCrawler-0.0.1.one-jar.jar http://test-site.com /home/user/DownloadEmails



## Configuration

	application.properties
	## Input
	* base.URL = URL to download emails.
	* download.directory = Directory path to save emails.
	* recovery.dir = Directory name to save backup files to recover from internet connection loss and resume from last run.
	
	
	## Thread
	* min.threads = minimum number of threads.
	* max.threads = maximum number of threads.
	* wait.time = Delay time. Invigilator will wait for 'waitTime' secs to check the difference in url queue size.

	log4j.properties
	* log4j.appender.file.File= Path to save log.


## Class Diagram

![alt text](https://github.com/vishals79/web-crawler/blob/master/etc/web-crawler-class-diagram.jpg "Class Diagram")

## Flow Diagram

### Main Flow

![alt text](https://github.com/vishals79/web-crawler/blob/master/etc/main-flow.jpg "Main Flow")

### URLProcessor Manager Flow

![alt text](https://github.com/vishals79/web-crawler/blob/master/etc/url-processor-manager.jpg "URL Processor Manager")

### URLProcessor Worker Flow

![alt text](https://github.com/vishals79/web-crawler/blob/master/etc/url-processor-worker.jpg "URL Processor Worker")

### Invigilator Flow

![alt text](https://github.com/vishals79/web-crawler/blob/master/etc/invigilator.jpg "Invigilator")

### Recovery Manager Flow

![alt text](https://github.com/vishals79/web-crawler/blob/master/etc/recovery-manager.jpg "Recovery Manager")

### Recovery Worker Flow

![alt text](https://github.com/vishals79/web-crawler/blob/master/etc/recovery-worker.jpg "Recovery Worker")

