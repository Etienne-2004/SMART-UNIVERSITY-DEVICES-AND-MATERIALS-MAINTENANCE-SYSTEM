@echo off
title Smart University MMS - Database Check
echo ============================================================
echo   CHECKING SMART UNIVERSITY DATABASE
echo ============================================================
echo.

echo Checking if database exists and has data...
echo.

mysql -u root -p -e "USE smart_university_maintenance_db; SELECT 'Universities:', COUNT(*) FROM universities; SELECT 'Colleges:', COUNT(*) FROM colleges; SELECT 'Users:', COUNT(*) FROM users; SELECT 'Devices:', COUNT(*) FROM devices; SELECT 'Materials:', COUNT(*) FROM materials; SELECT 'Maintenance Requests:', COUNT(*) FROM maintenance_requests; SELECT 'Notifications:', COUNT(*) FROM notifications;" 2>nul

if %errorlevel% equ 0 (
    echo.
    echo [SUCCESS] Database is properly imported!
    echo.
    echo You can now login to the system at: http://localhost:8081
    echo.
    echo Login Credentials:
    echo Admin: admin@smartuni.rw / Admin@2024
    echo Staff: charliestaff@smartuni.rw / Password@2024
    echo Technician: alicetechnic@smartuni.rw / Password@2024
    echo Cleaner: edwardcleaner@smartuni.rw / Password@2024
) else (
    echo.
    echo [ERROR] Database not found or MySQL not accessible.
    echo Please run IMPORT_DATABASE.bat first to import the database.
)

echo.
pause
