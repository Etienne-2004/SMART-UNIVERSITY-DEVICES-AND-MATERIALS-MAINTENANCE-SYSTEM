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
@RequestMapping("/technician")
@RequiredArgsConstructor
public class TechnicianController {

    private final UserRepository userRepository;
    private final MaintenanceService maintenanceService;
    private final NotificationService notificationService;
    private final MaintenanceRequestRepository maintenanceRequestRepository;

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User technician = getCurrentUser(userDetails);

        model.addAttribute("technician", technician);
        model.addAttribute("assignedTasks", maintenanceService.getByTechnician(technician.getId()));
        model.addAttribute("pendingCount",
                maintenanceRequestRepository.findByAssignedTechnicianIdAndStatus(
                        technician.getId(), MaintenanceRequest.TaskStatus.ASSIGNED).size());
        model.addAttribute("inProgressCount",
                maintenanceRequestRepository.findByAssignedTechnicianIdAndStatus(
                        technician.getId(), MaintenanceRequest.TaskStatus.IN_PROGRESS).size());
        model.addAttribute("completedCount",
                maintenanceRequestRepository.findByAssignedTechnicianIdAndStatus(
                        technician.getId(), MaintenanceRequest.TaskStatus.COMPLETED).size());
        model.addAttribute("notifications", notificationService.getAllForUser(technician.getId()));
        model.addAttribute("unreadCount", notificationService.countUnread(technician.getId()));
        return "technician/dashboard";
    }

    @GetMapping("/tasks")
    public String tasks(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User technician = getCurrentUser(userDetails);
        model.addAttribute("technician", technician);
        model.addAttribute("tasks", maintenanceService.getByTechnician(technician.getId()));
        return "technician/tasks";
    }

    @PostMapping("/tasks/{id}/start")
    public String startTask(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttrs) {
        User technician = getCurrentUser(userDetails);
        try {
            maintenanceService.startTask(id, technician);
            redirectAttrs.addFlashAttribute("success", "Task started successfully");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/technician/tasks";
    }

    @PostMapping("/tasks/{id}/complete")
    public String completeTask(
            @PathVariable Long id,
            @RequestParam String notes,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttrs) {

        User technician = getCurrentUser(userDetails);
        try {
            maintenanceService.completeTask(id, notes, null, technician);
            redirectAttrs.addFlashAttribute("success", "Task marked as complete");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/technician/tasks";
    }

    @GetMapping("/tasks/{id}")
    public String taskDetail(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        User technician = getCurrentUser(userDetails);
        MaintenanceRequest task = maintenanceRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        model.addAttribute("technician", technician);
        model.addAttribute("task", task);
        return "technician/task-detail";
    }
}
