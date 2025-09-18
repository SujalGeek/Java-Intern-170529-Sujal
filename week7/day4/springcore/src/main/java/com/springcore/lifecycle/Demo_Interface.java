package com.springcore.lifecycle;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class Demo_Interface implements InitializingBean, DisposableBean{
private double Price;

public double getPrice() {
	return Price;
}

public void setPrice(double price) {
	Price = price;
}

public Demo_Interface() {
	super();
	// TODO Auto-generated constructor stub
}

@Override
public String toString() {
	return "Demo_Interface [Price=" + Price + "]";
}

@Override
public void afterPropertiesSet() throws Exception {
//	init
	System.out.println("Init method using interface");
}

@Override
public void destroy() throws Exception {
	System.out.println("Destroy method using the interface");
	
}

}
