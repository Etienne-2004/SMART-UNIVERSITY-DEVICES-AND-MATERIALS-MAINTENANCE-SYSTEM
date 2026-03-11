package com.example.smart_university_devices_and_materials_maintanance_system.controller;

import com.example.smart_university_devices_and_materials_maintanance_system.entities.*;
import com.example.smart_university_devices_and_materials_maintanance_system.repositories.*;
import com.example.smart_university_devices_and_materials_maintanance_system.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cleaner")
@RequiredArgsConstructor
public class CleanerController {

    private final UserRepository userRepository;
    private final MaterialService materialService;
    private final MaintenanceService maintenanceService;
    private final NotificationService notificationService;
    private final MaintenanceRequestRepository maintenanceRequestRepository;

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User cleaner = getCurrentUser(userDetails);
        model.addAttribute("cleaner", cleaner);
        model.addAttribute("myReports", materialService.getReportedByUser(cleaner.getId()));
        model.addAttribute("myRequests", maintenanceRequestRepository.findByReportedById(cleaner.getId()));
        model.addAttribute("notifications", notificationService.getAllForUser(cleaner.getId()));
        model.addAttribute("unreadCount", notificationService.countUnread(cleaner.getId()));
        return "cleaner/dashboard";
    }

    @GetMapping("/report")
    public String reportPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User cleaner = getCurrentUser(userDetails);
        model.addAttribute("cleaner", cleaner);
        return "cleaner/report";
    }

    @PostMapping("/report")
    public String submitReport(
            @RequestParam String materialName,
            @RequestParam String roomLocation,
            @RequestParam(required = false) String description,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttrs) {

        User cleaner = getCurrentUser(userDetails);
        try {
            Material material = Material.builder()
                    .materialName(materialName)
                    .roomLocation(roomLocation)
                    .description(description)
                    .university(cleaner.getUniversity())
                    .college(cleaner.getCollege())
                    .reportedBy(cleaner)
                    .build();
            Material saved = materialService.addMaterial(material);

            // Auto-report as maintenance issue
            maintenanceService.reportMaterialIssue(saved,
                    "Issue reported: " + (description != null ? description : "No details"),
                    MaintenanceRequest.Priority.MEDIUM, cleaner);

            redirectAttrs.addFlashAttribute("success",
                    "Issue reported successfully. Admin will assign a technician.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cleaner/dashboard";
    }
}
