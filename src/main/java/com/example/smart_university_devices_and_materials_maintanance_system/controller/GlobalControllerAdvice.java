package com.example.smart_university_devices_and_materials_maintanance_system.controller;

import com.example.smart_university_devices_and_materials_maintanance_system.entities.User;
import com.example.smart_university_devices_and_materials_maintanance_system.repositories.UserRepository;
import com.example.smart_university_devices_and_materials_maintanance_system.service.DeviceService;
import com.example.smart_university_devices_and_materials_maintanance_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UserRepository userRepository;
    private final UserService userService;
    private final DeviceService deviceService;

    @ModelAttribute
    public void addGlobalAttributes(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            userRepository.findByEmail(userDetails.getUsername()).ifPresent(user -> {
                model.addAttribute("currentUser", user);
                model.addAttribute("currentRole", user.getRole().name());

                if (user.getRole() == User.Role.ADMIN) {
                    model.addAttribute("pendingUsersCount", userService.getPendingApprovals().size());
                    model.addAttribute("pendingDevicesCount", deviceService.getPendingApproval().size());
                }
            });
        }
    }
}
