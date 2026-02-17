@echo off
REM SocialApp Packager - Creates a portable, shareable package
setlocal enabledelayedexpansion

echo =====================================
echo SocialApp Packager
echo =====================================
echo.
echo This script will create a portable ZIP file for distribution.
echo.

set SCRIPT_DIR=%~dp0
set PACKAGE_NAME=SocialApp_%date:~-4%-%date:~-10,2%-%date:~-7,2%_%time:~0,2%-%time:~3,2%

REM Remove colons from timestamp for filename
set PACKAGE_NAME=%PACKAGE_NAME::=-%

echo Creating package: %PACKAGE_NAME%.zip
echo.

REM Create temporary folder
if exist "%SCRIPT_DIR%_temp_package" rmdir /s /q "%SCRIPT_DIR%_temp_package"
mkdir "%SCRIPT_DIR%_temp_package\SocialApp"

echo Copying files...

REM Copy essential files
copy "%SCRIPT_DIR%SocialApp.bat" "%SCRIPT_DIR%_temp_package\SocialApp\" >nul
copy "%SCRIPT_DIR%Launch.vbs" "%SCRIPT_DIR%_temp_package\SocialApp\" >nul
copy "%SCRIPT_DIR%README.md" "%SCRIPT_DIR%_temp_package\SocialApp\" >nul
copy "%SCRIPT_DIR%GeekSocial.jar" "%SCRIPT_DIR%_temp_package\SocialApp\" >nul
copy "%SCRIPT_DIR%manifest.txt" "%SCRIPT_DIR%_temp_package\SocialApp\" >nul

REM Copy JAR dependencies
copy "%SCRIPT_DIR%gson-2.10.1.jar" "%SCRIPT_DIR%_temp_package\SocialApp\" >nul
copy "%SCRIPT_DIR%webcam-capture-0.3.12.jar" "%SCRIPT_DIR%_temp_package\SocialApp\" >nul
copy "%SCRIPT_DIR%jna-5.13.0.jar" "%SCRIPT_DIR%_temp_package\SocialApp\" >nul
copy "%SCRIPT_DIR%bridj-0.7.0.jar" "%SCRIPT_DIR%_temp_package\SocialApp\" >nul
copy "%SCRIPT_DIR%slf4j-api-2.0.5.jar" "%SCRIPT_DIR%_temp_package\SocialApp\" >nul
copy "%SCRIPT_DIR%slf4j-nop-2.0.5.jar" "%SCRIPT_DIR%_temp_package\SocialApp\" >nul

REM Copy JavaFX SDK
echo Copying JavaFX SDK (this may take a moment)...
xcopy "%SCRIPT_DIR%javafx-sdk-25.0.2" "%SCRIPT_DIR%_temp_package\SocialApp\javafx-sdk-25.0.2" /E /I /Q >nul

REM Copy data folders if they exist
if exist "%SCRIPT_DIR%data" (
    echo Copying data...
    xcopy "%SCRIPT_DIR%data" "%SCRIPT_DIR%_temp_package\SocialApp\data" /E /I /Q >nul
)

if exist "%SCRIPT_DIR%media" (
    echo Copying media files...
    xcopy "%SCRIPT_DIR%media" "%SCRIPT_DIR%_temp_package\SocialApp\media" /E /I /Q >nul
)

echo Creating ZIP file...

REM Check if 7-Zip is available
if exist "C:\Program Files\7-Zip\7z.exe" (
    "C:\Program Files\7-Zip\7z.exe" a -tzip "%SCRIPT_DIR%!PACKAGE_NAME!.zip" "%SCRIPT_DIR%_temp_package\SocialApp" >nul
    goto cleanup
)

if exist "C:\Program Files (x86)\7-Zip\7z.exe" (
    "C:\Program Files (x86)\7-Zip\7z.exe" a -tzip "%SCRIPT_DIR%!PACKAGE_NAME!.zip" "%SCRIPT_DIR%_temp_package\SocialApp" >nul
    goto cleanup
)

REM Fallback to PowerShell if 7-Zip not found
powershell -Command "Compress-Archive -Path '%SCRIPT_DIR%_temp_package\SocialApp' -DestinationPath '%SCRIPT_DIR%!PACKAGE_NAME!.zip' -Force" 2>nul
if %ERRORLEVEL% EQU 0 goto cleanup

REM If all else fails
echo Error: Could not create ZIP file. Please ensure 7-Zip is installed or use a ZIP utility manually.
pause
exit /b 1

:cleanup
rmdir /s /q "%SCRIPT_DIR%_temp_package"

echo.
echo =====================================
echo Package created successfully!
echo =====================================
echo.
echo File: !PACKAGE_NAME!.zip
echo Size: Check Windows Explorer
echo.
echo The package includes:
echo - Compiled application (GeekSocial.jar)
echo - All dependencies (GSON, Webcam, JavaFX, etc.)
echo - Windows launcher (Launch.vbs)
echo - Batch file (SocialApp.bat)
echo - Documentation (README.md)
echo - User data and media files
echo.
echo To share this app:
echo 1. Send the ZIP file to others
echo 2. They extract it
echo 3. Double-click Launch.vbs to run
echo   (Just need Java installed on their system)
echo.
echo =====================================
pause
