@echo off
title Smart University MMS - Database Import
echo ============================================================
echo   IMPORTING SMART UNIVERSITY DATABASE
echo ============================================================
echo.
echo This will import the comprehensive database with:
echo - 10 Universities (Rwanda institutions)
echo - 10 Colleges 
echo - 10 Users (Admin, Staff, Technicians, Cleaners)
echo - 10 Devices with professional asset IDs
echo - 10 Materials with reference numbers
echo - 5 Maintenance Requests
echo - 3 Notifications
echo - 2 Audit Logs
echo.
echo Database: smart_university_maintenance_db
echo Admin Login: admin@smartuni.rw / Admin@2024
echo Other Users: Password@2024
echo.
echo Press any key to start import...
pause > nul

echo.
echo Importing database...
echo.

:: Try to import using MySQL command
mysql -u root -p < smart_university_database.sql
if %errorlevel% equ 0 (
    echo [SUCCESS] Database imported successfully!
    echo.
    echo You can now login to the system at: http://localhost:8081
    echo Admin credentials: admin@smartuni.rw / Admin@2024
) else (
    echo [ERROR] MySQL command not found.
    echo.
    echo Please import manually using one of these methods:
    echo.
    echo Method 1: XAMPP phpMyAdmin
    echo 1. Open XAMPP Control Panel
    echo 2. Start Apache and MySQL
    echo 3. Open phpMyAdmin (click "Admin" next to MySQL)
    echo 4. Click "Import" tab
    echo 5. Select "smart_university_database.sql"
    echo 6. Click "Go" to import
    echo.
    echo Method 2: MySQL Workbench
    echo 1. Open MySQL Workbench
    echo 2. Connect to localhost with root user
    echo 3. File -> Run SQL Script
    echo 4. Select "smart_university_database.sql"
    echo 5. Execute the script
    echo.
)

echo.
pause
