package com.example.smart_university_devices_and_materials_maintanance_system.repositories;

import com.example.smart_university_devices_and_materials_maintanance_system.entities.MaintenanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {

    List<MaintenanceRequest> findByStatus(MaintenanceRequest.TaskStatus status);

    List<MaintenanceRequest> findByAssignedTechnicianId(Long technicianId);

    List<MaintenanceRequest> findByReportedById(Long userId);

    List<MaintenanceRequest> findByUniversityId(Long universityId);

    List<MaintenanceRequest> findByUniversityIdAndStatus(Long universityId, MaintenanceRequest.TaskStatus status);

    List<MaintenanceRequest> findByAssignedTechnicianIdAndStatus(Long technicianId,
            MaintenanceRequest.TaskStatus status);

    @Query("SELECT m FROM MaintenanceRequest m WHERE m.status IN ('PENDING', 'ASSIGNED', 'IN_PROGRESS') AND m.dueDate < :now")
    List<MaintenanceRequest> findOverdueTasks(LocalDateTime now);

    @Query("SELECT COUNT(m) FROM MaintenanceRequest m WHERE m.status = :status")
    long countByStatus(MaintenanceRequest.TaskStatus status);

    @Query("SELECT COUNT(m) FROM MaintenanceRequest m WHERE m.assignedTechnician.id = :technicianId AND m.status = 'COMPLETED'")
    long countCompletedByTechnician(Long technicianId);

    @Query("SELECT m FROM MaintenanceRequest m WHERE m.university.id = :universityId ORDER BY m.reportedAt DESC")
    List<MaintenanceRequest> findByUniversityOrderByDate(Long universityId);

    @Query("SELECT m.assignedTechnician.fullName, COUNT(m) as cnt FROM MaintenanceRequest m WHERE m.status = 'COMPLETED' GROUP BY m.assignedTechnician ORDER BY cnt DESC")
    List<Object[]> getTechnicianPerformance();
}
