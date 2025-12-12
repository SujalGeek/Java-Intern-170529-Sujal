package com.example.chat_backend.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Message {

	private String sender;
	private String content;
	private LocalDateTime timeStamp;
	public Message(String sender, String content) {
		super();
		this.sender = sender;
		this.content = content;
		this.timeStamp= LocalDateTime.now();
	}
	
	
}
