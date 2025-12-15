package com.example.chat_app_backend.entities;

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
	private MessageType type;
	private LocalDateTime timeStamp;
	public Message(String sender, String content,MessageType type) {
		super();
		this.sender = sender;
		this.content = content;
		this.type=type;
		this.timeStamp= LocalDateTime.now();
	}
	
	
}
