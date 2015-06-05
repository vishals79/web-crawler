# Generic Web Crawler in Java.

## Introduction

A simple generic web crawler to download emails from a website. 
* Crawl from the start point.
* A page is identified as an email on the basis of filter (A page should contain "From", "Date" and "Subject" for it to be considered as an email.) 
* Emails are downloaded in user provided path.
* If a page is redirected to another domain or outside of parent location, that page is not picked up for crawling.


## How to crawl


## Configuration


## Class Diagram

![alt text](https://github.com/vishals79/web-crawler/blob/master/etc/web-crawler-class-diagram.jpg "Class Diagram")

## Flow Diagram

![alt text](https://github.com/vishals79/web-crawler/blob/master/etc/main-flow.jpg "Main Flow")

![alt text](https://github.com/vishals79/web-crawler/blob/master/etc/url-processor.jpg "URL Processor")

![alt text](https://github.com/vishals79/web-crawler/blob/master/etc/invigilator.jpg "Invigilator")

