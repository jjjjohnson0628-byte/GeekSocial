@echo off
REM SocialApp Cloud Deployment Setup Script
REM This script will help deploy your SocialApp to Render.com

echo.
echo ========== SocialApp Deployment ==========
echo.
echo Step 1: Download Git
echo   Download from: https://git-scm.com/download/win
echo   Run the installer and follow prompts
echo   IMPORTANT: Keep default settings!
echo.
echo Step 2: After installing Git, restart PowerShell
echo.
echo Step 3: Run these commands in PowerShell:
echo.
echo   cd C:\Users\Jjjjo\Desktop\HomePrograms\SocialApp
echo.
echo   git config --global user.name "Your Name"
echo   git config --global user.email "your.email@github.com"
echo.
echo Step 4: Create a GitHub repository
echo   Go to: https://github.com/new
echo   Name it: "SocialApp"
echo   Click "Create Repository"
echo.
echo Step 5: In PowerShell, push your code:
echo.
echo   git init
echo   git add .
echo   git commit -m "Initial SocialApp server with cloud deployment"
echo   git remote add origin https://github.com/YOUR_USERNAME/SocialApp.git
echo   git branch -M main
echo   git push -u origin main
echo.
echo Step 6: Deploy on Render
echo   Go to: https://render.com
echo   Sign up (free)
echo   Click "New +" then "Docker"
echo   Paste your GitHub repo URL
echo   Click Deploy (wait 3-5 minutes)
echo.
echo Step 7: Update your client
echo   Copy the Render URL (https://socialapp-server-XXXXX.onrender.com)
echo   In PowerShell:
echo   [Environment]::SetEnvironmentVariable("SOCIAL_APP_SERVER", "https://your-url", "User")
echo.
echo Step 8: Restart PowerShell and run:
echo   java -cp . SocialApp
echo.
echo ==========================================
pause
