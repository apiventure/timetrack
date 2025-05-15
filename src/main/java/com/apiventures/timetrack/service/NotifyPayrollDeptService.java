package com.apiventures.timetrack.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;

@Service
public class NotifyPayrollDeptService {

    final String excelFilePath = "src/main/resources/file/hqcentralpayrollrequestform.xlsx";
    @Autowired
    private com.apiventures.timetrack.service.EmailNotificationService emailService;
    //@Scheduled(cron = "0 24 15 * * WED,THU,FRI")
    public void notifyPayrollDept() throws FileNotFoundException, IOException, MessagingException {

        emailService.sendEmailWithAttachment(
                "nalluriedison2@gmail.com",
                "Timesheet Updated",
                "The employee has updated the timesheet. The revised hours are included in the attached Excel file.",
                excelFilePath
        );
    }

}
