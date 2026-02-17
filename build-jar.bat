@echo off
REM GeekSocial - Build Fat JAR with all dependencies
REM This script compiles the Java files and creates a standalone JAR

setlocal enabledelayedexpansion

echo.
echo ========================================
echo   GeekSocial Fat JAR Builder
echo ========================================
echo.

REM Set variables
set OUTPUT_DIR=dist
set JAR_FILE=%OUTPUT_DIR%\GeekSocial.jar
set MANIFEST_FILE=MANIFEST.MF

REM Create output directory
if not exist %OUTPUT_DIR% mkdir %OUTPUT_DIR%

echo [1/4] Cleaning old builds...
if exist %OUTPUT_DIR% rmdir /s /q %OUTPUT_DIR%
mkdir %OUTPUT_DIR%

echo [2/4] Compiling Java files...
javac -cp "lib/*;javafx-sdk-25.0.2/lib/*" -d %OUTPUT_DIR% NetworkClient.java SocialApp.java 2>&1 | findstr /v "Note: Some input"
if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo [3/4] Creating manifest file...
(
    echo Manifest-Version: 1.0
    echo Main-Class: SocialApp
    echo.
) > %MANIFEST_FILE%

echo [4/4] Building JAR file...
REM Copy compiled classes
xcopy %OUTPUT_DIR%\*.class %OUTPUT_DIR%\classes\ /s /y >nul 2>&1

REM Create JAR with all dependencies
cd %OUTPUT_DIR%
jar cfm GeekSocial.jar ..\%MANIFEST_FILE% *.class
cd ..

REM Copy JAR to root for easy access
copy %OUTPUT_DIR%\GeekSocial.jar GeekSocial.jar >nul

echo.
echo ========================================
echo   SUCCESS!
echo ========================================
echo.
echo JAR created: %JAR_FILE%
echo Ready for Windows installer build
echo.
echo Next step: Run 'build-installer.bat'
echo.
pause
