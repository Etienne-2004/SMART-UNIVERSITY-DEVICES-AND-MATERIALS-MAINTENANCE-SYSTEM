package com.example.smart_university_devices_and_materials_maintanance_system.service;

import com.example.smart_university_devices_and_materials_maintanance_system.entities.OtpToken;
import com.example.smart_university_devices_and_materials_maintanance_system.repositories.OtpTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final OtpTokenRepository otpTokenRepository;
    private final JavaMailSender mailSender;

    @Value("${app.otp.expiry-minutes:1440}")
    private int otpExpiryMinutes;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final SecureRandom random = new SecureRandom();

    public String generateAndSendOtp(String email) {
        // Invalidate any existing OTPs
        otpTokenRepository.invalidateAllForEmail(email);

        // Generate 6-digit OTP
        String otp = String.format("%06d", random.nextInt(1000000));

        OtpToken token = OtpToken.builder()
                .email(email)
                .otpCode(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes))
                .used(false)
                .build();
        otpTokenRepository.save(token);

        // Send OTP via email
        try {
            sendOtpEmail(email, otp);
        } catch (Exception e) {
            log.warn("Could not send OTP email (check mail config): {}", e.getMessage());
            log.info("OTP for {} is: {} (shown in logs because mail failed)", email, otp);
        }

        return otp;
    }

    public boolean validateOtp(String email, String otpCode) {
        return otpTokenRepository.findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email)
                .map(token -> {
                    if (token.isUsed())
                        return false;
                    if (LocalDateTime.now().isAfter(token.getExpiresAt()))
                        return false;
                    if (!token.getOtpCode().equals(otpCode))
                        return false;

                    token.setUsed(true);
                    otpTokenRepository.save(token);
                    return true;
                })
                .orElse(false);
    }

    private void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("🔐 Smart University System – Your OTP Code");
        message.setText(
                "Dear User,\n\n" +
                        "Your One-Time Password (OTP) for Smart University Device & Materials Maintenance System is:\n\n"
                        +
                        "  " + otp + "\n\n" +
                        "This OTP is valid for 24 hours (" + (otpExpiryMinutes / 60) + " hours).\n\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "Smart University System\nRwanda");
        mailSender.send(message);
        log.info("OTP email sent to {}", toEmail);
    }
}
