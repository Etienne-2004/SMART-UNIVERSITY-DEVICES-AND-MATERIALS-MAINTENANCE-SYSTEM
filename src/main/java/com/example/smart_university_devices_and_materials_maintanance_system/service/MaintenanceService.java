package com.example.smart_university_devices_and_materials_maintanance_system.service;

import com.example.smart_university_devices_and_materials_maintanance_system.entities.*;
import com.example.smart_university_devices_and_materials_maintanance_system.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaintenanceService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final MaterialRepository materialRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Transactional
    public MaintenanceRequest reportDeviceIssue(Device device, String description,
            MaintenanceRequest.Priority priority,
            User reportedBy) {
        MaintenanceRequest request = MaintenanceRequest.builder()
                .requestType(MaintenanceRequest.RequestType.DEVICE)
                .device(device)
                .issueDescription(description)
                .priority(priority)
                .status(MaintenanceRequest.TaskStatus.PENDING)
                .reportedBy(reportedBy)
                .university(device.getUniversity())
                .college(device.getCollege())
                .build();

        MaintenanceRequest saved = maintenanceRequestRepository.save(request);

        // Notify all admins
        notifyAdmins(reportedBy.getUniversity(),
                "New Device Issue Reported",
                "Device issue reported by " + reportedBy.getFullName() + ": " + description,
                saved, Notification.NotificationType.SYSTEM);

        return saved;
    }

    @Transactional
    public MaintenanceRequest reportMaterialIssue(Material material, String description,
            MaintenanceRequest.Priority priority,
            User reportedBy) {
        MaintenanceRequest request = MaintenanceRequest.builder()
                .requestType(MaintenanceRequest.RequestType.MATERIAL)
                .material(material)
                .issueDescription(description)
                .priority(priority)
                .status(MaintenanceRequest.TaskStatus.PENDING)
                .reportedBy(reportedBy)
                .university(material.getUniversity())
                .college(material.getCollege())
                .build();

        MaintenanceRequest saved = maintenanceRequestRepository.save(request);

        notifyAdmins(reportedBy.getUniversity(),
                "New Material Issue Reported",
                "Material issue reported by " + reportedBy.getFullName() + ": " + description,
                saved, Notification.NotificationType.SYSTEM);

        return saved;
    }

    @Transactional
    public MaintenanceRequest reportMaintenanceIssue(
            MaintenanceRequest.RequestType requestType,
            String deviceName,
            String materialName,
            String issueDescription,
            MaintenanceRequest.Priority priority,
            String roomLocation,
            User reportedBy) {
        
        MaintenanceRequest request = MaintenanceRequest.builder()
                .requestType(requestType)
                .issueDescription(issueDescription)
                .priority(priority)
                .status(MaintenanceRequest.TaskStatus.PENDING)
                .reportedBy(reportedBy)
                .reportedAt(LocalDateTime.now())
                .university(reportedBy.getUniversity())
                .college(reportedBy.getCollege())
                .build();

        // Store device/material info in description for now since we don't have device/material entities
        String fullDescription = issueDescription;
        if (requestType == MaintenanceRequest.RequestType.DEVICE && deviceName != null) {
            fullDescription = "Device: " + deviceName + " - " + issueDescription;
        } else if (requestType == MaintenanceRequest.RequestType.MATERIAL && materialName != null) {
            fullDescription = "Material: " + materialName + " - " + issueDescription;
        }
        request.setIssueDescription(fullDescription);

        MaintenanceRequest saved = maintenanceRequestRepository.save(request);

        notifyAdmins(reportedBy.getUniversity(),
                "New Maintenance Issue Reported",
                "Issue reported by " + reportedBy.getFullName() + ": " + fullDescription,
                saved, Notification.NotificationType.SYSTEM);

        return saved;
    }

    @Transactional
    public MaintenanceRequest assignTechnician(Long requestId, Long technicianId,
            LocalDateTime dueDate, User admin) {
        if (requestId == null)
            throw new IllegalArgumentException("Request ID cannot be null");
        MaintenanceRequest request = maintenanceRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found: " + requestId));

        User technician = userRepository.findById(technicianId)
                .orElseThrow(() -> new IllegalArgumentException("Technician not found: " + technicianId));

        request.setAssignedTechnician(technician);
        request.setStatus(MaintenanceRequest.TaskStatus.ASSIGNED);
        request.setAssignedAt(LocalDateTime.now());
        request.setDueDate(dueDate);

        MaintenanceRequest saved = maintenanceRequestRepository.save(request);

        // Notify technician
        notificationService.send(technician,
                "🔧 New Task Assigned",
                "You have been assigned a maintenance task. Issue: " + request.getIssueDescription(),
                Notification.NotificationType.TASK_ASSIGNED, saved);

        return saved;
    }

    @Transactional
    public MaintenanceRequest startTask(Long requestId, User technician) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (!request.getAssignedTechnician().getId().equals(technician.getId())) {
            throw new SecurityException("You are not assigned to this task");
        }

        request.setStatus(MaintenanceRequest.TaskStatus.IN_PROGRESS);
        request.setStartedAt(LocalDateTime.now());

        return maintenanceRequestRepository.save(request);
    }

    @Transactional
    public MaintenanceRequest completeTask(Long requestId, String technicianNotes,
            String photoPath, User technician) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (!request.getAssignedTechnician().getId().equals(technician.getId())) {
            throw new SecurityException("You are not assigned to this task");
        }

        request.setStatus(MaintenanceRequest.TaskStatus.COMPLETED);
        request.setCompletedAt(LocalDateTime.now());
        request.setTechnicianNotes(technicianNotes);
        request.setPhotoPath(photoPath);

        MaintenanceRequest saved = maintenanceRequestRepository.save(request);

        // Notify admins
        notifyAdmins(technician.getUniversity(),
                "✅ Task Completed",
                "Task completed by " + technician.getFullName() + ". Notes: " + technicianNotes,
                saved, Notification.NotificationType.TASK_COMPLETED);

        return saved;
    }

    @Transactional
    public MaintenanceRequest verifyCompletion(Long requestId, User admin) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        request.setStatus(MaintenanceRequest.TaskStatus.VERIFIED);
        request.setVerifiedAt(LocalDateTime.now());
        request.setVerifiedBy(admin);

        MaintenanceRequest saved = maintenanceRequestRepository.save(request);

        if (request.getMaterial() != null) {
            Material m = request.getMaterial();
            m.setStatus(Material.MaterialStatus.VERIFIED);
            materialRepository.save(m);
        }

        // Notify technician
        if (request.getAssignedTechnician() != null) {
            notificationService.send(request.getAssignedTechnician(),
                    "✔ Task Verified",
                    "Your completed task has been verified by admin.",
                    Notification.NotificationType.TASK_VERIFIED, saved);
        }

        return saved;
    }

    public List<MaintenanceRequest> getOverdueTasks() {
        return maintenanceRequestRepository.findOverdueTasks(LocalDateTime.now());
    }

    private void notifyAdmins(University university, String title, String message,
            MaintenanceRequest request, Notification.NotificationType type) {
        if (university != null) {
            userRepository.findByUniversityIdAndRole(university.getId(), User.Role.ADMIN)
                    .forEach(admin -> notificationService.send(admin, title, message, type, request));
        }
    }

    // ── Analytics ─────────────────────────────────────────────────────────────

    public long countByStatus(MaintenanceRequest.TaskStatus status) {
        return maintenanceRequestRepository.countByStatus(status);
    }

    public List<MaintenanceRequest> getByUniversity(Long universityId) {
        return maintenanceRequestRepository.findByUniversityOrderByDate(universityId);
    }

    public List<MaintenanceRequest> getByTechnician(Long technicianId) {
        return maintenanceRequestRepository.findByAssignedTechnicianId(technicianId);
    }

    public List<MaintenanceRequest> getMyRequests(Long userId) {
        return maintenanceRequestRepository.findByReportedById(userId);
    }

    public List<Object[]> getTechnicianPerformance() {
        return maintenanceRequestRepository.getTechnicianPerformance();
    }
}
