package com.example.smart_university_devices_and_materials_maintanance_system.service;

import com.example.smart_university_devices_and_materials_maintanance_system.entities.*;
import com.example.smart_university_devices_and_materials_maintanance_system.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getPendingApprovals() {
        return userRepository.findByAccountStatus(User.AccountStatus.PENDING);
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

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }
}
