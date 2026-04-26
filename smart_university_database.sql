SET FOREIGN_KEY_CHECKS = 0;

-- SMART UNIVERSITY DEVICE & MATERIALS MAINTENANCE SYSTEM (SMARTUNI)
-- CONSOLIDATED DATABASE SCRIPT (SCHEMA + DATA)
-- Designed for National Scale Excellence


SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";

SET AUTOCOMMIT = 0;

START TRANSACTION;

SET time_zone = "+00:00";

SET FOREIGN_KEY_CHECKS = 0;






DROP TABLE IF EXISTS `universities`;

CREATE TABLE `universities` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `abbreviation` varchar(50) NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


INSERT INTO `universities` (`id`, `name`, `abbreviation`, `location`, `type`) VALUES
(1, 'University of Rwanda', 'UR', 'Kigali, Rwanda', 'PUBLIC_UNIVERSITY'),
(2, 'Integrated Polytechnic Regional Center Karongi', 'RP Karongi', 'Karongi, Rwanda', 'POLYTECHNIC'),
(3, 'Integrated Polytechnic Regional Center Kigali', 'RP Kigali', 'Kigali, Rwanda', 'POLYTECHNIC'),
(4, 'African Leadership University', 'ALU', 'Kigali, Rwanda', 'PRIVATE_UNIVERSITY'),
(5, 'Carnegie Mellon University Rwanda', 'CMUR', 'Kigali, Rwanda', 'PRIVATE_UNIVERSITY'),
(6, 'University of Kigali', 'UoK', 'Kigali, Rwanda', 'PRIVATE_UNIVERSITY'),
(7, 'Integrated Polytechnic Regional Center Tumba', 'RP Tumba', 'Rulindo, Rwanda', 'POLYTECHNIC'),
(8, 'Mount Kenya University Kigali', 'MKU Kigali', 'Kigali, Rwanda', 'PRIVATE_UNIVERSITY'),
(9, 'Musanze Polytechnic', 'Musanze', 'Musanze, Rwanda', 'POLYTECHNIC'),
(10, 'Byumba School of Nursing', 'Byumba', 'Byumba, Rwanda', 'NURSING_SCHOOL');



DROP TABLE IF EXISTS `colleges`;

