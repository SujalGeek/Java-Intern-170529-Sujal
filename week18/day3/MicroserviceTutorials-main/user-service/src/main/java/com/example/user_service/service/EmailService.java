package com.example.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Method to send Verification Email
    public void sendVerificationEmail(String toEmail, String code) {
        // This link points to your Frontend Verify Page
        String link = "http://localhost:5173/verify?code=" + code;
        
        String body = "Welcome to QuizApp!\n\n" +
                      "Please click the link below to verify your account:\n" +
                      link + "\n\n" +
                      "If you did not create an account, please ignore this email.";
        
        sendEmail(toEmail, "Verify Your Account - QuizApp", body);
    }

    // Method to send Reset Password Email
    public void sendResetToken(String toEmail, String token) {
        // This link points to your Frontend Reset Page
        String link = "http://localhost:5173/reset-password?token=" + token;
        
        String body = "Hello,\n\n" +
                      "We received a request to reset your password.\n" +
                      "Click the link below to set a new password:\n" +
                      link + "\n\n" +
                      "This link is valid for 1 hour.";
        
        sendEmail(toEmail, "Reset Password Request - QuizApp", body);
    }

    // Helper method to actually send the email via SMTP
    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            // This 'From' address is just a label, Gmail overwrites it with your actual email
            message.setFrom("noreply@quizapp.com"); 
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            
            mailSender.send(message);
            System.out.println("✅ Email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}