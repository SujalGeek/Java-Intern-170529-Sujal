package com.example.user_service.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    // Inject values from your YAML file
    @Value("${SENDGRID_API_KEY}")
    private String sendGridApiKey;

    @Value("${SPRING_MAIL_FROM_EMAIL}")
    private String fromEmail;

    // --- 1. Send Verification Email ---
    public void sendVerificationEmail(String toEmail, String code) {
        String link = "http://localhost:5173/verify?code=" + code;
        String subject = "Verify your QuizApp Account";
        String body = "Welcome to QuizApp!\n\n" +
                      "Please click the link below to verify your account:\n" + 
                      link + 
                      "\n\n(If you did not sign up, please ignore this email.)";
        
        sendEmail(toEmail, subject, body);
    }

    // --- 2. Send Reset Token ---
    public void sendResetToken(String toEmail, String token) {
        String link = "http://localhost:5173/reset-password?token=" + token;
        String subject = "Reset Your Password";
        String body = "You have requested to reset your password.\n\n" +
                      "Click the link below to set a new password:\n" + 
                      link;
        
        sendEmail(toEmail, subject, body);
    }

    // --- 3. The Core SendGrid Logic (Uses HTTP Port 443 - Firewall Safe) ---
    private void sendEmail(String toAddress, String subject, String textBody) {
        Email from = new Email(fromEmail); // Sender (sujalmorwani@gmail.com)
        Email to = new Email(toAddress);   // Recipient
        String subjectLine = subject;
        Content content = new Content("text/plain", textBody);
        
        Mail mail = new Mail(from, subjectLine, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            
            // Log the result
            System.out.println("✅ Email Sent! Status Code: " + response.getStatusCode());
            if (response.getStatusCode() >= 400) {
                System.err.println("❌ SendGrid Error: " + response.getBody());
            }
        } catch (IOException ex) {
            System.err.println("❌ Network Error sending email: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}