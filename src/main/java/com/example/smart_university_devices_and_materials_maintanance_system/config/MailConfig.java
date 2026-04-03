package com.example.smart_university_devices_and_materials_maintanance_system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Explicit Gmail SMTP configuration for OTP email delivery.
 *
 * Uses Google App Password (not the regular Gmail password).
 * App Password must be generated from: https://myaccount.google.com/apppasswords
 * 2-Step Verification must be enabled on the Gmail account first.
 *
 * The password is stored WITHOUT spaces in application.properties
 * (spaces shown by Google are only visual separators — JavaMail requires no spaces).
 */
@Configuration
public class MailConfig {

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // ── Gmail SMTP server settings ─────────────────────────────────────
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);
        mailSender.setDefaultEncoding("UTF-8");

        // ── JavaMail / SMTP session properties ────────────────────────────
        Properties props = mailSender.getJavaMailProperties();

        // Transport protocol
        props.put("mail.transport.protocol", "smtp");

        // STARTTLS (required for Gmail port 587)
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");

        // Authentication
        props.put("mail.smtp.auth", "true");

        // Trust Gmail's certificate
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        // Timeouts (milliseconds) — prevents hanging threads
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        // Disable legacy SSL on port 587 (we use STARTTLS instead)
        props.put("mail.smtp.ssl.enable", "false");

        // Debug — set to true temporarily to see SMTP conversation in logs
        props.put("mail.debug", "false");

        return mailSender;
    }
}
