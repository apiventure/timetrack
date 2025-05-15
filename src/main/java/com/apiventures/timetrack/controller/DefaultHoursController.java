package com.apiventures.timetrack.controller;

import com.apiventures.timetrack.entity.DefaultHours;
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
    private final TimeSheetNotificationScheduler emailService;
    private final NotifyPayrollDeptService notifyPayrollDeptService;// sends email

    public DefaultHoursController(DefaultHoursService defaultService,
                                  SubmittedEntryService submittedService,
                                  TimeSheetNotificationScheduler emailService, NotifyPayrollDeptService notifyPayrollDeptService) {
        this.defaultService   = defaultService;
        this.submittedService = submittedService;
        this.emailService     = emailService;
        this.notifyPayrollDeptService = notifyPayrollDeptService;
    }

    @GetMapping({"/dashboard"})
    public String dashboard(Model model,
                            @ModelAttribute("success") String successMsg) {

        // 1) current defaults from the in‐memory list
        model.addAttribute("defaultHoursEntries", defaultService.findAll());

        // 2) compute this week’s Friday
        LocalDate nextFriday = LocalDate.now()
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        model.addAttribute("nextFriday", nextFriday);

        // 3) load any previously submitted entries for that Friday
        model.addAttribute("submittedEntries",
                submittedService.findByWeek(nextFriday));

        model.addAttribute("success", successMsg);
        return "dashboard";
    }

    // ▶ only updates the in‐memory List<DefaultHours>
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

    // ▶ sends email *and* archives into H2 via SubmittedEntryService
    @PostMapping("/demo-submit")
    public String demoSubmit(RedirectAttributes ra) {

        // send the confirmation email
        emailService.sendConfEmail();

        // archive the current defaults into your DB
        LocalDate nextFriday = LocalDate.now()
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        List<DefaultHours> defaults = defaultService.findAll();
        submittedService.archiveDefaults(nextFriday, defaults);

        ra.addFlashAttribute("success", "Demo email sent and hours submitted!");
        return "redirect:/dashboard";
    }

    // Method to handle the POST request from the Resubmit form
    @PostMapping("submitted/resubmit") // Matches the th:action="@{/submitted/resubmit}"
    public String handleResubmit(RedirectAttributes redirectAttributes) {
        try {
            // Call the service method to send the email
            notifyPayrollDeptService.notifyPayrollDept();

            // Add a success message to be displayed after redirection
            redirectAttributes.addFlashAttribute("success", "Payroll notification email sent successfully!");

        } catch (FileNotFoundException e) {
            // Handle file not found error
            e.printStackTrace(); // Log the error
            redirectAttributes.addFlashAttribute("error", "Error: Excel file for payroll not found.");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Redirect back to the dashboard page to show the updated status/message
        // Assuming your dashboard URL is /dashboard or /submitted/view
        return "redirect:/dashboard"; // Or "redirect:/submitted/view" depending on your setup
    }
}