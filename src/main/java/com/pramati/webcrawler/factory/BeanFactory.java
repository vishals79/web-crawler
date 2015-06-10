package com.pramati.webcrawler.factory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanFactory {
	private static ApplicationContext context;
	
	static {
		setContext();
	}
	
	public static ApplicationContext getContext(){
		return context;
	}
	
	private static void setContext(){
		context = new ClassPathXmlApplicationContext(
				"spring.xml");
	}
}
