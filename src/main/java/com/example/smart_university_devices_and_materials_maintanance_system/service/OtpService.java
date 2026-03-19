package com.example.smart_university_devices_and_materials_maintanance_system.service;

import com.example.smart_university_devices_and_materials_maintanance_system.entities.OtpToken;
import com.example.smart_university_devices_and_materials_maintanance_system.repositories.OtpTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    /** Admin email receives a copy of every new registration notification */
    private static final String ADMIN_EMAIL = "harindintwarietiennee@gmail.com";

    private final SecureRandom random = new SecureRandom();

    // ──────────────────────────────────────────────────────────────────────────
    //  PUBLIC API
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Generates a 6-digit OTP, persists it, and sends it to the user's email.
     * Admin also receives a notification email for new registrations.
     *
     * @param email the recipient's email address
     * @return the generated OTP string (also stored in DB)
     */
    public String generateAndSendOtp(String email) {
        // Invalidate any existing unused OTPs for this email
        otpTokenRepository.invalidateAllForEmail(email);

        // Generate a cryptographically secure 6-digit OTP
        String otp = String.format("%06d", random.nextInt(1_000_000));
        long expiresInHours = otpExpiryMinutes / 60;

        OtpToken token = OtpToken.builder()
                .email(email)
                .otpCode(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes))
                .used(false)
                .build();
        otpTokenRepository.save(token);

        // Send OTP email to the user (async so it doesn't block the HTTP request)
        sendOtpEmailAsync(email, otp, expiresInHours);
        log.info("✅ OTP generated and dispatch initiated for: {}", email);
        return otp;
    }

    /**
     * Sends an admin notification when a new user completes registration.
     * Call this after the user entity is saved.
     */
    @Async
    public void sendAdminRegistrationNotification(String newUserEmail, String newUserFullName, String role) {
        try {
            String subject = "📋 New User Registration – Awaiting Approval";
            String htmlBody = buildAdminNotificationHtml(newUserEmail, newUserFullName, role);
            sendHtmlEmail(ADMIN_EMAIL, subject, htmlBody);
            log.info("📧 Admin notification sent for new user: {}", newUserEmail);
        } catch (Exception e) {
            log.warn("⚠️ Could not send admin notification email: {}", e.getMessage());
        }
    }

    /**
     * Validates the provided OTP code for the given email.
     *
     * @return true if valid and not expired; false otherwise
     */
    public boolean validateOtp(String email, String otpCode) {
        return otpTokenRepository
                .findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email)
                .map(token -> {
                    if (token.isUsed()) {
                        log.warn("OTP already used for {}", email);
                        return false;
                    }
                    if (LocalDateTime.now().isAfter(token.getExpiresAt())) {
                        log.warn("OTP expired for {}", email);
                        return false;
                    }
                    if (!token.getOtpCode().equals(otpCode)) {
                        log.warn("Incorrect OTP submitted for {}", email);
                        return false;
                    }
                    token.setUsed(true);
                    otpTokenRepository.save(token);
                    log.info("✅ OTP validated successfully for {}", email);
                    return true;
                })
                .orElse(false);
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ──────────────────────────────────────────────────────────────────────────

    @Async
    protected void sendOtpEmailAsync(String toEmail, String otp, long expiresInHours) {
        try {
            String subject = "🔐 Your OTP Code – Smart University MMS";
            String htmlBody = buildOtpEmailHtml(toEmail, otp, expiresInHours);
            sendHtmlEmail(toEmail, subject, htmlBody);
            log.info("📧 OTP email delivered to: {}", toEmail);
        } catch (Exception e) {
            log.error("❌ Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            log.info("💬 FALLBACK — OTP for {} is: {} (expires in {} hours)", toEmail, otp, expiresInHours);
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage mime = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setFrom(fromEmail, "Smart University MMS");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML
            mailSender.send(mime);
        } catch (UnsupportedEncodingException e) {
            log.error("Error encoding email from address: {}", e.getMessage());
            throw new MessagingException("Failed to encode email from address", e);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  EMAIL TEMPLATES
    // ──────────────────────────────────────────────────────────────────────────

    private String buildOtpEmailHtml(String email, String otp, long expiresInHours) {
        String[] digits = otp.split("");
        StringBuilder digitCells = new StringBuilder();
        for (String d : digits) {
            digitCells.append(
                "<td style=\"width:42px;height:52px;background:#1e3a5f;border:2px solid #3b82f6;"
                + "border-radius:8px;text-align:center;vertical-align:middle;"
                + "font-size:28px;font-weight:800;color:#60a5fa;font-family:monospace;\">")
                .append(d).append("</td>");
        }

        return "<!DOCTYPE html>"
            + "<html><head><meta charset='UTF-8'/></head>"
            + "<body style='margin:0;padding:0;background:#0f172a;font-family:Inter,Arial,sans-serif;'>"
            + "<table width='100%' cellpadding='0' cellspacing='0'>"
            + "<tr><td align='center' style='padding:40px 20px;'>"
            + "<table width='560' cellpadding='0' cellspacing='0' style='background:#1e293b;"
            +        "border-radius:16px;border:1px solid #334155;overflow:hidden;'>"

            // Header
            + "<tr><td style='background:linear-gradient(135deg,#1d4ed8,#4f46e5);"
            +         "padding:32px 40px;text-align:center;'>"
            + "<div style='font-size:36px;'>🏛️</div>"
            + "<h1 style='color:#fff;margin:8px 0 4px;font-size:22px;font-weight:700;"
            +         "letter-spacing:.5px;'>Smart University MMS</h1>"
            + "<p style='color:#bfdbfe;margin:0;font-size:13px;'>Device &amp; Materials Maintenance System</p>"
            + "</td></tr>"

            // Body
            + "<tr><td style='padding:36px 40px;'>"
            + "<h2 style='color:#f1f5f9;margin:0 0 8px;font-size:18px;'>Your Verification Code</h2>"
            + "<p style='color:#94a3b8;font-size:14px;margin:0 0 28px;'>Enter this 6-digit OTP to verify your account.</p>"

            // OTP digits row
            + "<table cellpadding='0' cellspacing='6' style='margin:0 auto 28px;'>"
            + "<tr>" + digitCells + "</tr>"
            + "</table>"

            + "<div style='background:#0f172a;border-radius:8px;padding:14px 18px;"
            +            "border-left:4px solid #3b82f6;margin-bottom:24px;'>"
            + "<p style='color:#64748b;font-size:12px;margin:0 0 4px;'>⏰ Expiry</p>"
            + "<p style='color:#e2e8f0;font-size:14px;margin:0;'>Valid for <strong>" + expiresInHours
            +    " hour" + (expiresInHours == 1 ? "" : "s") + "</strong> from time of request</p>"
            + "</div>"

            + "<p style='color:#64748b;font-size:13px;margin:0;'>"
            + "If you did not request this code, you can safely ignore this email. "
            + "Your account will not be created without OTP verification.</p>"
            + "</td></tr>"

            // Footer
            + "<tr><td style='background:#0f172a;padding:20px 40px;text-align:center;"
            +            "border-top:1px solid #1e293b;'>"
            + "<p style='color:#475569;font-size:12px;margin:0;'>"
            + "© 2026 Smart University Device &amp; Materials Maintenance System &nbsp;|&nbsp; Rwanda"
            + "</p></td></tr>"

            + "</table>"
            + "</td></tr></table>"
            + "</body></html>";
    }

    private String buildAdminNotificationHtml(String newUserEmail, String fullName, String role) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
        return "<!DOCTYPE html>"
            + "<html><head><meta charset='UTF-8'/></head>"
            + "<body style='margin:0;padding:0;background:#0f172a;font-family:Inter,Arial,sans-serif;'>"
            + "<table width='100%' cellpadding='0' cellspacing='0'>"
            + "<tr><td align='center' style='padding:40px 20px;'>"
            + "<table width='560' cellpadding='0' cellspacing='0' style='background:#1e293b;"
            +        "border-radius:16px;border:1px solid #334155;overflow:hidden;'>"

            // Header
            + "<tr><td style='background:linear-gradient(135deg,#064e3b,#065f46);"
            +         "padding:28px 40px;text-align:center;'>"
            + "<div style='font-size:32px;'>👨‍💼</div>"
            + "<h1 style='color:#fff;margin:8px 0 4px;font-size:20px;font-weight:700;'>"
            +         "New User Registration — Admin Alert</h1>"
            + "<p style='color:#6ee7b7;margin:0;font-size:13px;'>Smart University MMS</p>"
            + "</td></tr>"

            // Body
            + "<tr><td style='padding:32px 40px;'>"
            + "<p style='color:#94a3b8;font-size:14px;margin:0 0 20px;'>"
            +    "A new user has registered and is awaiting email OTP verification.</p>"

            + "<table width='100%' cellpadding='10' cellspacing='0' "
            +        "style='background:#0f172a;border-radius:10px;border:1px solid #1e3a5f;"
            +        "font-size:14px;color:#e2e8f0;'>"
            + "<tr><td style='color:#64748b;width:130px;'>👤 Full Name</td>"
            +     "<td><strong>" + escapeHtml(fullName) + "</strong></td></tr>"
            + "<tr style='border-top:1px solid #1e293b;'>"
            +     "<td style='color:#64748b;'>📧 Email</td>"
            +     "<td>" + escapeHtml(newUserEmail) + "</td></tr>"
            + "<tr style='border-top:1px solid #1e293b;'>"
            +     "<td style='color:#64748b;'>🔖 Role</td>"
            +     "<td><span style='background:#1e3a5f;color:#60a5fa;padding:2px 10px;"
            +         "border-radius:20px;font-size:12px;'>" + escapeHtml(role) + "</span></td></tr>"
            + "<tr style='border-top:1px solid #1e293b;'>"
            +     "<td style='color:#64748b;'>🕐 Time</td><td>" + timestamp + "</td></tr>"
            + "</table>"

            + "<p style='color:#64748b;font-size:13px;margin:24px 0 0;'>"
            + "Please log in to the admin dashboard to review and approve or reject this account.</p>"
            + "</td></tr>"

            // Footer
            + "<tr><td style='background:#0f172a;padding:16px 40px;text-align:center;"
            +            "border-top:1px solid #1e293b;'>"
            + "<p style='color:#475569;font-size:12px;margin:0;'>"
            + "© 2026 Smart University MMS &nbsp;|&nbsp; Admin Notification Service"
            + "</p></td></tr>"

            + "</table>"
            + "</td></tr></table>"
            + "</body></html>";
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
