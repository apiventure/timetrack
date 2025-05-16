package com.apiventures.timetrack.controller;

import com.apiventures.timetrack.schedulers.TimeSheetNotificationScheduler;
import com.apiventures.timetrack.service.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    @Autowired
    private OTPService otpService;

    @Autowired
    private TimeSheetNotificationScheduler timeSheetNotificationScheduler;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // No .html extension needed
    }

    @PostMapping("/send-otp")
    @ResponseBody
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        boolean allowed = otpService.generateAndSendOtp(email);
        if (!allowed) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized email.");
        }
        return ResponseEntity.ok("OTP sent.");
    }


    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email, @RequestParam String otp, Model model) {
        boolean verified = otpService.verifyOtp(email, otp);
        if (verified) {
            return "redirect:/dashboard";
        } else {
            model.addAttribute("email", email);
            model.addAttribute("error", "Invalid OTP , Please Try Again");
            return "login";
        }
    }


}
