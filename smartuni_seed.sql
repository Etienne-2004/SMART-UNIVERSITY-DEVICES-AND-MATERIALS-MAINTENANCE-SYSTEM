-- =============================================================================
--  smartuni_seed.sql  —  Updated for newest schema
-- =============================================================================

USE smart_university_maintenance_db;
SET FOREIGN_KEY_CHECKS = 0;

-- HELPERS
SET @UR      = (SELECT id FROM universities WHERE abbreviation = 'UR'        LIMIT 1);
SET @RPKAR   = (SELECT id FROM universities WHERE abbreviation = 'RP Karongi' LIMIT 1);
SET @RPKGL   = (SELECT id FROM universities WHERE abbreviation = 'RP Kigali'  LIMIT 1);
SET @AUCA    = (SELECT id FROM universities WHERE abbreviation = 'AUCA'       LIMIT 1);
SET @ALU     = (SELECT id FROM universities WHERE abbreviation = 'ALU'        LIMIT 1);

-- Refresh college ID variables
SET @UR_CST    = (SELECT id FROM colleges WHERE abbreviation='CST'    AND university_id=@UR      LIMIT 1);
SET @UR_ADM    = (SELECT id FROM colleges WHERE abbreviation='ADM'    AND university_id=@UR      LIMIT 1);
SET @RPKAR_ICT = (SELECT id FROM colleges WHERE abbreviation='ICT'    AND university_id=@RPKAR   LIMIT 1);
SET @RPKAR_HOSP= (SELECT id FROM colleges WHERE abbreviation='HOSP'   AND university_id=@RPKAR   LIMIT 1);
SET @RPKAR_ADM = (SELECT id FROM colleges WHERE abbreviation='ADM'    AND university_id=@RPKAR   LIMIT 1);
SET @RPKAR_MH  = (SELECT id FROM colleges WHERE abbreviation='MH'     AND university_id=@RPKAR   LIMIT 1);

-- USERS
SET @ADMIN_UR    = (SELECT id FROM users WHERE username='uwimana.jp'    LIMIT 1);
SET @TECH_RPKAR  = (SELECT id FROM users WHERE username='nzeyimana.c'   LIMIT 1);
SET @TECH_UR2    = (SELECT id FROM users WHERE username='byiringiro.s'  LIMIT 1);
SET @STAFF_RPKAR = (SELECT id FROM users WHERE username='kayitesi.mc'   LIMIT 1);
SET @STAFF_UR    = (SELECT id FROM users WHERE username='mugenzi.a'     LIMIT 1);
SET @CLN_RPKAR   = (SELECT id FROM users WHERE username='mutwewingabo.f' LIMIT 1);
SET @CLN_UR      = (SELECT id FROM users WHERE username='bizimana.jl'   LIMIT 1);

-- DEVICES
INSERT IGNORE INTO devices
    (device_id, device_name, room_location, status,
     university_id, college_id, reported_by, assigned_technician, admin_approved, description)
VALUES
('RPKAR/ICT/PC01',   'Personal Computer 01', 'ICT Lab 01', 'ACTIVE', @RPKAR, @RPKAR_ICT, @STAFF_RPKAR, @TECH_RPKAR, 1, 'Reliable desktop for ICT lab'),
('RPKAR/HOSP/PC02',  'Personal Computer 02', 'Hospitality Block', 'ACTIVE', @RPKAR, @RPKAR_HOSP, @STAFF_RPKAR, @TECH_RPKAR, 1, 'Reception PC'),
('RPKAR/ICT/PJ01',   'Projector 01', 'ICT Lab 01', 'UNDER_REPAIR', @RPKAR, @RPKAR_ICT, @STAFF_RPKAR, @TECH_RPKAR, 1, 'Lamp needs replacement'),
('UR/CST/SW01',      'Switch 01', 'CST Block A', 'ACTIVE', @UR, @UR_CST, @STAFF_UR, @TECH_UR2, 1, 'Main department switch'),
('UR/CST/LT01',      'Laptop 01', 'CST Staff Room', 'ACTIVE', @UR, @UR_CST, @STAFF_UR, @TECH_UR2, 1, 'Assigned to Dean');

-- MATERIALS
INSERT IGNORE INTO materials
    (material_name, room_location, status,
     university_id, college_id, reported_by, admin_approved, description)
VALUES
('Table 01',   'ICT Lab 01', 'REPORTED', @RPKAR, @RPKAR_ICT, @CLN_RPKAR, 1, 'New lab table'),
('Mirror 01',  'Hospitality Toilet', 'PENDING', @RPKAR, @RPKAR_HOSP, @CLN_RPKAR, 1, 'Mirror cracked'),
('Door 01',    'Finance Office', 'PENDING', @RPKAR, NULL, @CLN_RPKAR, 1, 'Door handle broken'),
('Chair 04',   'Main Hall', 'IN_PROGRESS', @RPKAR, @RPKAR_MH, @CLN_RPKAR, 1, 'Leg loose'),
('Window 01',  'CBE Block B', 'REPORTED', @UR, NULL, @CLN_UR, 1, 'Shattered window');

SET FOREIGN_KEY_CHECKS = 1;
