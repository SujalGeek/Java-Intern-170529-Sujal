package com.springcore.javaconfig;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Demo {

	public static void main(String[] args) {
	
	ApplicationContext context = new AnnotationConfigApplicationContext(JavaConfig.class);
	Student student1 = context.getBean("getStudent",Student.class);
	/*ystem.out.println(student1);*/
	student1.study();
//	context.cl
	}

}
