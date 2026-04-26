package com.example.smart_university_devices_and_materials_maintanance_system.service;

import com.example.smart_university_devices_and_materials_maintanance_system.entities.*;
import com.example.smart_university_devices_and_materials_maintanance_system.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getPendingApprovals() {
        return userRepository.findByAccountStatus(User.AccountStatus.PENDING_APPROVAL);
    }

    @Transactional
    public User approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setAccountStatus(User.AccountStatus.ACTIVE);
        return userRepository.save(user);
    }

    @Transactional
    public User suspendUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setAccountStatus(User.AccountStatus.SUSPENDED);
        return userRepository.save(user);
    }

    public List<User> getAllTechnicians() {
        return userRepository.findByRole(User.Role.TECHNICIAN);
    }

    public List<User> getTechniciansByUniversity(Long universityId) {
        return userRepository.findByUniversityIdAndRole(universityId, User.Role.TECHNICIAN);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Page<User> getUsersWithFilters(String search, String roleFilter, String statusFilter, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            // Search by name, email, or username
            if (roleFilter != null && !roleFilter.isEmpty() && statusFilter != null && !statusFilter.isEmpty()) {
                User.Role role = User.Role.valueOf(roleFilter);
                User.AccountStatus status = User.AccountStatus.valueOf(statusFilter);
                return userRepository.searchUsersWithRoleAndStatus(search.trim(), role, status, pageable);
            } else if (roleFilter != null && !roleFilter.isEmpty()) {
                User.Role role = User.Role.valueOf(roleFilter);
                return userRepository.searchUsersWithRole(search.trim(), role, pageable);
            } else if (statusFilter != null && !statusFilter.isEmpty()) {
                User.AccountStatus status = User.AccountStatus.valueOf(statusFilter);
                return userRepository.searchUsersWithStatus(search.trim(), status, pageable);
            } else {
                return userRepository.searchUsers(search.trim(), pageable);
            }
        } else {
            // Apply filters without search
            if (roleFilter != null && !roleFilter.isEmpty() && statusFilter != null && !statusFilter.isEmpty()) {
                User.Role role = User.Role.valueOf(roleFilter);
                User.AccountStatus status = User.AccountStatus.valueOf(statusFilter);
                return userRepository.findByRoleAndAccountStatus(role, status, pageable);
            } else if (roleFilter != null && !roleFilter.isEmpty()) {
                User.Role role = User.Role.valueOf(roleFilter);
                return userRepository.findByRole(role, pageable);
            } else if (statusFilter != null && !statusFilter.isEmpty()) {
                User.AccountStatus status = User.AccountStatus.valueOf(statusFilter);
                return userRepository.findByAccountStatus(status, pageable);
            } else {
                return userRepository.findAll(pageable);
            }
        }
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }

    @Transactional
    public User updateUser(Long id, String fullName, String email, String role, String accountStatus) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRole(User.Role.valueOf(role));
        user.setAccountStatus(User.AccountStatus.valueOf(accountStatus));
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userRepository.delete(user);
    }
}
