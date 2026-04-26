@echo off
title Smart University MMS - Professional Startup
echo ============================================================
echo   SMART UNIVERSITY DEVICE & MATERIALS MAINTENANCE SYSTEM
echo ============================================================
echo.
echo [1/3] Checking Database Connectivity...
echo.

:: Check if MySQL is running on port 3306
netstat -ano | findstr :3306 >nul
if %errorlevel% neq 0 (
    echo [ERROR] MySQL is NOT running! 
    echo Please start MySQL in XAMPP Control Panel first.
    echo.
    pause
    exit
)

echo [OK] Database is ready.
echo.
echo [2/3] Setting up Database (Single SQL File)...
echo.

:: Execute the single SQL setup file
mysql -u root -p < smart_university_database.sql
if %errorlevel% neq 0 (
    echo [ERROR] Database setup failed! Please check MySQL credentials.
    echo.
    pause
    exit
)

echo [OK] Database setup completed.
echo.
echo [3/3] Launching Spring Boot Application...
echo.

:: Run the application
call mvnw.cmd spring-boot:run

pause
