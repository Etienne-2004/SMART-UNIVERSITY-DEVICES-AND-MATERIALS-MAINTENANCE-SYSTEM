package com.example.smart_university_devices_and_materials_maintanance_system.repositories;

import com.example.smart_university_devices_and_materials_maintanance_system.entities.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    List<Material> findByUniversityId(Long universityId);

    List<Material> findByUniversityIdAndCollegeId(Long universityId, Long collegeId);

    List<Material> findByStatus(Material.MaterialStatus status);

    List<Material> findByReportedById(Long userId);

    List<Material> findByAssignedTechnicianId(Long technicianId);

    List<Material> findByAdminApproved(boolean approved);

    @Query("SELECT COUNT(m) FROM Material m WHERE m.status = 'PENDING' OR m.status = 'REPORTED'")
    long countPendingMaterials();
}
