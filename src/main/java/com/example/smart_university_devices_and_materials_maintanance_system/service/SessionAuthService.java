package com.example.smart_university_devices_and_materials_maintanance_system.service;

import com.example.smart_university_devices_and_materials_maintanance_system.entities.User;
import com.example.smart_university_devices_and_materials_maintanance_system.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionAuthService {

    private final UserRepository userRepository;

    public User getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new IllegalStateException("User not logged in");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    public User.Role getCurrentUserRole(HttpSession session) {
        User.Role role = (User.Role) session.getAttribute("userRole");
        if (role == null) {
            throw new IllegalStateException("User role not found in session");
        }
        return role;
    }

    public String getCurrentUserEmail(HttpSession session) {
        String email = (String) session.getAttribute("userEmail");
        if (email == null) {
            throw new IllegalStateException("User email not found in session");
        }
        return email;
    }

    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("userId") != null;
    }

    public void requireLogin(HttpSession session) {
        if (!isLoggedIn(session)) {
            throw new IllegalStateException("User must be logged in");
        }
    }
}
