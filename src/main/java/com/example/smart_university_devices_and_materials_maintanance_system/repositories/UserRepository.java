package com.example.smart_university_devices_and_materials_maintanance_system.repositories;

import com.example.smart_university_devices_and_materials_maintanance_system.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"university", "college"})
    @org.springframework.lang.NonNull
    List<User> findAll();

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByRole(User.Role role);

    List<User> findByUniversityId(Long universityId);

    List<User> findByAccountStatus(User.AccountStatus status);

    @Query("SELECT u FROM User u WHERE u.university.id = :universityId AND u.role = :role")
    List<User> findByUniversityIdAndRole(Long universityId, User.Role role);

    // Pagination and filtering methods
    @Query("SELECT u FROM User u WHERE u.fullName LIKE %:search% OR u.email LIKE %:search% OR u.username LIKE %:search%")
    Page<User> searchUsers(String search, Pageable pageable);

    @Query("SELECT u FROM User u WHERE (u.fullName LIKE %:search% OR u.email LIKE %:search% OR u.username LIKE %:search%) AND u.role = :role")
    Page<User> searchUsersWithRole(String search, User.Role role, Pageable pageable);

    @Query("SELECT u FROM User u WHERE (u.fullName LIKE %:search% OR u.email LIKE %:search% OR u.username LIKE %:search%) AND u.accountStatus = :status")
    Page<User> searchUsersWithStatus(String search, User.AccountStatus status, Pageable pageable);

    @Query("SELECT u FROM User u WHERE (u.fullName LIKE %:search% OR u.email LIKE %:search% OR u.username LIKE %:search%) AND u.role = :role AND u.accountStatus = :status")
    Page<User> searchUsersWithRoleAndStatus(String search, User.Role role, User.AccountStatus status, Pageable pageable);

    Page<User> findByRole(User.Role role, Pageable pageable);

    Page<User> findByAccountStatus(User.AccountStatus status, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.accountStatus = :status")
    Page<User> findByRoleAndAccountStatus(User.Role role, User.AccountStatus status, Pageable pageable);

}

