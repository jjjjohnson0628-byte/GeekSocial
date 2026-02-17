@echo off
REM ============================================================
REM SocialApp - QuickStart Launcher
REM ============================================================
REM This script starts everything needed in one click:
REM - Server (background)
REM - Application (foreground)
REM ============================================================

setlocal enabledelayedexpansion
color 0A
cls

echo.
echo ============================================================
echo             SocialApp - QuickStart Launcher
echo ============================================================
echo.

REM Check Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed
    echo Please run Install.bat first
    pause
    exit /b 1
)

REM Check if data directory exists
if not exist "data" (
    echo Creating data directory...
    mkdir data
)

echo Starting SocialApp Server...
echo Please wait, this may take a moment...
echo.

REM Check if port 8080 is available
netstat -ano | findstr ":8080" >nul 2>&1
if not errorlevel 1 (
    echo WARNING: Port 8080 is already in use
    echo Another service may be running on this port
    echo.
    pause
)

REM Start server in background
start /B "" java -cp ".;gson-2.10.1.jar" SocialAppServer >nul 2>&1

REM Wait for server to start
timeout /t 2 /nobreak >nul

REM Start the application
echo.
echo Starting SocialApp Application...
echo.

REM Try LaunchVBS first, then fallback
if exist "Launch.vbs" (
    start "" Launch.vbs
) else (
    REM Fallback: Use direct Java launch
    java --module-path javafx-sdk-25.0.2\lib --add-modules javafx.controls,javafx.media,javafx.swing ^
      -cp "GeekSocial.jar;gson-2.10.1.jar;webcam-capture-0.3.12.jar;jna-5.13.0.jar;bridj-0.7.0.jar;slf4j-api-2.0.5.jar;slf4j-nop-2.0.5.jar" ^
      SocialApp
)

echo.
echo ============================================================
echo SocialApp is starting...
echo.
echo IMPORTANT REMINDERS:
echo - Keep this window open (server is running here)
echo - To use Online Mode: Menu > Mode > Enable Online Mode
echo - To invite others: Have them run this same script
echo - To stop: Close all windows
echo.
echo ============================================================
pause
