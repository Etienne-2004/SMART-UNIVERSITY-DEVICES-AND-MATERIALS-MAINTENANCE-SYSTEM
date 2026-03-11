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

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UniversityRepository universityRepository;
    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("universities", universityRepository.findAll());
        return "auth/login";
    }

    @PostMapping("/auth/login")
    public String processLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpServletResponse response,
            RedirectAttributes redirectAttrs,
            Model model) {

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
            String token = authService.verifyOtpAndLogin(email, otpCode);
            setJwtCookie(response, token);

            User user = userRepository.findByEmail(email).orElseThrow();
            return "redirect:" + getDashboardPath(user.getRole());
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
}
