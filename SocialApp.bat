@echo off
setlocal enabledelayedexpansion

rem Get script directory
set SCRIPT_DIR=%~dp0

rem Check if Java is available
java -version >nul 2>&1
if errorlevel 1 (
    echo Java is not installed or not in PATH
    echo Please install Java JDK 11 or higher
    pause
    exit /b 1
)

set JFX_LIB=%SCRIPT_DIR%javafx-sdk-25.0.2\lib
set JAR_FILE=%SCRIPT_DIR%GeekSocial.jar
set CLASSPATH=%JAR_FILE%;%SCRIPT_DIR%gson-2.10.1.jar;%SCRIPT_DIR%webcam-capture-0.3.12.jar;%SCRIPT_DIR%jna-5.13.0.jar;%SCRIPT_DIR%bridj-0.7.0.jar;%SCRIPT_DIR%slf4j-api-2.0.5.jar;%SCRIPT_DIR%slf4j-nop-2.0.5.jar

rem Check if JAR file exists
if not exist "%JAR_FILE%" (
    echo GeekSocial.jar not found. Please run Build.bat first.
    pause
    exit /b 1
)

java --module-path "%JFX_LIB%" --add-modules javafx.controls,javafx.media,javafx.swing -cp "%CLASSPATH%" SocialApp

if errorlevel 1 (
    echo Error running application
    pause
)

endlocal
