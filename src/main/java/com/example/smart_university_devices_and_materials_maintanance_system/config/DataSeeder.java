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
    private final DeviceRepository deviceRepository;
    private final MaterialRepository materialRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (universityRepository.count() == 0) {
            seedUniversities();
            log.info("✅ Universities seeded successfully");
        }
        if (userRepository.count() < 10) {
            seedMoreUsers();
            log.info("✅ 10+ Users seeded successfully");
        }
        if (deviceRepository.count() == 0) {
            seedDevices();
            log.info("✅ 10 Devices seeded successfully");
        }
        if (materialRepository.count() == 0) {
            seedMaterials();
            log.info("✅ 10 Materials seeded successfully");
        }
        if (maintenanceRequestRepository.count() == 0) {
            seedMaintenanceRequests();
            log.info("✅ 10 Maintenance Requests seeded successfully");
        }
        if (notificationRepository.count() == 0) {
            seedNotifications();
            log.info("✅ 10 Notifications seeded successfully");
        }
        if (auditLogRepository.count() == 0) {
            seedAuditLogs();
            log.info("✅ 10 Audit Logs seeded successfully");
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

        // Add 4 more colleges to reach 10
        List<String[]> moreColleges = List.of(
                new String[] { "College of Information and Communications Technology", "CICT" },
                new String[] { "School of Mining and Geology", "SMG" },
                new String[] { "School of Architecture and Built Environment", "SABE" },
                new String[] { "School of Engineering", "SoE" });
        for (String[] mc : moreColleges) {
            collegeRepository.save(College.builder().name(mc[0]).abbreviation(mc[1]).university(ur).build());
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

    private void seedMoreUsers() {
        University ur = universityRepository.findByName("University of Rwanda").orElse(null);
        if (ur == null) return;
        College firstCollege = collegeRepository.findByUniversityId(ur.getId()).get(0);

        // Ensure Admin exists
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(User.builder()
                    .username("admin").email("admin@smartuni.rw")
                    .password(passwordEncoder.encode("Admin@2024"))
                    .fullName("System Administrator").role(User.Role.ADMIN)
                    .mfaEnabled(false).accountStatus(User.AccountStatus.ACTIVE)
                    .university(ur).build());
        }

        // Add variety of roles to reach 10+
        String[] names = {"Alice Technic", "Bob Tech", "Charlie Staff", "Diana Staff", "Edward Cleaner", 
                         "Fiona Student", "George Tech", "Hannah Staff", "Ian Cleaner", "Julia Student"};
        User.Role[] roles = {User.Role.TECHNICIAN, User.Role.TECHNICIAN, User.Role.STAFF, User.Role.STAFF, 
                           User.Role.CLEANER_STUDENT, User.Role.CLEANER_STUDENT, User.Role.TECHNICIAN, 
                           User.Role.STAFF, User.Role.CLEANER_STUDENT, User.Role.CLEANER_STUDENT};

        for (int i = 0; i < names.length; i++) {
            String username = names[i].toLowerCase().replace(" ", "");
            if (!userRepository.existsByUsername(username)) {
                userRepository.save(User.builder()
                        .username(username)
                        .email(username + "@smartuni.rw")
                        .password(passwordEncoder.encode("Password@2024"))
                        .fullName(names[i])
                        .role(roles[i])
                        .mfaEnabled(false)
                        .accountStatus(User.AccountStatus.ACTIVE)
                        .university(ur)
                        .college(firstCollege)
                        .build());
            }
        }
    }

    private void seedDevices() {
        University ur = universityRepository.findByName("Integrated Polytechnic Regional Center Karongi").orElse(null);
        if (ur == null) ur = universityRepository.findByName("University of Rwanda").orElse(null);
        if (ur == null) return;
        
        College college = collegeRepository.findByUniversityId(ur.getId()).stream().findFirst().orElse(null);
        User staff = userRepository.findByRole(User.Role.STAFF).get(0);

        String[][] deviceData = {
            {"RPKAR/HOSP/F&B/SO01", "Socket 01", "Hospitality Block, F&B Room"},
            {"RPKAR/HOSP/PC01", "Personal Computer 01", "Hospitality Block"},
            {"RPKAR/MEE/PC02", "Personal Computer 02", "Mechanical Block"},
            {"RPKAR/ADM/BU01", "Bulb 01", "Administration Block"},
            {"RPKAR/CO LA01/SW05", "Switch 05", "Computer Lab 01"},
            {"RPKAR/CA CE/PR01", "Printer 01", "Career Center"},
            {"RPKAR/GA/CAM01", "Camera 01", "Gate"},
            {"RPKAR/EEE LAB/MU01", "Multimeter 01", "Electrical Engineering Lab"},
            {"RPKAR/ICT/PC99", "Workstation Pro", "ICT Hub"},
            {"RPKAR/LAB/SR07", "Server Rail", "Main Server Room"}
        };
        
        for (String[] data : deviceData) {
            deviceRepository.save(Device.builder()
                    .deviceId(data[0])
                    .deviceName(data[1])
                    .roomLocation(data[2])
                    .status(Device.DeviceStatus.ACTIVE)
                    .university(ur)
                    .college(college)
                    .reportedBy(staff)
                    .adminApproved(true)
                    .description("High-level asset tracked in National Maintenance Grid")
                    .build());
        }
    }

    private void seedMaterials() {
        University ur = universityRepository.findByName("Integrated Polytechnic Regional Center Karongi").orElse(null);
        if (ur == null) ur = universityRepository.findByName("University of Rwanda").orElse(null);
        if (ur == null) return;

        College college = collegeRepository.findByUniversityId(ur.getId()).stream().findFirst().orElse(null);
        User cleaner = userRepository.findByRole(User.Role.CLEANER_STUDENT).get(0);

        String[][] materialData = {
            {"RPKAR/ICT LAB01/TB01", "Table 01", "ICT LAB 01"},
            {"RPKAR/HOSP/TANK01", "Tank 01", "Hospitality Block"},
            {"RPKAR/HOSP/TOI/MI01", "Mirror 01", "Hospitality Block Toilet"},
            {"RPKAR/LIB 01/BK ST010", "Book Stand 010", "Library 01"},
            {"RPKAR/ST AF/CH01", "Chair 01", "Student Affairs"},
            {"RPKAR/FIN/DO01", "Door 01", "Finance Office"},
            {"RPKAR/MH/CH04", "Main Hall Chair 04", "Main Hall"},
            {"RPKAR/LAB/ST-Z", "Storage Rack Z", "General Lab"},
            {"RPKAR/ADM/DK-01", "Executive Desk", "Admin Office"},
            {"RPKAR/CAFE/TB-09", "Dining Table 09", "Campus Cafeteria"}
        };

        for (String[] data : materialData) {
            materialRepository.save(Material.builder()
                    .materialName(data[1])
                    .roomLocation(data[2])
                    .status(Material.MaterialStatus.VERIFIED)
                    .university(ur)
                    .college(college)
                    .reportedBy(cleaner)
                    .adminApproved(true)
                    .description("Official material inventory - " + data[0])
                    .build());
        }
    }

    private void seedMaintenanceRequests() {
        List<Device> devices = deviceRepository.findAll();
        User reporter = userRepository.findByRole(User.Role.STAFF).get(0);
        User tech = userRepository.findByRole(User.Role.TECHNICIAN).get(0);
        
        String[] issues = {"Screen flickering randomly", "Paper jam in main tray", "Overheating after 1 hour", 
                          "Network port not responding", "OS update required", "Broken power cable", 
                          "Lens needs cleaning", "Remote control missing", "Noisy cooling fan", "Calibration required"};

        for (int i = 0; i < 10; i++) {
            MaintenanceRequest request = MaintenanceRequest.builder()
                    .requestType(MaintenanceRequest.RequestType.DEVICE)
                    .device(devices.get(i))
                    .issueDescription(issues[i])
                    .priority(MaintenanceRequest.Priority.values()[i % 4])
                    .status(MaintenanceRequest.TaskStatus.values()[i % 5])
                    .reportedBy(reporter)
                    .assignedTechnician(i % 2 == 0 ? tech : null)
                    .university(devices.get(i).getUniversity())
                    .college(devices.get(i).getCollege())
                    .reportedAt(java.time.LocalDateTime.now().minusDays(i))
                    .build();
            maintenanceRequestRepository.save(request);
        }
    }
    private void seedNotifications() {
        User admin = userRepository.findByUsername("admin").get();
        String[] titles = {"System Update", "New Device Registered", "Maintenance Overdue", "User Registration", 
                           "Inventory Low", "Audit Completed", "Security Alert", "Feedback Received", 
                           "Network Maintenance", "Training Scheduled"};
        
        for (int i = 0; i < 10; i++) {
            notificationRepository.save(Notification.builder()
                    .title(titles[i])
                    .message("This is a professional system notification for " + titles[i])
                    .read(i % 2 == 0)
                    .recipient(admin)
                    .type(Notification.NotificationType.SYSTEM)
                    .createdAt(java.time.LocalDateTime.now().minusHours(i))
                    .build());
        }
    }

    private void seedAuditLogs() {
        User admin = userRepository.findByUsername("admin").get();
        String[] actions = {"LOGIN", "CREATE_DEVICE", "APPROVE_USER", "DELETE_MATERIAL", 
                            "UPDATE_TASK", "EXPORT_REPORT", "MFA_ENABLED", "PASSWORD_RESET", 
                            "CLEARED_NOTIFICATIONS", "LOGOUT"};

        for (int i = 0; i < 10; i++) {
            auditLogRepository.save(AuditLog.builder()
                    .user(admin)
                    .action(actions[i])
                    .details("Admin performed action: " + actions[i])
                    .performedAt(java.time.LocalDateTime.now().minusDays(i))
                    .build());
        }
    }
}
