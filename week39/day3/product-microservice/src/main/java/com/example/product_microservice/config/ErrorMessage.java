package com.example.product_microservice.config;

import java.util.Date;

public class ErrorMessage {

	private Date timestamp;
	private String message;
	private String details;
	
	public ErrorMessage()
	{
		
	}
	
	
	public ErrorMessage(Date timestamp, String message, String details) {
		super();
		this.timestamp = timestamp;
		this.message = message;
		this.details = details;
	}



	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
