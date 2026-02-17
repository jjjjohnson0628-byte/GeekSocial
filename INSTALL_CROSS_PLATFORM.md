# GeekSocial - Cross-Platform Installation

## 🖥️ Windows Users

### Quick Start
1. Download `GeekSocial-portable.zip`
2. Extract the ZIP file
3. Open the `release` folder
4. **Double-click `GeekSocial.bat`**
5. App launches!

### Requirements
- Windows 7 or later
- Java 11+ (download: https://java.com)

### Troubleshooting
- If nothing happens, check Java is installed: Open Command Prompt and type `java -version`
- If still stuck, try: Open Command Prompt in the `release` folder and type: `java -jar GeekSocial.jar`

---

## 🍎 macOS Users

### Quick Start
1. Download `GeekSocial-universal.zip`
2. Extract the ZIP file
3. Open Terminal
4. Navigate to the folder:
   ```bash
   cd ~/Downloads/release
   ```
5. Make the script executable (first time only):
   ```bash
   chmod +x GeekSocial.sh
   ```
6. **Run the app:**
   ```bash
   ./GeekSocial.sh
   ```

### Requirements
- macOS 10.12 or later
- Java 11+ 

### Install Java on Mac
```bash
# Using Homebrew (easiest):
brew install openjdk@11

# Then add to PATH:
echo 'export PATH="/usr/local/opt/openjdk@11/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

Or download from: https://www.oracle.com/java/technologies/downloads/

### Alternative: Double-Click Method
1. Right-click `GeekSocial.sh`
2. Select "Open With" → "Other"
3. Navigate to Applications → Utilities → Terminal
4. Click "Always Open With"
5. Now you can double-click in future

### Troubleshooting
- **"Permission denied"**: Run `chmod +x GeekSocial.sh`
- **"java: command not found"**: Install Java (see above)
- **Check Java version**: Open Terminal and type: `java -version`

---

## 🐧 Linux Users

### Quick Start (Ubuntu/Debian)

1. Download `GeekSocial-universal.zip`
2. Extract the ZIP file
3. Open Terminal
4. Navigate to folder:
   ```bash
   cd ~/Downloads/release
   ```
5. Make script executable:
   ```bash
   chmod +x GeekSocial.sh
   ```
6. **Run the app:**
   ```bash
   ./GeekSocial.sh
   ```

### Install Java (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install openjdk-11-jre
```

### Install Java (Other Distros)
- **Fedora/RHEL**: `sudo dnf install java-11-openjdk`
- **Arch**: `sudo pacman -S jdk-openjdk`
- Manual: https://www.oracle.com/java/technologies/downloads/

### Desktop Integration (Optional)
Create a desktop launcher:

1. Create file: `~/.local/share/applications/geeksocial.desktop`
2. Add content:
   ```ini
   [Desktop Entry]
   Version=1.0
   Type=Application
   Name=GeekSocial
   Exec=/path/to/release/GeekSocial.sh
   Icon=applications-internet
   Categories=Network;
   ```
3. Save and close
4. GeekSocial will appear in your applications menu

### Troubleshooting
- **"Permission denied"**: Run `chmod +x GeekSocial.sh`
- **"java: command not found"**: Install Java above
- **Check Java**: `java -version`

---

## 🌍 For All Platforms

### What to Do First Time
1. **Install Java 11+** (if not already installed)
   - Windows: https://java.com
   - Mac: `brew install openjdk@11`
   - Linux: `sudo apt install openjdk-11-jre`

2. **Extract the ZIP file**
   - Windows: Right-click → Extract All
   - Mac/Linux: `unzip GeekSocial-universal.zip`

3. **Launch the app**
   - Windows: Double-click `release/GeekSocial.bat`
   - Mac/Linux: `./release/GeekSocial.sh` (from Terminal)

### First Launch
- App takes 5-10 seconds to start (Java startup time)
- Click "Online Mode" to connect to cloud server
- Create your account
- Start posting!

### Local vs Online Mode
- **Local**: Data saved on your computer only
- **Online**: Data syncs to cloud (access from any device)

---

## 🔧 Advanced: Run from Command Line

If you prefer the Terminal, you can always run directly:

```bash
# Requires being in the release folder
java -jar GeekSocial.jar
```

---

## 📚 Getting Help

- **GitHub**: https://github.com/jjjjohnson0628-byte/GeekSocial
- **Cloud Server**: https://geeksocial.onrender.com
- **Report Issues**: GitHub Issues page
- **Full Documentation**: Check README.md in the ZIP

---

## 🚀 System Requirements Summary

| OS | Version | Java | Disk Space |
|---|---|---|---|
| Windows | 7+ | 11+ | 100 MB |
| macOS | 10.12+ | 11+ | 100 MB |
| Linux | Any | 11+ | 100 MB |

---

## 💡 Tips

- Java download takes 2-5 minutes
- App extracts in seconds
- First launch takes 5-10 seconds
- All subsequent launches are faster
- No installation wizard needed
- All files in one folder = portable

**That's it! You're ready to use GeekSocial across all platforms.**
