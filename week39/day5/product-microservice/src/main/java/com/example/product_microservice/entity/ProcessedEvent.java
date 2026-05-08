package com.example.product_microservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ProcessedEvent {

	@Id
	private String eventId;

	public ProcessedEvent() {}
	
	public ProcessedEvent(String eventId)
	{
		this.eventId = eventId;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	
	
}
