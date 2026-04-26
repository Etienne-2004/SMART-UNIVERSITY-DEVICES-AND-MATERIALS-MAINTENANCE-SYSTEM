package com.example.smart_university_devices_and_materials_maintanance_system.repositories;

import com.example.smart_university_devices_and_materials_maintanance_system.entities.Device;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    @EntityGraph(attributePaths = {"university", "college", "assignedTechnician", "reportedBy"})
    @org.springframework.lang.NonNull
    List<Device> findAll();

    Optional<Device> findByDeviceId(String deviceId);

    boolean existsByDeviceId(String deviceId);

    List<Device> findByUniversityId(Long universityId);

    List<Device> findByUniversityIdAndCollegeId(Long universityId, Long collegeId);

    List<Device> findByAssignedTechnicianId(Long technicianId);

    List<Device> findByReportedById(Long userId);

    List<Device> findByAdminApproved(boolean approved);

    @Query("SELECT COUNT(d) FROM Device d WHERE d.university.id = :universityId")
    long countByUniversity(Long universityId);

    @Query("SELECT COUNT(d) FROM Device d WHERE d.status = 'INACTIVE' OR d.status = 'UNDER_REPAIR'")
    long countDamagedDevices();
}
