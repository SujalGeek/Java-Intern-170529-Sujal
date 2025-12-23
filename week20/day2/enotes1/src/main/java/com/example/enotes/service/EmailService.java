package com.example.enotes.service;

import com.example.enotes.dto.EmailRequest;

public interface EmailService {

	void sendEmail(String to,String subject,String message);
}
