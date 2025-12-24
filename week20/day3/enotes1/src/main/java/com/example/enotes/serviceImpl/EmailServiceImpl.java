package com.example.enotes.serviceImpl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.enotes.service.EmailService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

	
	@Value("${spring.sendgrid.api-key}")
	private String apiKey;
	
	@Value("${app.email.from}")
	private String fromEmail;
	
	
	@Override
	public void sendEmail(String to, String subject, String message) {
	
		Email from = new Email(fromEmail);
		
		Email toAddress = new Email(to);
		
		Content content = new Content("text/plain",message);
		
		Mail mail = new Mail(from,subject,toAddress,content);
		
		SendGrid sg = new SendGrid(apiKey);
		
		Request request = new Request();
		
		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			
			Response response = sg.api(request);
			
			log.info("Email Sent. Status Code {}",response.getStatusCode());
			
		}
		catch(IOException ex)
		{
			log.error("Error sending email: {}", ex.getMessage());
			throw new RuntimeException("Failed to send email");
		}
		
	}

	
	
}
