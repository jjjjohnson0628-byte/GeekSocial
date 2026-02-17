@echo off
REM SocialApp Dependency Checker
REM Verifies that all necessary components are available

setlocal enabledelayedexpansion

echo =====================================
echo SocialApp System Check
echo =====================================
echo.

REM Check Java
echo Checking for Java...
java -version > nul 2>&1
if errorlevel 1 (
    echo [X] Java NOT FOUND
    echo.
    echo To fix this:
    echo 1. Download Java from: https://www.oracle.com/java/technologies/downloads/
    echo 2. Install JDK 11 or higher
    echo 3. Restart your computer
    echo 4. Run this script again
    echo.
    pause
    exit /b 1
) else (
    for /f "tokens=*" %%i in ('java -version 2^>^&1') do set JAVA_VERSION=%%i
    echo [OK] !JAVA_VERSION!
)

echo.
echo Checking for required files...

REM Check script directory
set SCRIPT_DIR=%~dp0

REM Check for GeekSocial.jar
if exist "%SCRIPT_DIR%GeekSocial.jar" (
    echo [OK] GeekSocial.jar found
) else (
    echo [X] GeekSocial.jar NOT FOUND
    echo.
    echo To fix:
    echo Run: Build.bat
    echo.
    pause
    exit /b 1
)

REM Check for JavaFX SDK
if exist "%SCRIPT_DIR%javafx-sdk-25.0.2\lib" (
    echo [OK] JavaFX SDK found
) else (
    echo [X] JavaFX SDK NOT FOUND
    echo.
    echo The app cannot work without JavaFX
    echo Please ensure javafx-sdk-25.0.2 folder exists in: %SCRIPT_DIR%
    echo.
    pause
    exit /b 1
)

REM Check for essential JAR files
set JAR_FILES=gson-2.10.1.jar webcam-capture-0.3.12.jar jna-5.13.0.jar bridj-0.7.0.jar slf4j-api-2.0.5.jar slf4j-nop-2.0.5.jar

for %%j in (%JAR_FILES%) do (
    if exist "%SCRIPT_DIR%%%j" (
        echo [OK] %%j found
    ) else (
        echo [X] %%j NOT FOUND
    )
)

echo.
echo =====================================
echo System check complete!
echo =====================================
echo.
echo All systems are GO!
echo You can now run: Launch.vbs
echo.
pause
