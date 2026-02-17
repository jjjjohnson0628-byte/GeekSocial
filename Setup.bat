@echo off
REM SocialApp Installation Wizard
REM Guides users through initial setup and troubleshooting

setlocal enabledelayedexpansion

:menu
cls
echo =====================================
echo SocialApp Installation & Setup Wizard
echo =====================================
echo.
echo What would you like to do?
echo.
echo 1. Check System Requirements
echo 2. Install Java (Windows only)
echo 3. Build the Application
echo 4. Run the Application
echo 5. Create Shareable Package
echo 6. Run System Diagnostics
echo 7. Exit
echo.
set /p choice="Enter your choice (1-7): "

if "%choice%"=="1" goto check_requirements
if "%choice%"=="2" goto install_java
if "%choice%"=="3" goto build_app
if "%choice%"=="4" goto run_app
if "%choice%"=="5" goto create_package
if "%choice%"=="6" goto diagnostics
if "%choice%"=="7" exit /b 0

echo Invalid choice. Please try again.
pause
goto menu

:check_requirements
cls
echo =====================================
echo System Requirements Check
echo =====================================
echo.

REM Check Java
echo Checking Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo [X] Java NOT FOUND
    echo Solution: See menu option 2 (Install Java) or visit:
    echo https://www.oracle.com/java/technologies/downloads/
) else (
    java -version 2>&1 | findstr /R ".*" && echo [OK] Java is installed
)

REM Check Java version
echo.
echo Checking Java version (requires 11 or higher)...
for /f "tokens=*" %%i in ('java -version 2^>^&1 ^| findstr "version"') do (
    echo %%i
)

REM Check disk space (requires at least 500MB)
echo.
echo Checking disk space...
for /f "tokens=3" %%a in ('dir /-c ^| find "bytes free"') do (
    set "free_space=%%a"
)
if defined free_space (
    echo Available space: !free_space! bytes
)

REM Check for GeekSocial.jar
echo.
echo Checking compiled application...
if exist GeekSocial.jar (
    echo [OK] GeekSocial.jar found
) else (
    echo [X] GeekSocial.jar NOT FOUND
    echo Solution: Run menu option 3 (Build the Application)
)

REM Check for JavaFX
echo.
echo Checking JavaFX SDK...
if exist javafx-sdk-25.0.2\lib (
    echo [OK] JavaFX SDK found
) else (
    echo [X] JavaFX SDK NOT FOUND
)

echo.
pause
goto menu

:install_java
cls
echo =====================================
echo Java Installation Guide
echo =====================================
echo.
echo Option 1: Oracle Java (Official)
echo - Visit: https://www.oracle.com/java/technologies/downloads/
echo - Click "Download JDK" for your OS
echo - Run installer
echo - Keep all defaults
echo - Restart your computer
echo.
echo Option 2: OpenJDK (Free Alternative)
echo - Visit: https://adoptopenjdk.net/
echo - Download latest version
echo - Run installer
echo - Restart your computer
echo.
echo After installing Java:
echo 1. Restart your computer
echo 2. Come back to this menu
echo 3. Choose option 1 to verify Java is installed
echo.
pause
goto menu

:build_app
cls
echo =====================================
echo Building SocialApp
echo =====================================
echo.

REM Check if Java is available first
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java first (menu option 2) and restart
    pause
    goto menu
)

echo Running Build.bat...
call Build.bat

echo.
echo Build completed!
echo GeekSocial.jar should now exist.
pause
goto menu

:run_app
cls
echo =====================================
echo Running SocialApp
echo =====================================
echo.

REM Check if Java is available
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed
    echo Please install Java (menu option 2) first
    pause
    goto menu
)

REM Check if jar exists
if not exist GeekSocial.jar (
    echo Error: GeekSocial.jar not found
    echo Please build the application first (menu option 3)
    pause
    goto menu
)

echo Starting SocialApp...
call SocialApp.bat
pause
goto menu

:create_package
cls
echo =====================================
echo Create Shareable Package
echo =====================================
echo.

REM Check if jar exists
if not exist GeekSocial.jar (
    echo Error: GeekSocial.jar not found
    echo Please build the application first (menu option 3)
    pause
    goto menu
)

echo Running Package.bat...
call Package.bat

echo.
pause
goto menu

:diagnostics
cls
echo =====================================
echo System Diagnostics
echo =====================================
echo.

REM Detailed Java check
echo === Java Information ===
java -version 2>&1
echo.

REM Check all required files
echo === Required Files ===
for %%f in (GeekSocial.jar gson-2.10.1.jar webcam-capture-0.3.12.jar jna-5.13.0.jar bridj-0.7.0.jar slf4j-api-2.0.5.jar slf4j-nop-2.0.5.jar) do (
    if exist %%f (
        echo [OK] %%f
    ) else (
        echo [X] %%f - MISSING
    )
)

echo.
echo === JavaFX SDK ===
if exist javafx-sdk-25.0.2\lib (
    echo [OK] JavaFX SDK found at javafx-sdk-25.0.2
) else (
    echo [X] JavaFX SDK not found
)

echo.
echo === Directories ===
if exist data (
    echo [OK] data/ directory exists
) else (
    echo [ ] data/ directory not found (will be created on first run)
)

if exist media (
    echo [OK] media/ directory exists
) else (
    echo [ ] media/ directory not found (will be created on first run)
)

echo.
echo === Configuration ===
echo Current Directory: %CD%
echo.

echo === Recommendation ===
if exist GeekSocial.jar (
    java -version >nul 2>&1
    if errorlevel 1 (
        echo [!] Java is missing. Install Java and restart.
    ) else (
        echo [OK] System ready! You can run the app.
    )
) else (
    echo [!] Application not built. Run option 3 to build.
)

echo.
pause
goto menu

endlocal
