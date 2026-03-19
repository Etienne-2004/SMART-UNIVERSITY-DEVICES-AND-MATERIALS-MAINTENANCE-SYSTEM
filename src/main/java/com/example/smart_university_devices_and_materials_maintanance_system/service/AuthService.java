package com.example.smart_university_devices_and_materials_maintanance_system.service;

import com.example.smart_university_devices_and_materials_maintanance_system.entities.*;
import com.example.smart_university_devices_and_materials_maintanance_system.repositories.*;
import com.example.smart_university_devices_and_materials_maintanance_system.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;

    @Transactional
    public User register(String username, String email, String password, String fullName,
            String phoneNumber, User.Role role,
            University university, College college) {

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .phoneNumber(phoneNumber)
                .role(role)
                .university(university)
                .college(college)
                .mfaEnabled(true)
                .accountStatus(role == User.Role.ADMIN
                        ? User.AccountStatus.PENDING_EMAIL
                        : User.AccountStatus.PENDING_EMAIL)
                .build();

        User saved = userRepository.save(user);
        otpService.generateAndSendOtp(email);
        log.info("✅ User registered and OTP sent: {} ({})", username, role);
        return saved;
    }

    public String initiateLogin(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getAccountStatus() != User.AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active. Status: " + user.getAccountStatus());
        }

        // Send OTP if MFA is enabled
        if (user.isMfaEnabled()) {
            otpService.generateAndSendOtp(email);
            return "OTP_REQUIRED";
        }

        return generateToken(user);
    }

    public String verifyOtpAndActivateAccount(String email, String otpCode) {
        if (!otpService.validateOtp(email, otpCode)) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getAccountStatus() == User.AccountStatus.PENDING_EMAIL) {
            // First time activation - after OTP, if not admin, wait for approval
            if (user.getRole() == User.Role.ADMIN) {
                user.setAccountStatus(User.AccountStatus.ACTIVE);
            } else {
                user.setAccountStatus(User.AccountStatus.PENDING_APPROVAL);
            }
            userRepository.save(user);
        } else if (user.getAccountStatus() != User.AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active. Current status: " + user.getAccountStatus());
        }

        return "Account activated successfully";
    }

    public String verifyOtpAndLogin(String email, String otpCode) {
        if (!otpService.validateOtp(email, otpCode)) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getAccountStatus() == User.AccountStatus.PENDING_EMAIL) {
            // First time activation - after OTP, if not admin, wait for approval
            if (user.getRole() == User.Role.ADMIN) {
                user.setAccountStatus(User.AccountStatus.ACTIVE);
            } else {
                user.setAccountStatus(User.AccountStatus.PENDING_APPROVAL);
            }
            userRepository.save(user);
        } else if (user.getAccountStatus() != User.AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active. Current status: " + user.getAccountStatus());
        }

        return generateToken(user);
    }

    private String generateToken(User user) {
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Audit log
        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .action("LOGIN")
                .details("User logged in successfully")
                .build());

        // Build UserDetails for JWT generation
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

        return jwtUtil.generateToken(userDetails);
    }

    public void logAudit(User user, String action, String details) {
        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .action(action)
                .details(details)
                .build());
    }
}
