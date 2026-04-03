package com.example.smart_university_devices_and_materials_maintanance_system.controller;

import com.example.smart_university_devices_and_materials_maintanance_system.entities.User;
import com.example.smart_university_devices_and_materials_maintanance_system.repositories.UserRepository;
import com.example.smart_university_devices_and_materials_maintanance_system.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * LearningController — serves the Smart Learning System for all dashboard
 * roles.
 * All four roles (ADMIN, STAFF, TECHNICIAN, CLEANER_STUDENT) share the same
 * underlying knowledge base; the content is filtered/highlighted server-side
 * by the user's role so every page feels personalised.
 */
@Controller
@RequestMapping("/learning")
@RequiredArgsConstructor
public class LearningController {

    private final UserRepository userRepository;
    private final MaintenanceService maintenanceService;

    // ── Shared Knowledge Base ────────────────────────────────────────────────

    private static final List<Map<String, Object>> ALL_LESSONS = List.of(

            // ── MODULE 1: Devices ────────────────────────────────────────────────
            lesson("1", "Optimal Device Environment", "devices",
                    "Electronic devices such as PCs, projectors and servers perform best between 18°C–22°C with " +
                            "35–55% relative humidity. Avoid placing devices near windows, heat vents or dusty corners. "
                            +
                            "Power surges are a leading cause of premature device failure — ensure all equipment is connected "
                            +
                            "via surge protectors or UPS units.",
                    List.of("ADMIN", "STAFF", "TECHNICIAN"),
                    List.of(
                            quiz("What is the ideal room temperature range for electronic devices?",
                                    List.of("0°C–10°C", "18°C–22°C", "30°C–40°C", "25°C–35°C"), 1),
                            quiz("Which accessory prevents power-surge damage?",
                                    List.of("Extension cord", "USB hub", "UPS/Surge protector", "HDMI cable"), 2))),

            // ── MODULE 2: Maintenance Lifecycle ──────────────────────────────────
            lesson("2", "Maintenance Request Lifecycle", "maintenance",
                    "Every maintenance request flows through defined stages: PENDING → ASSIGNED → IN_PROGRESS → COMPLETED → VERIFIED. "
                            +
                            "Staff or cleaners raise a request; the admin reviews and assigns a technician. " +
                            "The technician starts the task and updates notes. Once done, the admin verifies. " +
                            "Understanding each stage helps all roles collaborate without delays.",
                    List.of("ADMIN", "STAFF", "TECHNICIAN", "CLEANER_STUDENT"),
                    List.of(
                            quiz("Which status comes immediately after a technician is assigned?",
                                    List.of("COMPLETED", "IN_PROGRESS", "ASSIGNED", "VERIFIED"), 2),
                            quiz("Who verifies that a completed task is satisfactory?",
                                    List.of("Staff", "Technician", "Cleaner", "Admin"), 3))),

            // ── MODULE 3: Materials & Stock ───────────────────────────────────────
            lesson("3", "Material Stock Management", "materials",
                    "Cleaning and maintenance materials (detergents, sanitizers, replacement bulbs, etc.) must be tracked "
                            +
                            "carefully. Report low stock before complete depletion. " +
                            "Every material submission records the room location so that auditors can trace usage patterns. "
                            +
                            "Stock replenishment requests are approved by admins and their status is reflected in real-time on the dashboard.",
                    List.of("ADMIN", "STAFF", "CLEANER_STUDENT"),
                    List.of(
                            quiz("Why is room location required when reporting a material?",
                                    List.of("For decoration", "To trace usage patterns", "Not required", "For pricing"),
                                    1),
                            quiz("Who approves a material replenishment request?",
                                    List.of("Technician", "Staff", "Admin", "Cleaner"), 2))),

            // ── MODULE 4: Reporting Best Practices ───────────────────────────────
            lesson("4", "How to Write a Good Report", "reporting",
                    "A high-quality damage or issue report includes: (1) exact device ID or material name, " +
                            "(2) precise room/location, (3) a clear description of the symptom (not just 'it's broken'), "
                            +
                            "(4) an honest priority level (LOW / MEDIUM / HIGH / CRITICAL). " +
                            "Exaggerating priority to jump the queue slows down genuine emergencies. " +
                            "The system automatically logs who submitted the report and when.",
                    List.of("STAFF", "CLEANER_STUDENT"),
                    List.of(
                            quiz("What should a good report always include?",
                                    List.of("Only the device name",
                                            "Exact location + clear description + honest priority",
                                            "Just a photo", "Only the priority"),
                                    1),
                            quiz("What happens if priority is always set to CRITICAL incorrectly?",
                                    List.of("Faster repairs for everyone", "System ignores it",
                                            "Genuine emergencies get delayed", "Nothing changes"),
                                    2))),

            // ── MODULE 5: Technician Best Practices ──────────────────────────────
            lesson("5", "Technician Field Excellence", "technician",
                    "Before starting a task: re-read the issue description and confirm device ID or material location. "
                            +
                            "Carry the right tools — multi-meter, screwdrivers, cleaning kits, replacement fuses. " +
                            "Always update the task notes field with what you found and what you did. " +
                            "If a task cannot be completed (missing parts), mark it IN_PROGRESS and add a note explaining why. "
                            +
                            "Never mark a task COMPLETED unless the issue is fully resolved.",
                    List.of("TECHNICIAN"),
                    List.of(
                            quiz("What should you do if a replacement part is missing during repair?",
                                    List.of("Mark task COMPLETED", "Ignore it", "Mark IN_PROGRESS and add a note",
                                            "Cancel the task"),
                                    2),
                            quiz("Where should technicians document what they found and did?",
                                    List.of("In an email", "In the chat", "In the task notes field",
                                            "Verbally to admin"),
                                    2))),

            // ── MODULE 6: Admin System Management ────────────────────────────────
            lesson("6", "Admin System Governance", "admin",
                    "The admin role is the system's backbone. Key responsibilities include: approving new user registrations, "
                            +
                            "approving device/material submissions, assigning technicians to requests, verifying completions, "
                            +
                            "and monitoring the analytics dashboard for overdue tasks. " +
                            "Use the Analytics page to track technician performance and ensure no college is under-served. "
                            +
                            "Regularly review audit logs to detect any unusual activity.",
                    List.of("ADMIN"),
                    List.of(
                            quiz("Which page shows technician workload and task completion rates?",
                                    List.of("Users page", "Maintenance page", "Analytics page", "Devices page"), 2),
                            quiz("What should happen after a technician marks a task as COMPLETED?",
                                    List.of("It auto-closes", "Admin verifies it", "Staff reviews it", "It is deleted"),
                                    1))),

            // ── MODULE 7: Security & Audit ────────────────────────────────────────
            lesson("7", "System Security & Audit Trails", "security",
                    "Every login, device registration, material submission, and task update is recorded in the Audit Log. "
                            +
                            "Audit trails ensure full accountability across all colleges and campuses. " +
                            "Never share your account credentials. The system uses MFA (multi-factor authentication) for added security. "
                            +
                            "If you notice suspicious activity, report it immediately to the system administrator.",
                    List.of("ADMIN", "STAFF", "TECHNICIAN", "CLEANER_STUDENT"),
                    List.of(
                            quiz("What records every action taken in the system?",
                                    List.of("The database backup", "Audit Log", "Email notifications", "Chat history"),
                                    1),
                            quiz("What adds an extra layer of login security?",
                                    List.of("Longer passwords", "MFA (Multi-Factor Authentication)", "Incognito mode",
                                            "VPN"),
                                    1))));

