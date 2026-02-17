@echo off
REM ============================================================
REM SocialApp - Automatic Installation & Setup
REM ============================================================
REM This script:
REM - Checks if Java is installed
REM - Creates necessary directories
REM - Compiles the application
REM - Sets up the environment
REM ============================================================

setlocal enabledelayedexpansion
color 0A
cls

echo.
echo ============================================================
echo          SocialApp - Installation & Setup
echo ============================================================
echo.

REM Check Windows version
for /f "tokens=4-5 delims=. " %%i in ('ver') do set VERSION=%%i.%%j
if "%version%" == "10.0" (
    echo ✓ Running on Windows 10/11
) else (
    echo ⚠ Warning: This application is tested on Windows 10/11
)
echo.

REM ============================================================
REM 1. Check Java Installation
REM ============================================================
echo [1/5] Checking Java installation...
echo.

java -version >nul 2>&1
if errorlevel 1 (
    echo ✗ ERROR: Java is NOT installed
    echo.
    echo Please install Java first:
    echo - Go to: https://www.oracle.com/java/technologies/downloads/
    echo - Download: JDK 11 or later (LTS version recommended)
    echo - Install with default settings
    echo - Restart this script after installing Java
    echo.
    pause
    exit /b 1
)

REM Get Java version
for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| find "version"') do set JAVA_VERSION=%%i
echo ✓ Java installed: %JAVA_VERSION%
echo.

REM ============================================================
REM 2. Create Necessary Directories
REM ============================================================
echo [2/5] Creating directories...
echo.

if not exist "data" (
    mkdir data
    echo ✓ Created: data/
)

if not exist "media" (
    mkdir media
    echo ✓ Created: media/
)

if not exist "media\photos" (
    mkdir media\photos
    echo ✓ Created: media/photos/
)

if not exist "media\videos" (
    mkdir media\videos
    echo ✓ Created: media/videos/
)

if not exist "temp" (
    mkdir temp
    echo ✓ Created: temp/
)

echo.

REM ============================================================
REM 3. Compile Java Files
REM ============================================================
echo [3/5] Compiling application...
echo.

if not exist "gson-2.10.1.jar" (
    echo ⚠ Warning: gson-2.10.1.jar not found
    echo Some features may not work properly
    echo Download from: https://github.com/google/gson/releases
)

if exist "SocialAppServer.java" (
    echo Compiling SocialAppServer.java...
    javac -cp "gson-2.10.1.jar" SocialAppServer.java >nul 2>&1
    if errorlevel 1 (
        echo ✗ Failed to compile SocialAppServer
    ) else (
        echo ✓ Compiled: SocialAppServer
    )
)

if exist "NetworkClient.java" (
    echo Compiling NetworkClient.java...
    javac -cp "gson-2.10.1.jar" NetworkClient.java >nul 2>&1
    if errorlevel 1 (
        echo ✗ Failed to compile NetworkClient
    ) else (
        echo ✓ Compiled: NetworkClient
    )
)

if exist "SocialApp.java" (
    echo Compiling SocialApp.java...
    javac -cp "gson-2.10.1.jar;javafx-sdk-25.0.2/lib/*" --add-modules javafx.controls,javafx.media,javafx.swing SocialApp.java >nul 2>&1
    if errorlevel 1 (
        echo ✗ Failed to compile SocialApp
    ) else (
        echo ✓ Compiled: SocialApp
    )
)

echo.

REM ============================================================
REM 4. Create Launch Scripts (Backup)
REM ============================================================
echo [4/5] Setting up launch scripts...
echo.

if not exist "StartServer.bat" (
    echo ✓ StartServer.bat already exists
) else (
    echo ✓ StartServer.bat ready
)

if not exist "QuickStart.bat" (
    echo ✓ QuickStart.bat will be created
) else (
    echo ✓ QuickStart.bat ready
)

echo.

REM ============================================================
REM 5. Summary & Next Steps
REM ============================================================
echo [5/5] Installation complete!
echo.
echo ============================================================
echo                    SETUP SUCCESSFUL
echo ============================================================
echo.
echo Your SocialApp is ready to use!
echo.
echo QUICK START OPTIONS:
echo.
echo Option 1 - EASY (Recommended):
echo   Double-click: QuickStart.bat
echo   (Automatically starts server + app)
echo.
echo Option 2 - MANUAL:
echo   Step 1: Double-click StartServer.bat (keep open)
echo   Step 2: Double-click Launch.vbs
echo   Step 3: Enable Online Mode in app menu
echo.
echo Option 3 - COMMAND LINE:
echo   Run: SocialApp.bat
echo.
echo ============================================================
echo.
echo More info: Read QUICK_START.txt or ONLINE_MODE.txt
echo.
pause
