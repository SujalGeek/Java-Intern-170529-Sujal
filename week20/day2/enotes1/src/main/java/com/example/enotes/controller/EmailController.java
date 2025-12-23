package com.example.enotes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.enotes.dto.EmailRequest;
import com.example.enotes.service.EmailService;
import com.example.enotes.util.CommonUtil;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {

	@Autowired
	private EmailService emailService;
	
	@PostMapping("/send")
	public ResponseEntity<?> sendEmail(@RequestBody EmailRequest emailRequest)
	{
		try {
			emailService.sendEmail(
					emailRequest.getTo(),
					emailRequest.getSubject(),
					emailRequest.getMessage()
				);
			return CommonUtil.createBuildResponseMessage("Email Send Success", HttpStatus.OK);
		}
		catch(Exception e)
		{
			return CommonUtil.createErrorResponseMessage("Email Send Failed!!"+ e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
