package com.example.smart_university_devices_and_materials_maintanance_system.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "universities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class University {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String abbreviation;

    @Enumerated(EnumType.STRING)
    private UniversityType type;

    private String location;

    @OneToMany(mappedBy = "university", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<College> colleges = new ArrayList<>();

    @OneToMany(mappedBy = "university", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<User> users = new ArrayList<>();

    public enum UniversityType {
        PUBLIC_UNIVERSITY,
        POLYTECHNIC,
        NURSING_SCHOOL,
        PRIVATE_UNIVERSITY
    }
}
