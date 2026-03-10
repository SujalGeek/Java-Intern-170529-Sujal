package com.example.user_service.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    public void sendResetToken(String toEmail, String token) {
        Email from = new Email("temppostgeneral@gmail.com"); // 🔥 Ensure this is a Verified Sender in SendGrid
        String subject = "Your Password Recovery Token";
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", "Your recovery token is: " + token);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            // 🔥 ADD THE CAPTURE AND DEBUGGING HERE:
            Response response = sg.api(request); 
            
            System.out.println("SendGrid Status: " + response.getStatusCode());
            System.out.println("SendGrid Body: " + response.getBody());
            System.out.println("SendGrid Headers: " + response.getHeaders());

            if (response.getStatusCode() >= 400) {
                System.err.println("SendGrid Error: Failed to send email. Check API key and Verified Sender.");
            }
        } catch (Exception ex) {
            System.err.println("SendGrid Exception: " + ex.getMessage());
        }
    }
}
