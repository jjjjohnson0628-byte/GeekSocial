@echo off
REM Start the SocialApp Server
REM This enables online interaction between multiple instances

setlocal enabledelayedexpansion

echo ============================================================
echo SocialApp Server Startup
echo ============================================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java JDK 11 or higher
    echo Download from: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

echo Compiling SocialAppServer.java...
javac -cp "gson-2.10.1.jar" SocialAppServer.java
if errorlevel 1 (
    echo ERROR: Compilation failed
    pause
    exit /b 1
)

echo.
echo Starting server on http://127.0.0.1:8080...
echo Press Ctrl+C to stop the server
echo.

java -cp ".;gson-2.10.1.jar" SocialAppServer

pause
