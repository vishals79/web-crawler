package com.pramati.testcases;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.pramati.webcrawler.service.WebCrawlerService;

import junit.framework.TestCase;

/**
 * Unit test for advanced web crawler
 */
public class WebCrawlerTest 
    extends TestCase
{
	private static WebCrawlerService service = null;
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public WebCrawlerTest( String testName )
    {
        super( testName );
        service = new WebCrawlerService();
    }
    
    @BeforeClass
    public static void init(){
    	System.out.println("Before Class");
    }
    
    @Test
    public void testEmptyUrl(){
    	int actual  = service.downloadEmails("", "/home/vishals/DownloadEmails/");
    	assertEquals(-1, actual);
    }
    
    @Test
    public void testNullUrl(){
    	int actual  = service.downloadEmails(null, "/home/vishals/DownloadEmails/");
    	assertEquals(-1, actual);
    }
    
    @Test
    public void testEmptyPath(){
    	int actual  = service.downloadEmails("http://mail-archives.apache.org/mod_mbox/maven-users/", "");
    	assertEquals(-1, actual);
    }
    
    @Test
    public void testNullPath(){
    	int actual  = service.downloadEmails("http://mail-archives.apache.org/mod_mbox/maven-users/", null);
    	assertEquals(-1, actual);
    }
    
    @Test
    public void testNullArg(){
    	int actual  = service.downloadEmails(null, null);
    	assertEquals(-1, actual);
    }
    
    @Test
    public void testEmptyArg(){
    	int actual  = service.downloadEmails("", "");
    	assertEquals(-1, actual);
    }
    
    @Test
    public void testInvalidProtocolUrl(){
    	int actual  = service.downloadEmails("invalid", "/home/vishals/DownloadEmails/");
    	assertEquals(-1, actual);
    }
    
    @Test
    public void testUrlDoesNotExist(){
    	int actual  = service.downloadEmails("http://tsgtsftag.com", "/home/vishals/DownloadEmails/");
    	assertEquals(1, actual);
    }
    
    /*@Test
    public void testInvalidPath(){
    	int actual  = service.downloadEmails("http://mail-archives.apache.org/mod_mbox/maven-users/", "rdrqds/tft/stfft");
    	assertEquals(1, actual);
    }*/
    
    /* *//**
     * @return the suite of tests being tested
     *//*
    public static Test suite()
    {
        return new TestSuite( WebCrawlerTest.class );
    }*/
}
