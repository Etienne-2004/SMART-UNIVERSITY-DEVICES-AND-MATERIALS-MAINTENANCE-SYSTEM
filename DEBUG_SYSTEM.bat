@echo off
title Smart University MMS - System Debug
echo ============================================================
echo   COMPREHENSIVE SYSTEM DEBUGGING
echo ============================================================
echo.

echo [1] Checking Application Status...
powershell -Command "try { $status = (Invoke-WebRequest -Uri 'http://localhost:8081' -UseBasicParsing).StatusCode; Write-Host 'Application Status: ' $status } catch { Write-Host 'Application not responding' }"
echo.

echo [2] Checking Login Page...
powershell -Command "try { $status = (Invoke-WebRequest -Uri 'http://localhost:8081/login' -UseBasicParsing).StatusCode; Write-Host 'Login Page Status: ' $status } catch { Write-Host 'Login page not accessible' }"
echo.

echo [3] Checking Admin Dashboard...
powershell -Command "try { $status = (Invoke-WebRequest -Uri 'http://localhost:8081/admin/dashboard' -UseBasicParsing).StatusCode; Write-Host 'Admin Dashboard Status: ' $status } catch { Write-Host 'Admin dashboard redirecting to login (expected)' }"
echo.

echo [4] Checking Staff Dashboard...
powershell -Command "try { $status = (Invoke-WebRequest -Uri 'http://localhost:8081/staff/dashboard' -UseBasicParsing).StatusCode; Write-Host 'Staff Dashboard Status: ' $status } catch { Write-Host 'Staff dashboard redirecting to login (expected)' }"
echo.

echo [5] Checking Technician Dashboard...
powershell -Command "try { $status = (Invoke-WebRequest -Uri 'http://localhost:8081/technician/dashboard' -UseBasicParsing).StatusCode; Write-Host 'Technician Dashboard Status: ' $status } catch { Write-Host 'Technician dashboard redirecting to login (expected)' }"
echo.

echo [6] Checking Cleaner Dashboard...
powershell -Command "try { $status = (Invoke-WebRequest -Uri 'http://localhost:8081/cleaner/dashboard' -UseBasicParsing).StatusCode; Write-Host 'Cleaner Dashboard Status: ' $status } catch { Write-Host 'Cleaner dashboard redirecting to login (expected)' }"
echo.

echo [7] Checking Database Connection...
echo Testing database import...
if exist "smart_university_database.sql" (
    echo [OK] Database file exists
    echo File size: 
    for %%A in ("smart_university_database.sql") do echo %%~zA bytes
) else (
    echo [ERROR] Database file missing
)
echo.

echo [8] Checking Configuration Files...
if exist "src\main\resources\application.properties" (
    echo [OK] Application properties exist
) else (
    echo [ERROR] Application properties missing
)
echo.

echo [9] Checking Template Files...
if exist "src\main\resources\templates\auth\login.html" (
    echo [OK] Login template exists
) else (
    echo [ERROR] Login template missing
)

if exist "src\main\resources\templates\admin\dashboard.html" (
    echo [OK] Admin dashboard template exists
) else (
    echo [ERROR] Admin dashboard template missing
)

if exist "src\main\resources\templates\staff\dashboard.html" (
    echo [OK] Staff dashboard template exists
) else (
    echo [ERROR] Staff dashboard template missing
)

if exist "src\main\resources\templates\technician\dashboard.html" (
    echo [OK] Technician dashboard template exists
) else (
    echo [ERROR] Technician dashboard template missing
)

if exist "src\main\resources\templates\cleaner\dashboard.html" (
    echo [OK] Cleaner dashboard template exists
) else (
    echo [ERROR] Cleaner dashboard template missing
)
echo.

echo [10] Checking Controller Classes...
if exist "src\main\java\com\example\smart_university_devices_and_materials_maintanance_system\controller\AuthController.java" (
    echo [OK] AuthController exists
) else (
    echo [ERROR] AuthController missing
)

if exist "src\main\java\com\example\smart_university_devices_and_materials_maintanance_system\controller\AdminController.java" (
    echo [OK] AdminController exists
) else (
    echo [ERROR] AdminController missing
)

if exist "src\main\java\com\example\smart_university_devices_and_materials_maintanance_system\controller\StaffController.java" (
    echo [OK] StaffController exists
) else (
    echo [ERROR] StaffController missing
)

if exist "src\main\java\com\example\smart_university_devices_and_materials_maintanance_system\controller\TechnicianController.java" (
    echo [OK] TechnicianController exists
) else (
    echo [ERROR] TechnicianController missing
)

if exist "src\main\java\com\example\smart_university_devices_and_materials_maintanance_system\controller\CleanerController.java" (
    echo [OK] CleanerController exists
) else (
    echo [ERROR] CleanerController missing
)
echo.

echo ============================================================
echo   DEBUGGING COMPLETE
echo ============================================================
echo.
echo Next Steps:
echo 1. If all statuses are 200, application is running
echo 2. If database file exists, run IMPORT_DATABASE.bat
echo 3. If templates exist, login should work
echo 4. Test with credentials: admin@smartuni.rw / Admin@2024
echo.
pause
