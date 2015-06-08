package com.pramati.testcases;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Test;

import com.pramati.webcrawler.service.WebCrawlerService;

/**
 * Unit test for advanced web crawler
 */
public class WebCrawlerTest 
    extends TestCase
{
	private static WebCrawlerService service = null;
	
	private static String downloadFolder;
	private static Properties configFile = new Properties();
	private static InputStream inputStream = null;
	private static final String PROPERTY_FILE_NAME = "application.properties";
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public WebCrawlerTest( String testName )
    {
        super( testName );
        service = new WebCrawlerService();
        initialize();
    }
    
    public static void initialize(){
    	inputStream = WebCrawlerService.class.getClassLoader()
				.getResourceAsStream(PROPERTY_FILE_NAME);
		try {
			if (inputStream != null) {
				configFile.load(inputStream);
				downloadFolder = configFile
						.getProperty("download.directory");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    @Test
    public void testEmptyUrl(){
    	int actual  = -1;
    	if (downloadFolder != null && downloadFolder.length() > 0) {
			actual = service
					.downloadEmails("", downloadFolder);
		}
		assertEquals(-1, actual);
    }
    
    @Test
    public void testNullUrl(){
    	int actual  = -1;
    	if (downloadFolder != null && downloadFolder.length() > 0) {
			actual =  service.downloadEmails(null, downloadFolder);
    	}
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
    	int actual  = -1;
    	if (downloadFolder != null && downloadFolder.length() > 0) {
    		actual  = service.downloadEmails("invalid", downloadFolder);
    	}
    	assertEquals(-1, actual);
    }
    
    @Test
    public void testUrlDoesNotExist(){
    	int actual  = 1;
    	if (downloadFolder != null && downloadFolder.length() > 0) {
    		actual  = service.downloadEmails("http://tsgtsftag.com", downloadFolder);
    	}
    	assertEquals(1, actual);
    }
}
