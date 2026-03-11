package com.example.smart_university_devices_and_materials_maintanance_system.repositories;

import com.example.smart_university_devices_and_materials_maintanance_system.entities.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UniversityRepository extends JpaRepository<University, Long> {

    Optional<University> findByName(String name);

    boolean existsByName(String name);

    List<University> findByType(University.UniversityType type);

    List<University> findByNameContainingIgnoreCase(String keyword);
}
