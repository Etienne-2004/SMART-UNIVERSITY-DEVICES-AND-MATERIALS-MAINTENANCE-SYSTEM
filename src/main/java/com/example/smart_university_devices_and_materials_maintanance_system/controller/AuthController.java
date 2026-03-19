package com.example.smart_university_devices_and_materials_maintanance_system.controller;

import com.example.smart_university_devices_and_materials_maintanance_system.entities.*;
import com.example.smart_university_devices_and_materials_maintanance_system.repositories.*;
import com.example.smart_university_devices_and_materials_maintanance_system.security.JwtUtil;
import com.example.smart_university_devices_and_materials_maintanance_system.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.util.Random;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import javax.imageio.ImageIO;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UniversityRepository universityRepository;
    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;

    @GetMapping("/privacy-policy")
    public String privacyPolicy() {
        return "privacy-policy";
    }

    @GetMapping("/terms-of-service")
    public String termsOfService() {
        return "terms-of-service";
    }

    @GetMapping("/help-desk")
    public String helpDesk() {
        return "help-desk";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {
        String captchaText = generateRandomCaptchaText(6);
        session.setAttribute("captchaResult", captchaText);
        model.addAttribute("captchaImage", generateCaptchaImage(captchaText));
        model.addAttribute("universities", universityRepository.findAll());
        return "auth/login";
    }

    @PostMapping("/auth/login")
    public String processLogin(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String captchaAnswer,
            HttpSession session,
            HttpServletResponse response,
            RedirectAttributes redirectAttrs,
            Model model) {

        String expectedCaptcha = (String) session.getAttribute("captchaResult");
        if (expectedCaptcha != null && (captchaAnswer == null || !captchaAnswer.trim().equalsIgnoreCase(expectedCaptcha))) {
            model.addAttribute("error", "High-Level Robot Verification Failed: Incorrect characters.");
            
            // Re-generate captcha for retry
            String newCaptchaText = generateRandomCaptchaText(6);
            session.setAttribute("captchaResult", newCaptchaText);
            model.addAttribute("captchaImage", generateCaptchaImage(newCaptchaText));
            
            model.addAttribute("universities", universityRepository.findAll());
            return "auth/login";
        }

        try {
            String result = authService.initiateLogin(email, password);

            if ("OTP_REQUIRED".equals(result)) {
                redirectAttrs.addFlashAttribute("email", email);
                return "redirect:/otp/verify";
            }

            // No MFA — set JWT cookie and redirect to dashboard
            setJwtCookie(response, result);
            User user = userRepository.findByEmail(email).orElseThrow();
            return "redirect:" + getDashboardPath(user.getRole());

        } catch (Exception e) {
            model.addAttribute("error", "Invalid credentials: " + e.getMessage());
            
            // Re-generate captcha
            String newCaptchaText = generateRandomCaptchaText(6);
            session.setAttribute("captchaResult", newCaptchaText);
            model.addAttribute("captchaImage", generateCaptchaImage(newCaptchaText));
            
            model.addAttribute("universities", universityRepository.findAll());
            return "auth/login";
        }
    }

    @GetMapping("/otp/verify")
    public String otpPage(Model model) {
        return "auth/otp-verify";
    }

    @PostMapping("/otp/verify")
    public String verifyOtp(
            @RequestParam String email,
            @RequestParam String otpCode,
            HttpServletResponse response,
            RedirectAttributes redirectAttrs,
            Model model) {

        try {
            String result = authService.verifyOtpAndActivateAccount(email, otpCode);
            
            // Show success message and redirect to login page
            redirectAttrs.addFlashAttribute("success", 
                "✅ Account activated successfully! You can now log in with your credentials.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "auth/otp-verify";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("universities", universityRepository.findAll());
        return "auth/register";
    }

    @PostMapping("/auth/register")
    public String processRegister(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam String role,
            @RequestParam(required = false) Long universityId,
            @RequestParam(required = false) Long collegeId,
            RedirectAttributes redirectAttrs,
            Model model) {

        try {
            University university = universityId != null
                    ? universityRepository.findById(universityId).orElse(null)
                    : null;
            College college = collegeId != null
                    ? collegeRepository.findById(collegeId).orElse(null)
                    : null;

            authService.register(username, email, password, fullName, phoneNumber,
                    User.Role.valueOf(role), university, college);

            redirectAttrs.addFlashAttribute("email", email);
            redirectAttrs.addFlashAttribute("success",
                    "Registration successful! Please check your email for the OTP code to activate your account.");
            return "redirect:/otp/verify";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("universities", universityRepository.findAll());
            return "auth/register";
        }
    }

    @GetMapping("/auth/colleges/{universityId}")
    @ResponseBody
    public java.util.List<College> getColleges(@PathVariable Long universityId) {
        return collegeRepository.findByUniversityId(universityId);
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT_TOKEN", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/login?logout";
    }

    private void setJwtCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("JWT_TOKEN", token);
        cookie.setHttpOnly(true);
        // cookie.setSecure(true); // Enable in production over HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(86400); // 1 day
        response.addCookie(cookie);
    }

    private String getDashboardPath(User.Role role) {
        return switch (role) {
            case ADMIN -> "/admin/dashboard";
            case TECHNICIAN -> "/technician/dashboard";
            case STAFF -> "/staff/dashboard";
            case CLEANER_STUDENT -> "/cleaner/dashboard";
        };
    }

    private String generateRandomCaptchaText(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789@#%&*+?";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        while (sb.length() < length) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String generateCaptchaImage(String captchaText) {
        try {
            int width = 160;
            int height = 50;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(new Color(12, 17, 40));
            g2d.fillRect(0, 0, width, height);

            Random rand = new Random();
            for (int i = 0; i < 7; i++) {
                g2d.setColor(new Color(99 + rand.nextInt(100), 102 + rand.nextInt(100), 241));
                g2d.drawLine(0, rand.nextInt(height), width, rand.nextInt(height));
            }

            g2d.setFont(new Font("Monospaced", Font.BOLD, 30));
            for (int i = 0; i < captchaText.length(); i++) {
                g2d.setColor(new Color(200 + rand.nextInt(55), 200 + rand.nextInt(55), 200 + rand.nextInt(55)));
                g2d.drawString(String.valueOf(captchaText.charAt(i)), 15 + (i * 22), 35);
            }

            g2d.setTransform(new AffineTransform());
            for (int i = 0; i < 60; i++) {
                g2d.setColor(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255), 200));
                g2d.drawOval(rand.nextInt(width), rand.nextInt(height), 1, 1);
            }

            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            return "";
        }
    }
}
