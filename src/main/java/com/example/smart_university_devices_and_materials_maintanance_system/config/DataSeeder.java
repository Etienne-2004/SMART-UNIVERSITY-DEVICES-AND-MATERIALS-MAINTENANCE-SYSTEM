package com.example.smart_university_devices_and_materials_maintanance_system.config;

import com.example.smart_university_devices_and_materials_maintanance_system.entities.*;
import com.example.smart_university_devices_and_materials_maintanance_system.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UniversityRepository universityRepository;
    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (universityRepository.count() == 0) {
            seedUniversities();
            log.info("✅ Universities seeded successfully");
        }
        if (userRepository.count() == 0) {
            seedDefaultAdminUser();
            log.info("✅ Default admin user created");
        }
    }

    private void seedUniversities() {
        // ─── Public Universities (UR colleges) ──────────────────────────────────
        University ur = University.builder()
                .name("University of Rwanda")
                .abbreviation("UR")
                .type(University.UniversityType.PUBLIC_UNIVERSITY)
                .location("Kigali, Rwanda")
                .build();
        ur = universityRepository.save(ur);

        List<String[]> urColleges = List.of(
                new String[] { "College of Agriculture, Animal Sciences, and Veterinary Medicine", "CAVM" },
                new String[] { "College of Arts and Social Sciences", "CASS" },
                new String[] { "College of Business and Economics", "CBE" },
                new String[] { "College of Education", "CE" },
                new String[] { "College of Medicine and Health Sciences", "CMHS" },
                new String[] { "College of Science and Technology", "CST" });
        for (String[] c : urColleges) {
            collegeRepository.save(College.builder().name(c[0]).abbreviation(c[1]).university(ur).build());
        }

        // ─── Public Integrated Polytechnics (RP) ────────────────────────────────
        Map<String, String> polytechnics = Map.ofEntries(
                Map.entry("Green Hills Institute of Technology", "GIP"),
                Map.entry("Integrated Polytechnic Regional Center ILPD", "ILPD"),
                Map.entry("Integrated Polytechnic Regional Center Ngoma", "RP Ngoma"),
                Map.entry("Integrated Polytechnic Regional Center Kigali", "RP Kigali"),
                Map.entry("Integrated Polytechnic Regional Center Huye", "RP Huye"),
                Map.entry("Integrated Polytechnic Regional Center Karongi", "RP Karongi"),
                Map.entry("Integrated Polytechnic Regional Center Tumba", "RP Tumba"),
                Map.entry("Integrated Polytechnic Regional Center Rusizi", "RP Rusizi"),
                Map.entry("Integrated Polytechnic Regional Center Kitabi", "RP Kitabi"),
                Map.entry("Musanze Polytechnic", "Musanze Polytechnic"),
                Map.entry("Rwanda TVET Council", "RTC"),
                Map.entry("College of Information and Communications Technology", "CIP"));
        for (Map.Entry<String, String> e : polytechnics.entrySet()) {
            universityRepository.save(University.builder()
                    .name(e.getKey()).abbreviation(e.getValue())
                    .type(University.UniversityType.POLYTECHNIC)
                    .location("Rwanda").build());
        }

        // ─── Public Nursing Schools ──────────────────────────────────────────────
        List<String[]> nursingSchools = List.of(
                new String[] { "Byumba School of Nursing", "Byumba" },
                new String[] { "Kibungo School of Nursing", "Kibungo" },
                new String[] { "Nyagatare School of Nursing", "Nyagatare" },
                new String[] { "Rwamagana School of Nursing", "Rwamagana" });
        for (String[] ns : nursingSchools) {
            universityRepository.save(University.builder()
                    .name(ns[0]).abbreviation(ns[1])
                    .type(University.UniversityType.NURSING_SCHOOL)
                    .location("Rwanda").build());
        }

        // ─── Private Universities ────────────────────────────────────────────────
        List<String[]> privateUniversities = List.of(
                new String[] { "Rwanda Institute of Conservation Agriculture", "RICA" },
                new String[] { "East Africa College of Cultures", "EACC" },
                new String[] { "Kigali Independent University", "KIC" },
                new String[] { "University of Kigali", "UoK" },
                new String[] { "Rwanda Health Improvement Hub", "RHIH" },
                new String[] { "Université Libre de Kigali", "UNILAK" },
                new String[] { "Adventist University of Central Africa", "AUCA" },
                new String[] { "African Leadership University", "ALU" },
                new String[] { "African Institute of Mathematical Sciences Kigali", "AIWK" },
                new String[] { "East African University Rwanda", "EAU" },
                new String[] { "Carnegie Mellon University Rwanda", "CMUR" },
                new String[] { "Institut Catholique de Kabgayi", "ICK" },
                new String[] { "Catholic University of Rwanda", "CUR" },
                new String[] { "Higher Advanced Institute Professionnel", "HAIP" },
                new String[] { "University of Kigali (UNIK)", "UNIK" },
                new String[] { "Management and Infrastructure Professional College", "MIPC" },
                new String[] { "Institut d'Enseignement Supérieur de Ruhengeri", "INES" },
                new String[] { "Institut Supérieur Pédagogique de Gitwe", "ISPG" },
                new String[] { "Kigali Polytechnic", "KP" },
                new String[] { "Pentecostal Economics Community Development", "PECDTC" },
                new String[] { "Université Laïque de Kigali", "ULK" },
                new String[] { "Kigali International Management Academy", "KIM" },
                new String[] { "Mount Kenya University Kigali", "MKU Kigali" },
                new String[] { "Protestant Institute of Arts and Social Sciences", "PIASS" },
                new String[] { "University of Tourism Technology and Business Studies", "UTB" },
                new String[] { "University of Tourism and Administration Business", "UTAB" },
                new String[] { "University of Global Health Equity", "UGHE" });
        for (String[] pu : privateUniversities) {
            try {
                if (!universityRepository.existsByName(pu[0])) {
                    universityRepository.save(University.builder()
                            .name(pu[0]).abbreviation(pu[1])
                            .type(University.UniversityType.PRIVATE_UNIVERSITY)
                            .location("Rwanda").build());
                }
            } catch (Exception ex) {
                log.warn("Could not seed university {}: {}", pu[0], ex.getMessage());
            }
        }
    }

    private void seedDefaultAdminUser() {
        University ur = universityRepository.findByName("University of Rwanda").orElse(null);
        User admin = User.builder()
                .username("admin")
                .email("admin@smartuni.rw")
                .password(passwordEncoder.encode("Admin@2024"))
                .fullName("System Administrator")
                .role(User.Role.ADMIN)
                .mfaEnabled(false) // Disable MFA for default admin for easy first login
                .accountStatus(User.AccountStatus.ACTIVE)
                .university(ur)
                .build();
        userRepository.save(admin);

        // Demo users
        if (ur != null) {
            College firstCollege = collegeRepository.findByUniversityId(ur.getId())
                    .stream().findFirst().orElse(null);

            User technician = User.builder()
                    .username("technician1")
                    .email("technician1@smartuni.rw")
                    .password(passwordEncoder.encode("Tech@2024"))
                    .fullName("Jean Paul Technician")
                    .role(User.Role.TECHNICIAN)
                    .mfaEnabled(false)
                    .accountStatus(User.AccountStatus.ACTIVE)
                    .university(ur)
                    .college(firstCollege)
                    .build();
            userRepository.save(technician);

            User staff = User.builder()
                    .username("staff1")
                    .email("staff1@smartuni.rw")
                    .password(passwordEncoder.encode("Staff@2024"))
                    .fullName("Marie Staff Lab Supervisor")
                    .role(User.Role.STAFF)
                    .mfaEnabled(false)
                    .accountStatus(User.AccountStatus.ACTIVE)
                    .university(ur)
                    .college(firstCollege)
                    .build();
            userRepository.save(staff);

            User cleaner = User.builder()
                    .username("cleaner1")
                    .email("cleaner1@smartuni.rw")
                    .password(passwordEncoder.encode("Clean@2024"))
                    .fullName("Peter Cleaner")
                    .role(User.Role.CLEANER_STUDENT)
                    .mfaEnabled(false)
                    .accountStatus(User.AccountStatus.ACTIVE)
                    .university(ur)
                    .college(firstCollege)
                    .build();
            userRepository.save(cleaner);
        }
        log.info("✅ Demo users created: admin@smartuni.rw / Admin@2024");
    }
}
