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
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final NotificationService notificationService;

    @Transactional
    public Device addDevice(Device device) {
        if (deviceRepository.existsByDeviceId(device.getDeviceId())) {
            throw new IllegalArgumentException("Device ID already exists: " + device.getDeviceId());
        }
        return deviceRepository.save(device);
    }

    public Optional<Device> findById(Long id) {
        return deviceRepository.findById(id != null ? id : 0L);
    }

    public List<Device> getByUniversity(Long universityId) {
        return deviceRepository.findByUniversityId(universityId != null ? universityId : 0L);
    }

    public List<Device> getPendingApproval() {
        return deviceRepository.findByAdminApproved(false);
    }

    @Transactional
    public Device approveDevice(Long deviceId) {
        Device device = deviceRepository.findById(deviceId != null ? deviceId : 0L)
                .orElseThrow(() -> new IllegalArgumentException("Device not found"));
        device.setAdminApproved(true);

        if (device.getReportedBy() != null) {
            notificationService.send(device.getReportedBy(),
                    "✅ Device Registration Approved",
                    "Your device registration request (" + device.getDeviceId() + ") has been approved.",
                    Notification.NotificationType.APPROVED, null);
        }
        return deviceRepository.save(device);
    }

    @Transactional
    public Device updateDescription(Long deviceId, String description) {
        Device device = deviceRepository.findById(deviceId != null ? deviceId : 0L)
                .orElseThrow(() -> new IllegalArgumentException("Device not found"));
        device.setDescription(description);
        return deviceRepository.save(device);
    }

    public long countByUniversity(Long universityId) {
        return deviceRepository.countByUniversity(universityId != null ? universityId : 0L);
    }

    public long countDamaged() {
        return deviceRepository.countDamagedDevices();
    }

    public List<Device> getAll() {
        return deviceRepository.findAll();
    }

    public List<Device> getByTechnician(Long technicianId) {
        return deviceRepository.findByAssignedTechnicianId(technicianId != null ? technicianId : 0L);
    }

    public List<Device> getReportedByUser(Long userId) {
        return deviceRepository.findByReportedById(userId != null ? userId : 0L);
    }
}
