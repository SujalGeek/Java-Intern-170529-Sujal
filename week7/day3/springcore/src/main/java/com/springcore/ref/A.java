package com.springcore.ref;

public class A {
	private int x;
	private B obj;
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public B getObj() {
		return obj;
	}
	public void setObj(B obj) {
		this.obj = obj;
	}
	
	public A() {
		super();
	}
	@Override
	public String toString() {
		return "A [x=" + x + ", obj=" + obj + "]";
	}
	
}
