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

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@RequestMapping("/technician")
@RequiredArgsConstructor
public class TechnicianController {

    private final UserRepository userRepository;
    private final MaintenanceService maintenanceService;
    private final NotificationService notificationService;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final NavigationService navigationService;

    private User getCurrentUser(Principal principal) {
        if (principal == null) throw new RuntimeException("Not authenticated");
        return userRepository.findByEmail(principal.getName()).orElseThrow();
    }

    private void addSmartNavigation(Model model, User currentUser, HttpServletRequest request) {
        String currentPath = request.getRequestURI();
        model.addAttribute("navigationItems", navigationService.getSmartNavigationItems(currentUser, currentPath));
        model.addAttribute("pageTitle", navigationService.getPageTitle(currentPath, currentUser));
        model.addAttribute("pageSubtitle", navigationService.getPageSubtitle(currentPath, currentUser));
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, HttpServletRequest request, Model model) {
        try {
            User technician = getCurrentUser(principal);
            addSmartNavigation(model, technician, request);
            model.addAttribute("technician", technician);
            
            var allTasks = maintenanceService.getByTechnician(technician.getId());
            model.addAttribute("assignedTasks", allTasks);
            model.addAttribute("pendingCount", allTasks.stream().filter(t -> t.getStatus() == MaintenanceRequest.TaskStatus.ASSIGNED).count());
            model.addAttribute("inProgressCount", allTasks.stream().filter(t -> t.getStatus() == MaintenanceRequest.TaskStatus.IN_PROGRESS).count());
            model.addAttribute("completedCount", allTasks.stream().filter(t -> t.getStatus() == MaintenanceRequest.TaskStatus.COMPLETED).count());
            model.addAttribute("notifications", notificationService.getAllForUser(technician.getId()));
            model.addAttribute("unreadCount", notificationService.countUnread(technician.getId()));
            
            return "technician/dashboard";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @GetMapping("/tasks")
    public String tasks(Principal principal, HttpServletRequest request, Model model) {
        try {
            User technician = getCurrentUser(principal);
            addSmartNavigation(model, technician, request);
            model.addAttribute("technician", technician);
            model.addAttribute("tasks", maintenanceService.getByTechnician(technician.getId()));
            return "technician/tasks";
        } catch (Exception e) {
            return "redirect:/technician/dashboard";
        }
    }

    @PostMapping("/tasks/{id}/start")
    public String startTask(@PathVariable Long id,
            Principal principal,
            HttpServletRequest request,
            RedirectAttributes redirectAttrs) {
        try {
            User technician = getCurrentUser(principal);
            maintenanceService.startTask(id, technician);
            redirectAttrs.addFlashAttribute("success", "Task started successfully");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/technician/tasks");
    }

    @PostMapping("/tasks/{id}/complete")
    public String completeTask(
            @PathVariable Long id,
            @RequestParam String notes,
            Principal principal,
            HttpServletRequest request,
            RedirectAttributes redirectAttrs) {

        try {
            User technician = getCurrentUser(principal);
            maintenanceService.completeTask(id, notes, null, technician);
            redirectAttrs.addFlashAttribute("success", "Task marked as complete");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/technician/tasks");
    }

    @GetMapping("/tasks/{id}")
    public String taskDetail(@PathVariable Long id,
            Principal principal,
            HttpServletRequest request,
            Model model) {
        try {
            User technician = getCurrentUser(principal);
            addSmartNavigation(model, technician, request);
            
            MaintenanceRequest task = maintenanceRequestRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Task not found"));
            model.addAttribute("technician", technician);
            model.addAttribute("task", task);
            return "technician/task-detail";
        } catch (Exception e) {
            return "redirect:/technician/tasks";
        }
    }

    @GetMapping("/learning")
    public String learning(Principal principal, HttpServletRequest request, Model model) {
        try {
            User technician = getCurrentUser(principal);
            addSmartNavigation(model, technician, request);
            model.addAttribute("technician", technician);
            return "technician/learning";
        } catch (Exception e) {
            return "redirect:/technician/dashboard";
        }
    }
}
