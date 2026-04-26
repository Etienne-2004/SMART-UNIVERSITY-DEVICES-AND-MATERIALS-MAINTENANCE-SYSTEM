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
import org.springframework.transaction.annotation.Transactional;

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

    private void addSmartNavigation(Model model, User currentUser, HttpServletRequest request) {
        String currentPath = request.getRequestURI();
        model.addAttribute("navigationItems", navigationService.getSmartNavigationItems(currentUser, currentPath));
        model.addAttribute("pageTitle", navigationService.getPageTitle(currentPath, currentUser));
        model.addAttribute("pageSubtitle", navigationService.getPageSubtitle(currentPath, currentUser));
    }

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    }

    @Transactional
    private void createSampleTechnician() {
        // Create a sample technician if none exist
        User technician = User.builder()
                .username("tech001")
                .email("tech001@smartuni.rw")
                .password("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa") // Tech@2026
                .fullName("John Technician")
                .phoneNumber("0789000001")
                .role(User.Role.TECHNICIAN)
                .accountStatus(User.AccountStatus.ACTIVE)
                .mfaEnabled(false)
                .build();
        
        userRepository.save(technician);
    }

    @Transactional
    private void createSampleMaintenanceRequests() {
        // Create sample maintenance requests for testing
        MaintenanceRequest request1 = MaintenanceRequest.builder()
                .requestType(MaintenanceRequest.RequestType.DEVICE)
                .issueDescription("Screen not working properly, flickering issue")
                .priority(MaintenanceRequest.Priority.HIGH)
                .status(MaintenanceRequest.TaskStatus.PENDING)
                .reportedAt(LocalDateTime.now().minusDays(2))
                .build();

        MaintenanceRequest request2 = MaintenanceRequest.builder()
                .requestType(MaintenanceRequest.RequestType.MATERIAL)
                .issueDescription("Bulb needs replacement, dim display")
                .priority(MaintenanceRequest.Priority.MEDIUM)
                .status(MaintenanceRequest.TaskStatus.PENDING)
                .reportedAt(LocalDateTime.now().minusDays(1))
                .build();

        MaintenanceRequest request3 = MaintenanceRequest.builder()
                .requestType(MaintenanceRequest.RequestType.DEVICE)
                .issueDescription("Paper jam and not printing")
                .priority(MaintenanceRequest.Priority.LOW)
                .status(MaintenanceRequest.TaskStatus.PENDING)
                .reportedAt(LocalDateTime.now())
                .build();

        maintenanceRequestRepository.save(request1);
        maintenanceRequestRepository.save(request2);
        maintenanceRequestRepository.save(request3);
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request, Model model) {
        User admin = getCurrentUser(userDetails);

        // Add smart navigation
        addSmartNavigation(model, admin, request);

        model.addAttribute("admin", admin);
        
        // Load real data from database
        try {
            Long universityId = admin.getUniversity() != null ? admin.getUniversity().getId() : 0L;
            model.addAttribute("totalDevices", deviceService.countByUniversity(universityId));
            model.addAttribute("pendingRequests", maintenanceService.countByStatus(MaintenanceRequest.TaskStatus.PENDING));
            model.addAttribute("inProgressRequests", maintenanceService.countByStatus(MaintenanceRequest.TaskStatus.IN_PROGRESS));
            model.addAttribute("completedRequests", maintenanceService.countByStatus(MaintenanceRequest.TaskStatus.COMPLETED));
            model.addAttribute("pendingUsers", userService.getPendingApprovals().size());
            model.addAttribute("pendingDevices", deviceService.getPendingApproval().size());
            model.addAttribute("damagedDevices", deviceService.countDamaged());
            model.addAttribute("overdueTasks", maintenanceService.getOverdueTasks().size());
            model.addAttribute("recentRequests", maintenanceRequestRepository.findAll()
                    .stream().sorted((a, b) -> b.getReportedAt().compareTo(a.getReportedAt()))
                    .limit(5).toList());
            model.addAttribute("notifications", notificationService.getAllForUser(admin.getId()));
            model.addAttribute("unreadCount", notificationService.countUnread(admin.getId()));
        } catch (Exception e) {
            // Fallback to defaults if database fails
            model.addAttribute("totalDevices", 0L);
            model.addAttribute("pendingRequests", 0L);
            model.addAttribute("inProgressRequests", 0L);
            model.addAttribute("completedRequests", 0L);
            model.addAttribute("pendingUsers", 0);
            model.addAttribute("pendingDevices", 0);
            model.addAttribute("damagedDevices", 0L);
            model.addAttribute("overdueTasks", 0);
            model.addAttribute("recentRequests", java.util.Collections.emptyList());
            model.addAttribute("notifications", java.util.Collections.emptyList());
            model.addAttribute("unreadCount", 0L);
        }
        
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
        
        addSmartNavigation(model, admin, request);
        
        try {
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
        } catch (Exception e) {
            model.addAttribute("admin", admin);
            model.addAttribute("users", java.util.Collections.emptyList());
            model.addAttribute("pendingUsers", java.util.Collections.emptyList());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0L);
            model.addAttribute("size", size);
            model.addAttribute("sort", sort);
            model.addAttribute("direction", direction);
            model.addAttribute("search", search);
            model.addAttribute("roleFilter", roleFilter);
            model.addAttribute("statusFilter", statusFilter);
        }
        
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

    @PostMapping("/users/{id}/edit")
    public String editUser(
            @PathVariable Long id,
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String role,
            @RequestParam String accountStatus,
            RedirectAttributes redirectAttrs) {
        try {
            userService.updateUser(id, fullName, email, role, accountStatus);
            redirectAttrs.addFlashAttribute("success", "User updated successfully");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Failed to update user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            userService.deleteUser(id);
            redirectAttrs.addFlashAttribute("success", "User deleted successfully");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Failed to delete user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ── Device Management ─────────────────────────────────────────────────────

    @GetMapping("/devices")
    public String devices(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request, Model model) {
        User admin = getCurrentUser(userDetails);
        
        addSmartNavigation(model, admin, request);
        
        try {
            model.addAttribute("admin", admin);
            model.addAttribute("devices", deviceService.getAll());
            model.addAttribute("pendingDevices", deviceService.getPendingApproval());
        } catch (Exception e) {
            model.addAttribute("admin", admin);
            model.addAttribute("devices", java.util.Collections.emptyList());
            model.addAttribute("pendingDevices", java.util.Collections.emptyList());
        }
        return "admin/devices";
    }

    @PostMapping("/devices/{id}/approve")
    public String approveDevice(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        deviceService.approveDevice(id);
        redirectAttrs.addFlashAttribute("success", "Device approved successfully");
        return "redirect:/admin/devices";
    }

    @PostMapping("/devices/{id}/edit")
    public String editDevice(
            @PathVariable Long id,
            @RequestParam String deviceName,
            @RequestParam String deviceId,
            @RequestParam(required = false) String roomLocation,
            @RequestParam(required = false) String description,
            @RequestParam String status,
            RedirectAttributes redirectAttrs) {
        try {
            deviceService.updateDevice(id, deviceName, deviceId, roomLocation, description, status);
            redirectAttrs.addFlashAttribute("success", "Device updated successfully");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Failed to update device: " + e.getMessage());
        }
        return "redirect:/admin/devices";
    }

    @PostMapping("/devices/{id}/delete")
    public String deleteDevice(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            deviceService.deleteDevice(id);
            redirectAttrs.addFlashAttribute("success", "Device deleted successfully");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Failed to delete device: " + e.getMessage());
        }
        return "redirect:/admin/devices";
    }

    // ── Material Management ───────────────────────────────────────────────────

    @GetMapping("/materials")
    public String materials(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request, Model model) {
        User admin = getCurrentUser(userDetails);
        
        addSmartNavigation(model, admin, request);
        
        try {
            model.addAttribute("admin", admin);
            model.addAttribute("materials", materialService.getAll());
            model.addAttribute("pendingMaterials", materialService.getPendingApproval());
        } catch (Exception e) {
            model.addAttribute("admin", admin);
            model.addAttribute("materials", java.util.Collections.emptyList());
            model.addAttribute("pendingMaterials", java.util.Collections.emptyList());
        }
        return "admin/materials";
    }

    @PostMapping("/materials/{id}/approve")
    public String approveMaterial(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        materialService.approveMaterial(id);
        redirectAttrs.addFlashAttribute("success", "Material approved successfully");
        return "redirect:/admin/materials";
    }

    @PostMapping("/materials/{id}/edit")
    public String editMaterial(
            @PathVariable Long id,
            @RequestParam String materialName,
            @RequestParam(required = false) String roomLocation,
            @RequestParam(required = false) String description,
            @RequestParam String status,
            RedirectAttributes redirectAttrs) {
        try {
            materialService.updateMaterial(id, materialName, roomLocation, description, status);
            redirectAttrs.addFlashAttribute("success", "Material updated successfully");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Failed to update material: " + e.getMessage());
        }
        return "redirect:/admin/materials";
    }

    @PostMapping("/materials/{id}/delete")
    public String deleteMaterial(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            materialService.deleteMaterial(id);
            redirectAttrs.addFlashAttribute("success", "Material deleted successfully");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Failed to delete material: " + e.getMessage());
        }
        return "redirect:/admin/materials";
    }

    // ── Maintenance Requests ──────────────────────────────────────────────────

    @GetMapping("/maintenance")
    public String maintenance(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request, Model model) {
        User admin = getCurrentUser(userDetails);
        
        addSmartNavigation(model, admin, request);
        
        try {
            model.addAttribute("admin", admin);
            
            // Get requests and create sample if none exist
            List<MaintenanceRequest> requests = maintenanceRequestRepository.findAll();
            if (requests.isEmpty()) {
                createSampleMaintenanceRequests();
                requests = maintenanceRequestRepository.findAll();
            }
            model.addAttribute("requests", requests);
            
            // Get technicians and create sample if none exist
            List<User> technicians = userService.getAllTechnicians();
            if (technicians.isEmpty()) {
                createSampleTechnician();
                technicians = userService.getAllTechnicians();
            }
            model.addAttribute("technicians", technicians);
            model.addAttribute("statuses", MaintenanceRequest.TaskStatus.values());
        } catch (Exception e) {
            model.addAttribute("admin", admin);
            model.addAttribute("requests", java.util.Collections.emptyList());
            model.addAttribute("technicians", java.util.Collections.emptyList());
            model.addAttribute("statuses", MaintenanceRequest.TaskStatus.values());
        }
        return "admin/maintenance";
    }

    @PostMapping("/maintenance/{id}/assign")
    public String assignTask(
            @PathVariable Long id,
            @RequestParam Long technicianId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dueDate,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttrs) {

        User admin = getCurrentUser(userDetails);
        try {
            maintenanceService.assignTechnician(id, technicianId, dueDate, admin);
            redirectAttrs.addFlashAttribute("success", "Task assigned to technician successfully");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Assignment failed: " + e.getMessage());
        }
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

    @PostMapping("/maintenance/bulk-assign")
    public String bulkAssignTasks(
            @RequestParam Long technicianId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dueDate,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttrs) {
        User admin = getCurrentUser(userDetails);
        
        try {
            // Get all pending requests
            List<MaintenanceRequest> pendingRequests = maintenanceRequestRepository.findByStatus(MaintenanceRequest.TaskStatus.PENDING);
            
            if (pendingRequests.isEmpty()) {
                redirectAttrs.addFlashAttribute("success", "No pending tasks to assign");
                return "redirect:/admin/maintenance";
            }
            
            // Get technician
            User technician = userRepository.findById(technicianId)
                .orElseThrow(() -> new IllegalArgumentException("Technician not found"));
            
            int assignedCount = 0;
            for (MaintenanceRequest request : pendingRequests) {
                try {
                    maintenanceService.assignTechnician(request.getId(), technicianId, dueDate, admin);
                    assignedCount++;
                } catch (Exception e) {
                    // Skip this request if assignment fails, continue with others
                    continue;
                }
            }
            
            redirectAttrs.addFlashAttribute("success", 
                "Successfully assigned " + assignedCount + " tasks to " + technician.getFullName());
                
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Bulk assignment failed: " + e.getMessage());
        }
        
        return "redirect:/admin/maintenance";
    }

    // ── Analytics ─────────────────────────────────────────────────────────────

    @GetMapping("/analytics")
    public String analytics(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request, Model model) {
        User admin = getCurrentUser(userDetails);
        
        addSmartNavigation(model, admin, request);
        
        try {
            model.addAttribute("admin", admin);
            model.addAttribute("technicianPerformance", maintenanceService.getTechnicianPerformance());
            model.addAttribute("totalDevices", deviceService.countDamaged());
            model.addAttribute("pendingCount", maintenanceService.countByStatus(MaintenanceRequest.TaskStatus.PENDING));
            model.addAttribute("inProgressCount",
                    maintenanceService.countByStatus(MaintenanceRequest.TaskStatus.IN_PROGRESS));
            model.addAttribute("completedCount", maintenanceService.countByStatus(MaintenanceRequest.TaskStatus.COMPLETED));
            model.addAttribute("verifiedCount", maintenanceService.countByStatus(MaintenanceRequest.TaskStatus.VERIFIED));
            model.addAttribute("universities", universityRepository.findAll());
        } catch (Exception e) {
            model.addAttribute("admin", admin);
            model.addAttribute("technicianPerformance", java.util.Collections.emptyList());
            model.addAttribute("totalDevices", 0L);
            model.addAttribute("pendingCount", 0L);
            model.addAttribute("inProgressCount", 0L);
            model.addAttribute("completedCount", 0L);
            model.addAttribute("verifiedCount", 0L);
            model.addAttribute("universities", java.util.Collections.emptyList());
        }
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
