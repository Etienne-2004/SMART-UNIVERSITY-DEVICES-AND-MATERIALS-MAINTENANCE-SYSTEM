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
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final UserRepository userRepository;
    private final DeviceService deviceService;
    private final MaterialService materialService;
    private final MaintenanceService maintenanceService;
    private final NotificationService notificationService;
    private final MaintenanceRequestRepository maintenanceRequestRepository;

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User staff = getCurrentUser(userDetails);
        model.addAttribute("staff", staff);
        model.addAttribute("myDevices", deviceService.getReportedByUser(staff.getId()));
        model.addAttribute("myMaterials", materialService.getReportedByUser(staff.getId()));
        model.addAttribute("myRequests", maintenanceRequestRepository.findByReportedById(staff.getId()));
        model.addAttribute("notifications", notificationService.getAllForUser(staff.getId()));
        model.addAttribute("unreadCount", notificationService.countUnread(staff.getId()));
        return "staff/dashboard";
    }

    @GetMapping("/devices/add")
    public String addDevicePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User staff = getCurrentUser(userDetails);
        model.addAttribute("staff", staff);
        return "staff/add-device";
    }

    @PostMapping("/devices/add")
    public String addDevice(
            @RequestParam String deviceId,
            @RequestParam String deviceName,
            @RequestParam String roomLocation,
            @RequestParam(required = false) String description,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttrs) {

        User staff = getCurrentUser(userDetails);
        try {
            Device device = Device.builder()
                    .deviceId(deviceId)
                    .deviceName(deviceName)
                    .roomLocation(roomLocation)
                    .description(description)
                    .university(staff.getUniversity())
                    .college(staff.getCollege())
                    .reportedBy(staff)
                    .build();
            deviceService.addDevice(device);
            redirectAttrs.addFlashAttribute("success", "Device registered successfully");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/staff/dashboard";
    }

    @GetMapping("/materials/add")
    public String addMaterialPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User staff = getCurrentUser(userDetails);
        model.addAttribute("staff", staff);
        return "staff/add-material";
    }

    @PostMapping("/materials/add")
    public String addMaterial(
            @RequestParam String materialName,
            @RequestParam String roomLocation,
            @RequestParam(required = false) String description,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttrs) {

        User staff = getCurrentUser(userDetails);
        try {
            Material material = Material.builder()
                    .materialName(materialName)
                    .roomLocation(roomLocation)
                    .description(description)
                    .university(staff.getUniversity())
                    .college(staff.getCollege())
                    .reportedBy(staff)
                    .build();
            materialService.addMaterial(material);
            redirectAttrs.addFlashAttribute("success", "Material reported successfully");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/staff/dashboard";
    }

    @PostMapping("/maintenance/report")
    public String reportIssue(
            @RequestParam String requestType,
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) Long materialId,
            @RequestParam String description,
            @RequestParam String priority,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttrs) {

        User staff = getCurrentUser(userDetails);
        try {
            if ("DEVICE".equals(requestType) && deviceId != null) {
                Device device = deviceService.findById(deviceId)
                        .orElseThrow(() -> new IllegalArgumentException("Device not found"));
                maintenanceService.reportDeviceIssue(device, description,
                        MaintenanceRequest.Priority.valueOf(priority), staff);
            } else if ("MATERIAL".equals(requestType) && materialId != null) {
                Material material = materialService.findById(materialId)
                        .orElseThrow(() -> new IllegalArgumentException("Material not found"));
                maintenanceService.reportMaterialIssue(material, description,
                        MaintenanceRequest.Priority.valueOf(priority), staff);
            }
            redirectAttrs.addFlashAttribute("success", "Issue reported successfully");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/staff/dashboard";
    }
}
