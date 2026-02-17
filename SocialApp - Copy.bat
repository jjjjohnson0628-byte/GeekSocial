@echo off
REM =========================================
REM SocialApp compile & run script
REM =========================================

REM Set JDK 25 as JAVA_HOME
set JAVA_HOME=C:\Program Files\Java\jdk-25
set PATH=%JAVA_HOME%\bin;%PATH%

REM Paths
set FX_LIB=C:\Users\Jjjjo\Desktop\HomePrograms\javafx-sdk-25.0.2\lib
set GSON_JAR=gson-2.10.1.jar

REM Compile SocialApp
echo Compiling SocialApp...
javac --module-path "%FX_LIB%" --add-modules javafx.controls,javafx.media,javafx.swing -cp ".;%GSON_JAR%" SocialApp.java
if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b
)

REM Run SocialApp
echo Running SocialApp...
java --module-path "%FX_LIB%" --add-modules javafx.controls,javafx.media,javafx.swing -cp ".;%GSON_JAR%" SocialApp

pause
