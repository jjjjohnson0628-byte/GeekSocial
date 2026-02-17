# GeekSocial Installer Setup Guide

## Quick Start (Portable Version)

A portable version is ready to use immediately:
1. Download the `release/` folder
2. Double-click `GeekSocial.bat` to launch the app
3. Java 11+ must be installed on your system

**Download Java if needed:** https://www.oracle.com/java/technologies/downloads/

---

## Building the Windows .exe Installer

To create a professional Windows installer (`GeekSocial.exe`), you need the **WiX Toolset** which enables jpackage to generate Windows MSI/EXE installers.

### Prerequisites

- **Java 17+** with jpackage (already have: `jdk-25`)
- **WiX Toolset 3.x or later**
- 500MB free disk space

### Step 1: Install WiX Toolset

**Option A: Download from WiX Website (Recommended)**

1. Go to https://wixtoolset.org/releases/
2. Download "WiX v3.14 (latest 3.x)" or "WiX v5" installer
3. Run the installer and complete setup
4. It will automatically add to your PATH

**Option B: Using Chocolatey (if installed)**

```powershell
choco install wixtoolset -y
```

**Option C: Download Direct Link**

Windows: https://github.com/wixtoolset/wix3/releases/download/wix314rtm/wix314.exe

---

### Step 2: Verify WiX Installation

Open PowerShell and run:
```powershell
light.exe --version
```

Should show something like: `Light version 3.14.x.x`

If it says "command not found", restart your terminal or computer for PATH changes to take effect.

---

### Step 3: Build the Installer

Once WiX is installed, run:

```powershell
cd C:\Users\Jjjjo\Desktop\HomePrograms\SocialApp
.\build-installer.bat
```

This will create `installer\GeekSocial-1.0-installer.exe` (~200MB with JVM included)

---

## Understanding the Build Process

### JAR Package (52 KB)
- `GeekSocial.jar` - All compiled classes and dependencies
- Created by `build-jar.bat`
- Can be run with: `java -jar GeekSocial.jar`

### Portable Package
- `release/` folder with JAR + launcher script
- Users just double-click `GeekSocial.bat`
- Good for quick distribution

### Professional Installer (requires WiX)
- `installer/GeekSocial-1.0-installer.exe` (~200MB)
- Includes embedded JVM (Java Runtime)
- Users run `.exe` just like any Windows program
- Creates Start Menu shortcuts
- Professional uninstall process
- No Java requirement for end users

---

## Distribution Options

### 1. **Portable ZIP (Simplest)**
- Zip the `release/` folder
- Users extract and run `GeekSocial.bat`
- Requires Java 11+ installed

### 2. **GitHub Releases (Recommended)**
- Upload `GeekSocial-1.0-installer.exe` to GitHub
- Users download directly
- Professional appearance
- Version tracking

### 3. **Website Download**
- Host the `.exe` on your website
- Direct download link
- Professional deployment

### 4. **Windows Package Manager** (Advanced)
- Submit to WinGet package repository
- Users install via: `winget install geeksocial`

---

## Troubleshooting

### Error: "WiX tools not found"
**Solution:** WiX not installed or not in PATH
1. Install WiX from https://wixtoolset.org
2. Restart PowerShell/Command Prompt
3. Verify: `light.exe --version`
4. Try building again

### Error: "jpackage not found"
**Solution:** Java 17+ not in PATH
1. Verify Java: `java -version`
2. Should show JDK 17+ (not just JRE)
3. Add Java to PATH if needed

### Installer too large (~200MB)
This is normal and expected!
- jpackage embeds the entire Java Runtime (JVM)
- Users don't need Java pre-installed
- File size typical for Java applications with JVM

---

## Next Steps

1. **Test the installer:**
   - Run `GeekSocial-1.0-installer.exe` on a test machine
   - Verify shortcuts created
   - Test launching the app

2. **Publish to GitHub Releases:**
   - Go to your repository releases page
   - Create new release v1.0
   - Upload the `.exe` file
   - Share download link with users

3. **Create Marketing Materials:**
   - Write installation instructions
   - Provide system requirements
   - Include cloud server feature highlights

---

## Questions or Issues?

- Check GitHub Issues: https://github.com/jjjjohnson0628-byte/GeekSocial
- Review error messages carefully
- Verify Java 17+ installed with `java -version`
- Verify WiX installed with `light.exe --version`
