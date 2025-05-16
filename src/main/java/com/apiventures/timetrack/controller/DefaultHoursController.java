package com.apiventures.timetrack.controller;

import com.apiventures.timetrack.entity.DefaultHours;
import com.apiventures.timetrack.entity.SubmittedEntryEntity;
import com.apiventures.timetrack.schedulers.TimeSheetNotificationScheduler;
import com.apiventures.timetrack.service.DefaultHoursService;
import com.apiventures.timetrack.service.EmailNotificationService;
import com.apiventures.timetrack.service.NotifyPayrollDeptService;
import com.apiventures.timetrack.service.SubmittedEntryService;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DefaultHoursController {

    private final DefaultHoursService defaultService;          // in‐memory list
    private final SubmittedEntryService submittedService;      // JPA/H2 history
    private final EmailNotificationService emailService;
    private final NotifyPayrollDeptService notifyPayrollDeptService;// sends email

    public DefaultHoursController(DefaultHoursService defaultService,
                                  SubmittedEntryService submittedService,
                                  EmailNotificationService emailService, NotifyPayrollDeptService notifyPayrollDeptService) {
        this.defaultService   = defaultService;
        this.submittedService = submittedService;
        this.emailService     = emailService;
        this.notifyPayrollDeptService = notifyPayrollDeptService;
    }

    @GetMapping({"/dashboard"})
    public String dashboard(Model model,
                            @ModelAttribute("success") String successMsg) {


        model.addAttribute("defaultHoursEntries", defaultService.findAll());


        LocalDate nextFriday = LocalDate.now()
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        model.addAttribute("nextFriday", nextFriday);


        model.addAttribute("submittedEntries",
                submittedService.findByWeek(nextFriday));

        model.addAttribute("success", successMsg);
        return "dashboard";
    }

    //  only updates the in‐memory List<DefaultHours>
    @PostMapping("/default-hours")
    public String saveDefaults(@RequestParam List<String> project,
                               @RequestParam List<Integer> mon,
                               @RequestParam List<Integer> tue,
                               @RequestParam List<Integer> wed,
                               @RequestParam List<Integer> thu,
                               @RequestParam List<Integer> fri,
                               RedirectAttributes ra) {

        List<DefaultHours> entries = new ArrayList<>();
        for (int i = 0; i < project.size(); i++) {
            entries.add(new DefaultHours(
                    project.get(i),
                    mon.get(i),
                    tue.get(i),
                    wed.get(i),
                    thu.get(i),
                    fri.get(i)
            ));
        }
        defaultService.saveAll(entries);
        ra.addFlashAttribute("success", "Default hours updated in memory!");
        return "redirect:/dashboard";
    }

    // ▶ sends email  archives into H2 via SubmittedEntryService
    @PostMapping("/demo-submit")
    public String demoSubmit(RedirectAttributes ra) {
        try {

            LocalDate nextFriday = LocalDate.now()
                    .with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.FRIDAY));


            List<DefaultHours> defaults = defaultService.findAll();


            submittedService.archiveDefaults(nextFriday, defaults);


            List<SubmittedEntryEntity> submittedEntriesToSend =
                    submittedService.findByWeek(nextFriday);


            String recipientEmail = "edison.nalluri@marriott.com";
            emailService.sendSubmittedHoursEmail(recipientEmail, submittedEntriesToSend);


            ra.addFlashAttribute("success", "Demo email sent and hours submitted!");

        } catch (MessagingException e) {

            e.printStackTrace();
            ra.addFlashAttribute("error", "Error sending submitted hours email.");
        } catch (Exception e) {

            e.printStackTrace();
            ra.addFlashAttribute("error", "An unexpected error occurred during submission or email process.");
        }

        return "redirect:/dashboard";
    }


    @PostMapping("submitted/resubmit")
    public String handleResubmit(RedirectAttributes redirectAttributes) {
        try {

            LocalDate nextFriday = LocalDate.now()
                    .with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.FRIDAY));
            List<SubmittedEntryEntity> submittedEntriesToSend =
                    submittedService.findByWeek(nextFriday);

            notifyPayrollDeptService.notifyPayrollDept(submittedEntriesToSend);


            redirectAttributes.addFlashAttribute("success", "Payroll notification email sent successfully!");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error: Excel file for payroll not found.");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/dashboard";
    }
}