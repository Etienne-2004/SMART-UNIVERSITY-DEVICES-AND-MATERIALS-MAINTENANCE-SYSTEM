package com.example.smart_university_devices_and_materials_maintanance_system.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "materials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String materialName; // "Table 01"

    @Column(nullable = false)
    private String roomLocation; // "Hospitality Block"

    private String photoPath;

    @Enumerated(EnumType.STRING)
    private MaterialStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id")
    private University university;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id")
    private College college;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by")
    private User reportedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_technician")
    private User assignedTechnician;

    @Builder.Default
    private boolean adminApproved = true;

    @Column(length = 2000)
    private String description; // Material issues

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null)
            status = MaterialStatus.REPORTED;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum MaterialStatus {
        REPORTED, PENDING, IN_PROGRESS, COMPLETED, VERIFIED
    }
}
