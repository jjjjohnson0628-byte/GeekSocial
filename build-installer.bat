@echo off
REM GeekSocial - Windows Installer Builder
REM Creates a professional Windows .exe installer using jpackage

setlocal enabledelayedexpansion

echo.
echo ========================================
echo   GeekSocial Windows Installer Builder
echo ========================================
echo.

REM Check if JAR exists
if not exist "GeekSocial.jar" (
    echo ERROR: GeekSocial.jar not found!
    echo Please run 'build-jar.bat' first
    echo.
    pause
    exit /b 1
)

REM Check if jpackage is available
where jpackage >nul 2>&1
if errorlevel 1 (
    echo ERROR: jpackage not found in PATH!
    echo.
    echo You need Java 17+ with jpackage installed.
    echo Download from: https://www.oracle.com/java/technologies/downloads/
    echo.
    echo Or install OpenJDK 17+ with tools:
    echo   - Windows (Chocolatey): choco install openjdk17
    echo   - Windows (Manual): eclipse-temurin.js.org
    echo.
    pause
    exit /b 1
)

set OUTPUT_DIR=installer
set INPUT_JAR=GeekSocial.jar
set APP_NAME=GeekSocial
set APP_VERSION=1.0
set VENDOR=GeekSocial
set MODULE_PATH=javafx-sdk-25.0.2/lib

echo.
echo [1/3] Cleaning old installer builds...
if exist %OUTPUT_DIR% rmdir /s /q %OUTPUT_DIR%

echo [2/3] Creating Windows installer...

REM Create installer with jpackage
jpackage ^
    --input . ^
    --name %APP_NAME% ^
    --main-jar %INPUT_JAR% ^
    --main-class SocialApp ^
    --type exe ^
    --app-version %APP_VERSION% ^
    --vendor %VENDOR% ^
    --dest %OUTPUT_DIR% ^
    --win-console ^
    --win-shortcut ^
    --win-menu ^
    --icon GeekSocial-icon.ico 2>nul

REM If icon doesn't exist, build without it
if !errorlevel! neq 0 (
    echo [2/3] Building without custom icon...
    jpackage ^
        --input . ^
        --name %APP_NAME% ^
        --main-jar %INPUT_JAR% ^
        --main-class SocialApp ^
        --type exe ^
        --app-version %APP_VERSION% ^
        --vendor %VENDOR% ^
        --dest %OUTPUT_DIR% ^
        --win-console ^
        --win-shortcut ^
        --win-menu
)

if errorlevel 1 (
    echo.
    echo ERROR: Installer creation failed!
    echo.
    echo Troubleshooting:
    echo - Make sure jpackage is in your PATH (check: jpackage --version)
    echo - Java 17+ is required (check: java -version)
    echo - Try again with: %OUTPUT_DIR%\GeekSocial-*-installer.exe
    echo.
    pause
    exit /b 1
)

echo [3/3] Finalizing installer...

REM Find the generated installer
for /f "delims=" %%F in ('dir /b %OUTPUT_DIR%\*.exe 2^>nul') do (
    set INSTALLER=%%F
    goto found
)

:found
if defined INSTALLER (
    echo.
    echo ========================================
    echo   SUCCESS!
    echo ========================================
    echo.
    echo Installer created: %OUTPUT_DIR%\%INSTALLER%
    echo.
    echo You can now:
    echo   1. Run the installer: %OUTPUT_DIR%\%INSTALLER%
    echo   2. Share it with users
    echo   3. Upload to GitHub Releases
    echo.
) else (
    echo.
    echo Installer build may have failed - check %OUTPUT_DIR%
    echo.
)

pause
