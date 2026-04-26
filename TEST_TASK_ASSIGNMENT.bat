@echo off
title Smart University MMS - Task Assignment Test
echo ============================================================
echo   TASK ASSIGNMENT FUNCTIONALITY TEST
echo ============================================================
echo.

echo [1] Testing Admin Maintenance Page Access...
powershell -Command "try { $status = (Invoke-WebRequest -Uri 'http://localhost:8081/admin/maintenance' -UseBasicParsing).StatusCode; Write-Host 'Status: ' $status } catch { Write-Host 'Error: ' $_.Exception.Message }"
echo.

echo [2] Checking CSS Loading for Button Styles...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8081/admin/maintenance' -UseBasicParsing; if ($response.Content -like '*btn btn-sm*') { Write-Host '[OK] Button classes found in HTML' } else { Write-Host '[ERROR] Button classes missing' } } catch { Write-Host 'Error checking content' }"
echo.

echo [3] Checking Modal Structure...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8081/admin/maintenance' -UseBasicParsing; if ($response.Content -like '*assignModal*') { Write-Host '[OK] Modal structure found' } else { Write-Host '[ERROR] Modal structure missing' } } catch { Write-Host 'Error checking modal' }"
echo.

echo [4] Checking Bootstrap JavaScript Loading...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8081/admin/maintenance' -UseBasicParsing; if ($response.Content -like '*data-bs-toggle*') { Write-Host '[OK] Bootstrap attributes found' } else { Write-Host '[ERROR] Bootstrap attributes missing' } } catch { Write-Host 'Error checking Bootstrap' }"
echo.

echo [5] Verifying AdminController Endpoints...
if exist "src\main\java\com\example\smart_university_devices_and_materials_maintanance_system\controller\AdminController.java" (
    findstr /C:"@PostMapping.*maintenance.*assign" "src\main\java\com\example\smart_university_devices_and_materials_maintanance_system\controller\AdminController.java" >nul
    if %errorlevel% equ 0 (
        echo [OK] Task assignment endpoint found
    ) else (
        echo [ERROR] Task assignment endpoint missing
    )
) else (
    echo [ERROR] AdminController not found
)
echo.

echo [6] Verifying MaintenanceService Methods...
if exist "src\main\java\com\example\smart_university_devices_and_materials_maintanance_system\service\MaintenanceService.java" (
    findstr /C:"assignTechnician" "src\main\java\com\example\smart_university_devices_and_materials_maintanance_system\service\MaintenanceService.java" >nul
    if %errorlevel% equ 0 (
        echo [OK] assignTechnician method found
    ) else (
        echo [ERROR] assignTechnician method missing
    )
) else (
    echo [ERROR] MaintenanceService not found
)
echo.

echo [7] Checking CSS Button Styles...
if exist "src\main\resources\static\css\main.css" (
    findstr /C:".btn-outline-success" "src\main\resources\static\css\main.css" >nul
    if %errorlevel% equ 0 (
        echo [OK] Button styles found in CSS
    ) else (
        echo [ERROR] Button styles missing from CSS
    )
) else (
    echo [ERROR] CSS file not found
)
echo.

echo [8] Testing Database Connection (if database imported)...
echo Note: This test requires database to be imported first
echo Run IMPORT_DATABASE.bat if not already done
echo.

echo ============================================================
echo   TEST COMPLETE
echo ============================================================
echo.
echo Next Steps:
echo 1. If all tests pass, task assignment should work
echo 2. Import database if not done: IMPORT_DATABASE.bat
echo 3. Login as admin: admin@smartuni.rw / Admin@2024
echo 4. Navigate to: http://localhost:8081/admin/maintenance
echo 5. Click "Assign" button on any PENDING task
echo 6. Select technician and confirm assignment
echo.
pause
