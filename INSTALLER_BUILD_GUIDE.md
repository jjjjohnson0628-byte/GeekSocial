# GeekSocial Windows Installer Build Guide

## Quick Start

### Build the Installer (3 steps):

```powershell
# Step 1: Build the fat JAR
.\build-jar.bat

# Step 2: Create the Windows installer  
.\build-installer.bat

# Step 3: Share installer from the 'installer' folder
# Installer will be: installer\GeekSocial-1.0-installer.exe
```

---

## Requirements

- **Java 17+** with jpackage tool
  - Download: https://www.oracle.com/java/technologies/downloads/
  - Or: `choco install openjdk17` (if you have Chocolatey)
  
- **Windows**: Windows 7 or later

---

## How It Works

### `build-jar.bat`
Compiles Java source files and creates a "fat JAR" containing:
- All compiled classes (SocialApp, NetworkClient, etc.)
- All dependencies from `lib/` folder
- Manifest pointing to `SocialApp` as main entry point

Output: `GeekSocial.jar` (standalone, ~5-10 MB)

### `build-installer.bat`
Uses Java's `jpackage` tool to create a Windows installer:
- Bundles the JAR and JVM runtime
- Creates an `.exe` installer (~200+ MB)
- Adds Start Menu shortcuts
- Sets up uninstall capability

Output: `installer\GeekSocial-1.0-installer.exe`

---

## Step-by-Step Build Instructions

### 1. Verify Java Installation
```powershell
java -version
jpackage --version
```

If `jpackage --version` fails, you need Java 17+ with jpackage.

### 2. Build the JAR
```powershell
cd C:\Users\Jjjjo\Desktop\HomePrograms\SocialApp
.\build-jar.bat
```

Expected output:
```
[1/4] Cleaning old builds...
[2/4] Compiling Java files...
[3/4] Creating manifest file...
[4/4] Building JAR file...
========================================
  SUCCESS!
========================================
JAR created: dist\GeekSocial.jar
Ready for Windows installer build
```

### 3. Create the Installer
```powershell
.\build-installer.bat
```

Expected output:
```
[1/3] Cleaning old installer builds...
[2/3] Creating Windows installer...
[3/3] Finalizing installer...
========================================
  SUCCESS!
========================================
Installer created: installer\GeekSocial-1.0-installer.exe
```

### 4. Test the Installer
```powershell
.\installer\GeekSocial-1.0-installer.exe
```

User will see:
1. License agreement
2. Installation directory choice
3. Start Menu folder choice
4. Installation progress
5. "Installation Complete" dialog

---

## Distributing the Installer

### Option A: GitHub Releases (Recommended)
1. Go to: https://github.com/jjjjohnson0628-byte/GeekSocial/releases
2. Click "Create a new release"
3. Tag: `v1.0`
4. Upload: `installer\GeekSocial-1.0-installer.exe`
5. Description: "Download and run the installer - no Java installation needed!"

Users can then download from: `https://github.com/jjjjohnson0628-byte/GeekSocial/releases/download/v1.0/GeekSocial-1.0-installer.exe`

### Option B: Your Website
Host the `.exe` on your website and link to it.

### Option C: Direct File Sharing
Share `GeekSocial-1.0-installer.exe` via email, cloud storage, etc.

---

## What Users Get

After installing:
- ✅ GeekSocial appears in Start Menu
- ✅ Desktop shortcut (optional)
- ✅ Automatic cloud server connection
- ✅ All features (posts, messaging, live streaming)
- ✅ Easy uninstall via Control Panel → Programs

---

## Troubleshooting

### "jpackage not found"
- Install Java 17+: https://www.oracle.com/java/technologies/downloads/
- Verify: `jpackage --version`

### "JAR not found" / Compilation errors
- Run `build-jar.bat` first
- Check that `lib/` folder exists with all JAR files

### Installer is too large (200+ MB)
- This is normal - includes JVM runtime
- Users only download once

### Icon not found
- Optional: Create `GeekSocial-icon.ico` (256x256)
- Place in project root
- Rebuild installer

---

## Advanced: Custom Icon

To add a custom application icon:

1. Create a 256x256 pixel PNG image
2. Convert to ICO: https://convertio.co/png-ico/
3. Save as `GeekSocial-icon.ico` in project root
4. Run `build-installer.bat` again

The icon will appear on:
- Start Menu
- Desktop shortcut
- Taskbar when running
- Program files folder

---

## Version Updates

To create a new installer version:

1. Update version in `build-installer.bat`:
   ```batch
   set APP_VERSION=1.1
   ```

2. Rebuild:
   ```powershell
   .\build-jar.bat
   .\build-installer.bat
   ```

3. Create new GitHub release with updated version tag

---

## Notes

- Cloud server URL is automatically set to: `https://geeksocial.onrender.com`
- Users can override via environment variable: `SOCIAL_APP_SERVER`
- App stores local cache in `%APPDATA%\GeekSocial\`

---

Questions? Check logs in:
- `build-jar.bat` output
- `build-installer.bat` output
- Windows Event Viewer (if installer fails)
