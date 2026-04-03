@echo off
title Smart University MMS - Professional Startup
echo ============================================================
echo   SMART UNIVERSITY DEVICE & MATERIALS MAINTENANCE SYSTEM
echo ============================================================
echo.
echo [1/2] Checking Database Connectivity...
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
echo [2/2] Launching Spring Boot Application...
echo.

:: Run the application
call mvnw.cmd spring-boot:run

pause
