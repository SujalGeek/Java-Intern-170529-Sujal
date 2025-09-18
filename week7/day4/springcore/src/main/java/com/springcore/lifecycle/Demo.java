package com.springcore.lifecycle;

public class Demo {
private double price;

public double getPrice() {
	return price;
}

public void setPrice(double price) {
	System.out.println("Setting Price");
	this.price = price;
}

public Demo() {
	super();
	// TODO Auto-generated constructor stub
}

@Override
public String toString() {
	return "Demo [price=" + price + "]";
}

public void hey() {
	System.out.println("Inside init method: Hey Demo one!!");
}
public void byy() {
	System.out.println("Inside destroy method: Byy from Demo one!!");
}
}
