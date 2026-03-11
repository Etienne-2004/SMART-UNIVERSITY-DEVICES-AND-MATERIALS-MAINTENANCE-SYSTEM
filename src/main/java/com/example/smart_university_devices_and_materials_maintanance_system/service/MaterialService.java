package com.example.smart_university_devices_and_materials_maintanance_system.service;

import com.example.smart_university_devices_and_materials_maintanance_system.entities.*;
import com.example.smart_university_devices_and_materials_maintanance_system.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final NotificationService notificationService;

    @Transactional
    public Material addMaterial(Material material) {
        if (material == null)
            throw new IllegalArgumentException("Material cannot be null");
        return materialRepository.save(material);
    }

    public Optional<Material> findById(Long id) {
        return materialRepository.findById(id != null ? id : 0L);
    }

    public List<Material> getByUniversity(Long universityId) {
        return materialRepository.findByUniversityId(universityId != null ? universityId : 0L);
    }

    public List<Material> getPendingApproval() {
        return materialRepository.findByAdminApproved(false);
    }

    @Transactional
    public Material approveMaterial(Long materialId) {
        Material material = materialRepository.findById(materialId != null ? materialId : 0L)
                .orElseThrow(() -> new IllegalArgumentException("Material not found"));
        material.setAdminApproved(true);

        if (material.getReportedBy() != null) {
            notificationService.send(material.getReportedBy(),
                    "✅ Material Registration Approved",
                    "Your material registration request has been approved.",
                    Notification.NotificationType.APPROVED, null);
        }
        return materialRepository.save(material);
    }

    public List<Material> getAll() {
        return materialRepository.findAll();
    }

    public List<Material> getByTechnician(Long technicianId) {
        return materialRepository.findByAssignedTechnicianId(technicianId != null ? technicianId : 0L);
    }

    public List<Material> getReportedByUser(Long userId) {
        return materialRepository.findByReportedById(userId != null ? userId : 0L);
    }

    public long countPending() {
        return materialRepository.countPendingMaterials();
    }
}
