package com.apiventures.timetrack.schedulers;

import com.apiventures.timetrack.entity.SubmittedEntryEntity;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;



@Component
public class TimeSheetNotificationScheduler {

    @Autowired
    private com.apiventures.timetrack.service.EmailNotificationService emailService;

    @Autowired
    private com.apiventures.timetrack.service.SubmittedEntryService submittedEntryService;
    // Every Wednesday, Thursday, and Friday at 9:00 AM
    //@Scheduled(cron = "0 23 13 * * WED,THU,FRI")
    public void sendReminderEmail() {
        emailService.sendSimpleEmail(
                "edison.nalluri@marriott.com",
                "Timesheet Reminder",
                "Hello Associate , <br> Please submit your timesheet by EOD Friday.\n"
                        + "If not submitted, it will be automatically submit the time using the default hours saved in TimeLedger."
        );
    }

}
