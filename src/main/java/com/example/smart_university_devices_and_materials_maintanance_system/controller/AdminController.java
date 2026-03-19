package com.example.smart_university_devices_and_materials_maintanance_system.controller;

import com.example.smart_university_devices_and_materials_maintanance_system.entities.*;
import com.example.smart_university_devices_and_materials_maintanance_system.repositories.*;
import com.example.smart_university_devices_and_materials_maintanance_system.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final DeviceService deviceService;
    private final MaterialService materialService;
    private final MaintenanceService maintenanceService;
    private final NotificationService notificationService;
    private final UniversityRepository universityRepository;
    private final UserRepository userRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final NavigationService navigationService;

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    }

    private void addSmartNavigation(Model model, User currentUser, HttpServletRequest request) {
        String currentPath = request.getRequestURI();
        model.addAttribute("navigationItems", navigationService.getSmartNavigationItems(currentUser, currentPath));
        model.addAttribute("pageTitle", navigationService.getPageTitle(currentPath, currentUser));
        model.addAttribute("pageSubtitle", navigationService.getPageSubtitle(currentPath, currentUser));
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request, Model model) {
        User admin = getCurrentUser(userDetails);

        // Add smart navigation
        addSmartNavigation(model, admin, request);

        model.addAttribute("admin", admin);
        model.addAttribute("totalDevices", deviceService.countByUniversity(
                admin.getUniversity() != null ? admin.getUniversity().getId() : 0L));
        model.addAttribute("pendingRequests", maintenanceService.countByStatus(MaintenanceRequest.TaskStatus.PENDING));
        model.addAttribute("inProgressRequests",
                maintenanceService.countByStatus(MaintenanceRequest.TaskStatus.IN_PROGRESS));
        model.addAttribute("completedRequests",
                maintenanceService.countByStatus(MaintenanceRequest.TaskStatus.COMPLETED));
        model.addAttribute("pendingUsers", userService.getPendingApprovals().size());
        model.addAttribute("pendingDevices", deviceService.getPendingApproval().size());
        model.addAttribute("damagedDevices", deviceService.countDamaged());
        model.addAttribute("overdueTasks", maintenanceService.getOverdueTasks().size());
        model.addAttribute("recentRequests", maintenanceRequestRepository.findAll()
                .stream().sorted((a, b) -> b.getReportedAt().compareTo(a.getReportedAt()))
                .limit(5).toList());
        model.addAttribute("notifications", notificationService.getAllForUser(admin.getId()));
        model.addAttribute("unreadCount", notificationService.countUnread(admin.getId()));
        return "admin/dashboard";
    }

    // ── User Management ───────────────────────────────────────────────────────

    @GetMapping("/users")
    public String users(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullName") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String roleFilter,
            @RequestParam(required = false) String statusFilter,
            Model model) {
        
        User admin = getCurrentUser(userDetails);
        
        // Add smart navigation
        addSmartNavigation(model, admin, request);
        
        // Create pageable request
        org.springframework.data.domain.Pageable pageable = 
            org.springframework.data.domain.PageRequest.of(page, size, 
                org.springframework.data.domain.Sort.Direction.fromString(direction), sort);
        
        // Get filtered and paginated users
        org.springframework.data.domain.Page<User> userPage = userService.getUsersWithFilters(
            search, roleFilter, statusFilter, pageable);
        
        model.addAttribute("admin", admin);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("pendingUsers", userService.getPendingApprovals());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        model.addAttribute("search", search);
        model.addAttribute("roleFilter", roleFilter);
        model.addAttribute("statusFilter", statusFilter);
        
        return "admin/users";
    }

    @PostMapping("/users/{id}/approve")
    public String approveUser(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        userService.approveUser(id);
        redirectAttrs.addFlashAttribute("success", "User approved successfully");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/suspend")
    public String suspendUser(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        userService.suspendUser(id);
        redirectAttrs.addFlashAttribute("success", "User suspended successfully");
        return "redirect:/admin/users";
    }

    // ── Device Management ─────────────────────────────────────────────────────

    @GetMapping("/devices")
    public String devices(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request, Model model) {
        User admin = getCurrentUser(userDetails);
        
        // Add smart navigation
        addSmartNavigation(model, admin, request);
        model.addAttribute("admin", admin);
        model.addAttribute("devices", deviceService.getAll());
        model.addAttribute("pendingDevices", deviceService.getPendingApproval());
        return "admin/devices";
    }

    @PostMapping("/devices/{id}/approve")
    public String approveDevice(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        deviceService.approveDevice(id);
        redirectAttrs.addFlashAttribute("success", "Device approved successfully");
        return "redirect:/admin/devices";
    }

    // ── Material Management ───────────────────────────────────────────────────

    @GetMapping("/materials")
    public String materials(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request, Model model) {
        User admin = getCurrentUser(userDetails);
        
        // Add smart navigation
        addSmartNavigation(model, admin, request);
        model.addAttribute("admin", admin);
        model.addAttribute("materials", materialService.getAll());
        model.addAttribute("pendingMaterials", materialService.getPendingApproval());
        return "admin/materials";
    }

    @PostMapping("/materials/{id}/approve")
    public String approveMaterial(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        materialService.approveMaterial(id);
        redirectAttrs.addFlashAttribute("success", "Material approved successfully");
        return "redirect:/admin/materials";
    }

    // ── Maintenance Requests ──────────────────────────────────────────────────

    @GetMapping("/maintenance")
    public String maintenance(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request, Model model) {
        User admin = getCurrentUser(userDetails);
        
        // Add smart navigation
        addSmartNavigation(model, admin, request);
        model.addAttribute("admin", admin);
        model.addAttribute("requests", maintenanceRequestRepository.findAll());
        model.addAttribute("technicians", userService.getAllTechnicians());
        model.addAttribute("statuses", MaintenanceRequest.TaskStatus.values());
        return "admin/maintenance";
    }

    @PostMapping("/maintenance/{id}/assign")
    public String assignTask(
            @PathVariable Long id,
            @RequestParam Long technicianId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDate,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttrs) {

        User admin = getCurrentUser(userDetails);
        maintenanceService.assignTechnician(id, technicianId, dueDate, admin);
        redirectAttrs.addFlashAttribute("success", "Task assigned to technician");
        return "redirect:/admin/maintenance";
    }

    @PostMapping("/maintenance/{id}/verify")
    public String verifyTask(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttrs) {
        User admin = getCurrentUser(userDetails);
        maintenanceService.verifyCompletion(id, admin);
        redirectAttrs.addFlashAttribute("success", "Task verified successfully");
        return "redirect:/admin/maintenance";
    }

    // ── Analytics ─────────────────────────────────────────────────────────────

    @GetMapping("/analytics")
    public String analytics(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request, Model model) {
        User admin = getCurrentUser(userDetails);
        
        // Add smart navigation
        addSmartNavigation(model, admin, request);
        model.addAttribute("admin", admin);
        model.addAttribute("technicianPerformance", maintenanceService.getTechnicianPerformance());
        model.addAttribute("totalDevices", deviceService.countDamaged());
        model.addAttribute("pendingCount", maintenanceService.countByStatus(MaintenanceRequest.TaskStatus.PENDING));
        model.addAttribute("inProgressCount",
                maintenanceService.countByStatus(MaintenanceRequest.TaskStatus.IN_PROGRESS));
        model.addAttribute("completedCount", maintenanceService.countByStatus(MaintenanceRequest.TaskStatus.COMPLETED));
        model.addAttribute("verifiedCount", maintenanceService.countByStatus(MaintenanceRequest.TaskStatus.VERIFIED));
        model.addAttribute("universities", universityRepository.findAll());
        return "admin/analytics";
    }

    // ── Notifications ─────────────────────────────────────────────────────────

    @PostMapping("/notifications/mark-read")
    public String markNotificationsRead(@AuthenticationPrincipal UserDetails userDetails) {
        User admin = getCurrentUser(userDetails);
        notificationService.markAllRead(admin.getId());
        return "redirect:/admin/dashboard";
    }
}
