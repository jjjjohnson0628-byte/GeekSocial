@echo off
REM GeekSocial - Windows Installer Builder
setlocal enabledelayedexpansion

echo.
echo ========================================
echo   GeekSocial Windows Installer Builder
echo ========================================
echo.

REM Check if JAR exists
if not exist "GeekSocial.jar" (
    echo ERROR: GeekSocial.jar not found!
    echo Please run build-jar.bat first
    echo.
    pause
    exit /b 1
)

REM Check if jpackage is available
where jpackage >nul 2>&1
if errorlevel 1 (
    echo ERROR: jpackage not found in PATH!
    echo You need Java 17+ with jpackage installed.
    echo.
    pause
    exit /b 1
)

set OUTPUT_DIR=installer
set INPUT_JAR=GeekSocial.jar
set APP_NAME=GeekSocial
set APP_VERSION=1.0
set VENDOR=GeekSocial

echo [1/3] Cleaning old builds...
if exist "%OUTPUT_DIR%" (
    rmdir /s /q "%OUTPUT_DIR%"
)

echo [2/3] Creating Windows installer...

REM Create the installer
jpackage --input . --name %APP_NAME% --main-jar %INPUT_JAR% --main-class SocialApp --type exe --app-version %APP_VERSION% --vendor %VENDOR% --dest %OUTPUT_DIR% --win-console --win-shortcut --win-menu

if errorlevel 1 (
    echo ERROR: Installer creation failed!
    pause
    exit /b 1
)

echo [3/3] Finalizing...
echo.
echo ========================================
echo   SUCCESS!
echo ========================================
echo.

dir "%OUTPUT_DIR%\*.exe"

echo.
echo Installer created in: %OUTPUT_DIR%
echo.

pause
