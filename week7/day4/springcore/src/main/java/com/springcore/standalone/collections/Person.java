package com.springcore.standalone.collections;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Person {
	private List<String> friends;
	private Map<String,Integer> feestructue;
	private Properties properties;
	public Person() {
	    super();
	}


	public List<String> getFriends() {
		return friends;
	}

	public void setFriends(List<String> friends) {
		this.friends = friends;
	}

	@Override
	public String toString() {
		return "Person [friends=" + friends + ", feestructue=" + feestructue + "]";
	}

	public Map<String, Integer> getFeestructue() {
		return feestructue;
	}

	public void setFeestructue(Map<String, Integer> feestructue) {
		this.feestructue = feestructue;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public Person(List<String> friends, Map<String, Integer> feestructue, Properties properties) {
		super();
		this.friends = friends;
		this.feestructue = feestructue;
		this.properties = properties;
	}
	
	
	
}
