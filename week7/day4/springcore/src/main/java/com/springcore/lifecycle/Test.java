package com.springcore.lifecycle;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.springframework.context.support.AbstractApplicationContext;

public class Test {

	public static void main(String[] args) {
		
	AbstractApplicationContext context = new ClassPathXmlApplicationContext("com/springcore/lifecycle/config.xml");
	Demo d1 = (Demo) context.getBean("d1");
	System.out.println(d1.getPrice());	
	// registering the shutdown hook 
	context.registerShutdownHook();
	
	System.out.println();
	Demo_Interface d2 = (Demo_Interface) context.getBean("d2");
	System.out.println(d2.getPrice());
	
	
	Example e1 = (Example) context.getBean("example");
	System.out.println(e1);
	
	}

}
