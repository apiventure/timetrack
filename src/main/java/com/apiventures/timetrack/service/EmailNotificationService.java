package com.apiventures.timetrack.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service

// For CodeFest
public class EmailNotificationService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("apiventures999@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendEmailWithAttachment(String to, String subject, String text, String attachmentPath) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("apiventures999@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        // Add Excel file as attachment
        FileSystemResource file = new FileSystemResource(new File(attachmentPath));
        helper.addAttachment("timesheet.xlsx", file);

        mailSender.send(message);
        System.out.println("Email sent with Excel attachment.");
    }

}