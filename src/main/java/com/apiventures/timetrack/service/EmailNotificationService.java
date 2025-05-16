package com.apiventures.timetrack.service;

import java.io.File;
import java.util.List;

import com.apiventures.timetrack.entity.SubmittedEntryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service


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

    public void sendEmailWithAttachment(
            String to,
            String subject,
            String text,
            String attachmentPath,
            String attachmentFilename,
            String[] cc
    ) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("apiventures999@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text , true);


        if (cc != null && cc.length > 0) {
            helper.setCc(cc);
        }


        FileSystemResource file = new FileSystemResource(new File(attachmentPath));
        helper.addAttachment(attachmentFilename, file);

        mailSender.send(message);
        System.out.println("Email sent with Excel attachment.");
    }


    public void sendSubmittedHoursEmail(String to, List<SubmittedEntryEntity> submittedEntries) throws MessagingException {
        StringBuilder html = new StringBuilder();
        html.append("<h3>Submitted Timesheet Details</h3>");
        html.append("Hello Associate,<br>");
        html.append("We know you are busy, so we took care of the Timesheet !<br>");
        html.append("Please find the submitted hours below.<br>");


        html.append("<table border='1' cellpadding='5' cellspacing='0'>");
        html.append("<tr><th>Date</th><th>Project ID</th><th>Monday</th><th>Tuesday</th><th>Wednesday</th><th>Thursday</th><th>Friday</th></tr>");

        if (submittedEntries != null && !submittedEntries.isEmpty()) {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy");
            for (SubmittedEntryEntity entry : submittedEntries) {
                html.append("<tr>");
                String formattedDate = "";
                if (entry.getWeekCloseDate() != null) {
                    formattedDate = entry.getWeekCloseDate().format(formatter);
                }
                html.append("<td>").append(formattedDate).append("</td>");
                html.append("<td>").append(entry.getProject()).append("</td>");
                html.append("<td>").append(entry.getMon()).append("</td>");
                html.append("<td>").append(entry.getTue()).append("</td>");
                html.append("<td>").append(entry.getWed()).append("</td>");
                html.append("<td>").append(entry.getThu()).append("</td>");
                html.append("<td>").append(entry.getFri()).append("</td>");
                html.append("</tr>");
            }
        } else {
            html.append("<tr><td colspan='7'>No submitted hours data available.</td></tr>");
        }

        html.append("</table>");
        html.append("<br>Enjoy your weekend :)<br>");
        html.append("Thank you.");
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("Submitted Timesheet Information");
        helper.setText(html.toString(), true); // true = isHtml

        mailSender.send(message);
    }

}