    // ── Helper builders ──────────────────────────────────────────────────────

    private static Map<String, Object> lesson(String id, String title, String category,
            String content, List<String> roles,
            List<Map<String, Object>> questions) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("title", title);
        m.put("category", category);
        m.put("content", content);
        m.put("roles", roles);
        m.put("questions", questions);
        return Collections.unmodifiableMap(m);
    }

    private static Map<String, Object> quiz(String question, List<String> options, int correctIndex) {
        Map<String, Object> q = new LinkedHashMap<>();
        q.put("question", question);
        q.put("options", options);
        q.put("correctIndex", correctIndex);
        return Collections.unmodifiableMap(q);
    }

    // ── Controller Endpoints ─────────────────────────────────────────────────

    /**
     * Main learning hub — lists all lessons relevant to the current user's role.
     */
    @GetMapping
    public String hub(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = getUser(userDetails);
        String role = user.getRole().name();

        List<Map<String, Object>> myLessons = ALL_LESSONS.stream()
                .filter(l -> ((List<?>) l.get("roles")).contains(role))
                .toList();

        // Stats for learning progress KPI cards
        long total = myLessons.size();
        long quizTotal = myLessons.stream()
                .mapToLong(l -> ((List<?>) l.get("questions")).size())
                .sum();

        model.addAttribute("lessons", myLessons);
        model.addAttribute("totalLessons", total);
        model.addAttribute("totalQuizzes", quizTotal);
        model.addAttribute("roleName", friendlyRole(role));

        // Recent requests for the quick-stats panel (available to all roles)
        model.addAttribute("completedCount",
                maintenanceService.countByStatus(
                        com.example.smart_university_devices_and_materials_maintanance_system.entities.MaintenanceRequest.TaskStatus.COMPLETED));

        return "learning/hub";
    }

    /**
     * Single lesson page with embedded quiz.
     */
    @GetMapping("/{id}")
    public String lesson(@PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        User user = getUser(userDetails);
        String role = user.getRole().name();

        Map<String, Object> lesson = ALL_LESSONS.stream()
                .filter(l -> l.get("id").equals(id))
                .filter(l -> ((List<?>) l.get("roles")).contains(role))
                .findFirst()
                .orElse(null);

        if (lesson == null)
            return "redirect:/learning";

        // Find next/prev for navigation
        List<Map<String, Object>> myLessons = ALL_LESSONS.stream()
                .filter(l -> ((List<?>) l.get("roles")).contains(role))
                .toList();
        int idx = myLessons.indexOf(lesson);
        model.addAttribute("lesson", lesson);
        model.addAttribute("prevLesson", idx > 0 ? myLessons.get(idx - 1) : null);
        model.addAttribute("nextLesson", idx < myLessons.size() - 1 ? myLessons.get(idx + 1) : null);
        model.addAttribute("lessonNumber", idx + 1);
        model.addAttribute("totalLessons", myLessons.size());

        return "learning/lesson";
    }

    // ── Utility ─────────────────────────────────────────────────────────────

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    }

    private String friendlyRole(String role) {
        return switch (role) {
            case "ADMIN" -> "System Administrator";
            case "STAFF" -> "Staff Member";
            case "TECHNICIAN" -> "Technical Staff";
            case "CLEANER_STUDENT" -> "Maintenance Cleaner";
            default -> role;
        };
    }
}
