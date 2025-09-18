package com.springcore.javaconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;


public class Student {
	private Test test;

	public Student(Test test) {
		super();
		this.test=test;
		// TODO Auto-generated constructor stub
	}

	public Test getTest() {
		return test;
	}

	public void setTest(Test test) {
		this.test = test;
	}

	@Override
	public String toString() {
		return "Student [test=" + test + "]";
	}

	public void study() {
		this.test.display();
		System.out.println("Student is reading book!!");
	}

	
}
