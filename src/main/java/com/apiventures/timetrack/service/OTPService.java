package com.apiventures.timetrack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {

    @Autowired
    private JavaMailSender mailSender;

    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    private final Set<String> allowedEmails = Set.of(
            "edison.nalluri@marriott.com",
            "himaja.velpula@marriott.com",
            "somasekhar.patil@marriott.com",
            "sharon.thomas@marriott.com",
            "melanie.sandukas@marriott.com"
    );

    public boolean generateAndSendOtp(String email) {
        if (!allowedEmails.contains(email)) {
            return false;
        }
        String otp = String.valueOf(new Random().nextInt(900_000) + 100_000);
        otpStore.put(email, otp);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP is: " + otp);
        mailSender.send(message);
        return true;
    }

    public boolean verifyOtp(String email, String inputOtp) {
        return otpStore.containsKey(email) && otpStore.get(email).equals(inputOtp);
    }
}