CREATE TABLE `colleges` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `abbreviation` varchar(50) NOT NULL,
  `university_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`university_id`) REFERENCES `universities` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


INSERT INTO `colleges` (`id`, `name`, `abbreviation`, `university_id`) VALUES
(1, 'College of Science and Technology', 'CST', 1),
(2, 'College of Business and Economics', 'CBE', 1),
(3, 'College of Education', 'CE', 1),
(4, 'College of Medicine and Health Sciences', 'CMHS', 1),
(5, 'College of Arts and Social Sciences', 'CASS', 1),
(6, 'Computing & Information Technology', 'CICT', 2),
(7, 'Mechanical Engineering', 'ME', 2),
(8, 'Electrical and Electronics Engineering', 'EEE', 2),
(9, 'Hospitality and Tourism Management', 'HTM', 2),
(10, 'Civil Engineering', 'CE', 2);



DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL UNIQUE,
  `email` varchar(150) NOT NULL UNIQUE,
  `password` varchar(255) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `role` varchar(50) NOT NULL,
  `account_status` varchar(50) DEFAULT 'ACTIVE',
  `mfa_enabled` tinyint(1) DEFAULT 0,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `last_login` datetime DEFAULT NULL,
  `university_id` bigint(20) DEFAULT NULL,
  `college_id` bigint(20) DEFAULT NULL,
  `phone_number` varchar(50) DEFAULT NULL,
  `profile_photo` varchar(255) DEFAULT NULL,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`university_id`) REFERENCES `universities` (`id`),
  FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- Admin Password: Admin@2024
-- Other User Passwords: Password@2024 (BCrypt strength 12 to match SecurityConfig)
INSERT INTO `users` (`id`, `username`, `email`, `password`, `full_name`, `role`, `account_status`, `university_id`, `college_id`) VALUES
(1, 'admin', 'admin@smartuni.rw', '$2a$12$GRLdNijSQMUvl/au98L6S.776V/Y7H/t.K.Mh65.G.r1K/n1X/m1u', 'System Administrator', 'ADMIN', 'ACTIVE', 1, 1),
(2, 'alicetechnic', 'alicetechnic@smartuni.rw', '$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Alice Technic', 'TECHNICIAN', 'ACTIVE', 1, 1),
(3, 'bobtech', 'bobtech@smartuni.rw', '$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Bob Tech', 'TECHNICIAN', 'ACTIVE', 1, 1),
(4, 'charliestaff', 'charliestaff@smartuni.rw', '$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Charlie Staff', 'STAFF', 'ACTIVE', 1, 1),
(5, 'dianastaff', 'dianastaff@smartuni.rw', '$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Diana Staff', 'STAFF', 'ACTIVE', 1, 1),
(6, 'edwardcleaner', 'edwardcleaner@smartuni.rw', '$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Edward Cleaner', 'CLEANER_STUDENT', 'ACTIVE', 1, 1),
(7, 'fionastudent', 'fionastudent@smartuni.rw', '$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Fionna Student', 'CLEANER_STUDENT', 'ACTIVE', 1, 1),
(8, 'georgetech', 'georgetech@smartuni.rw', '$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'George Tech', 'TECHNICIAN', 'ACTIVE', 1, 1),
(9, 'hannahstaff', 'hannahstaff@smartuni.rw', '$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Hannah Staff', 'STAFF', 'ACTIVE', 1, 1),
(10, 'iancleaner', 'iancleaner@smartuni.rw', '$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Ian Cleaner', 'CLEANER_STUDENT', 'ACTIVE', 1, 1);



DROP TABLE IF EXISTS `devices`;

CREATE TABLE `devices` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `device_id` varchar(100) NOT NULL UNIQUE,
  `device_name` varchar(255) NOT NULL,
  `room_location` varchar(255) NOT NULL,
  `description` text,
  `status` varchar(50) DEFAULT 'ACTIVE',
  `admin_approved` tinyint(1) DEFAULT 1,
  `university_id` bigint(20) DEFAULT NULL,
  `college_id` bigint(20) DEFAULT NULL,
  `reported_by_id` bigint(20) DEFAULT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`university_id`) REFERENCES `universities` (`id`),
  FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`),
  FOREIGN KEY (`reported_by_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


INSERT INTO `devices` (`id`, `device_id`, `device_name`, `room_location`, `description`, `university_id`, `college_id`, `reported_by_id`) VALUES
(1, 'RPKAR/HOSP/F&B/SO01', 'Socket 01', 'Hospitality Block, F&B Room', 'National Maintenance Grid Asset', 2, 9, 4),
(2, 'RPKAR/HOSP/PC01', 'Personal Computer 01', 'Hospitality Block', 'High-level workstation', 2, 9, 4),
(3, 'RPKAR/MEE/PC02', 'Personal Computer 02', 'Mechanical Block', 'Campus asset tracking', 2, 7, 4),
(4, 'RPKAR/ADM/BU01', 'Bulb 01', 'Administration Block', 'Energy efficient IoT bulb', 2, 1, 4),
(5, 'RPKAR/CO LA01/SW05', 'Switch 05', 'Computer Lab 01', 'Core network switch', 2, 6, 4),
(6, 'RPKAR/CA CE/PR01', 'Printer 01', 'Career Center', 'High-speed network printer', 2, 1, 4),
(7, 'RPKAR/GA/CAM01', 'Camera 01', 'Gate', 'IP Surveillance System', 2, 1, 4),
(8, 'RPKAR/EEE LAB/MU01', 'Multimeter 01', 'Electrical Engineering Lab', 'Precision measuring tool', 2, 8, 4),
(9, 'RPKAR/ICT/PC99', 'Workstation Pro', 'ICT Hub', 'High-performance PC', 2, 6, 4),
(10, 'RPKAR/LAB/SR07', 'Server Rail', 'Main Server Room', 'Enterprise rack component', 2, 6, 4);



DROP TABLE IF EXISTS `materials`;

CREATE TABLE `materials` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `material_name` varchar(255) NOT NULL,
  `room_location` varchar(255) NOT NULL,
  `description` text,
  `status` varchar(50) DEFAULT 'VERIFIED',
  `admin_approved` tinyint(1) DEFAULT 1,
  `university_id` bigint(20) DEFAULT NULL,
  `college_id` bigint(20) DEFAULT NULL,
  `reported_by_id` bigint(20) DEFAULT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`university_id`) REFERENCES `universities` (`id`),
  FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`),
  FOREIGN KEY (`reported_by_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


INSERT INTO `materials` (`id`, `material_name`, `room_location`, `description`, `university_id`, `college_id`, `reported_by_id`) VALUES
(1, 'Table 01', 'ICT LAB 01', 'Ref: RPKAR/ICT LAB01/TB01', 2, 6, 6),
(2, 'Tank 01', 'Hospitality Block', 'Ref: RPKAR/HOSP/TANK01', 2, 9, 6),
(3, 'Mirror 01', 'Hospitality Block Toilet', 'Ref: RPKAR/HOSP/TOI/MI01', 2, 9, 6),
(4, 'Book Stand 010', 'Library 01', 'Ref: RPKAR/LIB 01/BK ST010', 2, 1, 6),
(5, 'Chair 01', 'Student Affairs', 'Ref: RPKAR/ST AF/CH01', 2, 1, 6),
(6, 'Door 01', 'Finance Office', 'Ref: RPKAR/FIN/DO01', 2, 1, 6),
(7, 'Main Hall Chair 04', 'Main Hall', 'Ref: RPKAR/MH/CH04', 2, 1, 6),
(8, 'Storage Rack Z', 'General Lab', 'Ref: RPKAR/LAB/ST-Z', 2, 1, 6),
(9, 'Executive Desk', 'Admin Office', 'Ref: RPKAR/ADM/DK-01', 2, 1, 6),
(10, 'Dining Table 09', 'Campus Cafeteria', 'Ref: RPKAR/CAFE/TB-09', 2, 1, 6);



DROP TABLE IF EXISTS `maintenance_requests`;

CREATE TABLE `maintenance_requests` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `request_type` varchar(50) NOT NULL,
  `issue_description` text NOT NULL,
  `priority` varchar(50) DEFAULT 'MEDIUM',
  `status` varchar(50) DEFAULT 'PENDING',
  `technician_notes` text,
  `due_date` datetime DEFAULT NULL,
  `reported_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `started_at` datetime DEFAULT NULL,
  `completed_at` datetime DEFAULT NULL,
  `verified_at` datetime DEFAULT NULL,
  `device_id` bigint(20) DEFAULT NULL,
  `material_id` bigint(20) DEFAULT NULL,
  `reported_by_id` bigint(20) DEFAULT NULL,
  `assigned_technician_id` bigint(20) DEFAULT NULL,
  `university_id` bigint(20) DEFAULT NULL,
  `college_id` bigint(20) DEFAULT NULL,
  `photo_path` varchar(255) DEFAULT NULL,
  `verified_by` bigint(20) DEFAULT NULL,
  `assigned_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`device_id`) REFERENCES `devices` (`id`),
  FOREIGN KEY (`material_id`) REFERENCES `materials` (`id`),
  FOREIGN KEY (`reported_by_id`) REFERENCES `users` (`id`),
  FOREIGN KEY (`assigned_technician_id`) REFERENCES `users` (`id`),
  FOREIGN KEY (`university_id`) REFERENCES `universities` (`id`),
  FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


INSERT INTO `maintenance_requests` (`id`, `request_type`, `issue_description`, `priority`, `status`, `device_id`, `reported_by_id`, `assigned_technician_id`, `university_id`, `college_id`) VALUES
(1, 'DEVICE', 'Screen flickering randomly', 'HIGH', 'ASSIGNED', 1, 4, 2, 1, 1),
(2, 'DEVICE', 'Paper jam in main tray', 'MEDIUM', 'PENDING', 6, 4, NULL, 1, 1),
(3, 'DEVICE', 'Overheating after 1 hour', 'CRITICAL', 'IN_PROGRESS', 2, 4, 3, 1, 1),
(4, 'DEVICE', 'Network port not responding', 'LOW', 'COMPLETED', 5, 4, 2, 1, 1),
(5, 'DEVICE', 'OS update required', 'LOW', 'PENDING', 9, 4, NULL, 1, 1);



DROP TABLE IF EXISTS `notifications`;

CREATE TABLE `notifications` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `recipient_id` bigint(20) NOT NULL,
  `title` varchar(255) NOT NULL,
  `message` varchar(1000) NOT NULL,
  `type` varchar(50) DEFAULT NULL,
  `is_read` tinyint(1) DEFAULT 0,
  `maintenance_request_id` bigint(20) DEFAULT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`recipient_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


INSERT INTO `notifications` (`id`, `recipient_id`, `title`, `message`, `type`) VALUES
(1, 1, 'System Update', 'National Maintenance Grid updated to v2.4.0', 'SYSTEM'),
(2, 1, 'New Device Registered', 'A new workstation added to RP Karongi.', 'SYSTEM'),
(3, 1, 'Maintenance Overdue', '3 tasks in UR are currently overdue.', 'SYSTEM');



DROP TABLE IF EXISTS `audit_logs`;

CREATE TABLE `audit_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `action` varchar(255) NOT NULL,
  `details` varchar(2000) DEFAULT NULL,
  `ip_address` varchar(100) DEFAULT NULL,
  `performed_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


INSERT INTO `audit_logs` (`id`, `user_id`, `action`, `details`) VALUES
(1, 1, 'LOGIN', 'Admin authenticated from Karongi Hub'),
(2, 1, 'APPROVE_USER', 'Activated technician account: Alice');


SET FOREIGN_KEY_CHECKS = 1;

COMMIT;


CREATE TABLE `otp_tokens` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `otp_code` varchar(10) NOT NULL,
  `expires_at` datetime DEFAULT NULL,
  `used` tinyint(1) DEFAULT 0,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
COMMIT;
