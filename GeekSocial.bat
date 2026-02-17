@echo off
REM GeekSocial Application Launcher
REM This script runs the GeekSocial social media application

setlocal

REM Set the path to the JAR
set JAR_FILE=%~dp0GeekSocial.jar
set LIB_PATH=%~dp0lib

REM Check if JAR exists
if not exist "%JAR_FILE%" (
    echo Error: GeekSocial.jar not found!
    echo Expected at: %JAR_FILE%
    pause
    exit /b 1
)

REM Set the server mode (change to your server if needed)
REM set SOCIAL_APP_SERVER=https://geeksocial.onrender.com

REM Run the application
java -jar "%JAR_FILE%"

if errorlevel 1 (
    echo.
    echo An error occurred while running GeekSocial
    echo Please ensure Java 11+ is installed
    echo Download Java from: https://www.oracle.com/java/technologies/downloads/
    pause
)
