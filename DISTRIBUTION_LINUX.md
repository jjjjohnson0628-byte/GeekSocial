# GeekSocial - Linux Distribution Guide

## 📦 Package for Linux Users

**File:** `GeekSocial-linux.zip` (universal application)

### What Linux Users Get
```
GeekSocial-linux.zip
├── INSTALL_CROSS_PLATFORM.md
└── release/
    ├── GeekSocial.sh       ← Run in Terminal
    ├── GeekSocial.jar      ← Main app
    └── README.md
```

---

## 🚀 Installation for Linux Users

### Step 1: Download & Extract
```bash
# Download
wget https://github.com/jjjjohnson0628-byte/GeekSocial/releases/download/v1.0-universal/GeekSocial-linux.zip

# Or on Mac/Debian with built-in unzip
unzip GeekSocial-linux.zip

# Or Extract in file manager
```

### Step 2: Install Java

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install openjdk-11-jre
java -version  # Verify
```

**Fedora/RHEL/CentOS:**
```bash
sudo dnf install java-11-openjdk
java -version
```

**Arch:**
```bash
sudo pacman -S jdk-openjdk
java -version
```

**Generic (any distro):**
Download from: https://www.oracle.com/java/technologies/downloads/

### Step 3: Make Executable (First Time)
```bash
cd ~/Downloads/release
chmod +x GeekSocial.sh
```

### Step 4: Run the App
```bash
./GeekSocial.sh
```

---

## 📦 Linux Package Distribution

### 1. GitHub Releases ⭐ (Easiest)
Users download and run:
```bash
unzip GeekSocial-linux.zip
cd release
chmod +x GeekSocial.sh
./GeekSocial.sh
```

### 2. AUR (Arch Linux)
Create PKGBUILD for Arch User Repository:
```bash
pkgname=geeksocial
pkgver=1.0
pkgrel=1
depends=('java-runtime-openjdk')
url="https://github.com/jjjjohnson0628-byte/GeekSocial"
source=("https://github.com/jjjjohnson0628-byte/GeekSocial/releases/download/v1.0-universal/GeekSocial.jar")

build() {
  true  # No build needed for compiled JAR
}

package() {
  install -Dm755 "$srcdir"/GeekSocial.jar "$pkgdir"/usr/share/geeksocial/GeekSocial.jar
}
```

Users: `yay -S geeksocial`

### 3. Snap Package
```yaml
name: geeksocial
version: 1.0
summary: Lightweight Java social networking app
description: |
  GeekSocial is a cross-platform social network application
  built with Java and JavaFX.

grade: stable
confinement: strict

apps:
  geeksocial:
    command: bin/geeksocial
    plugs: [home, network, network-bind]

parts:
  geeksocial:
    plugin: dump
    source: https://github.com/.../releases/download/v1.0-universal/GeekSocial.jar
    stage-packages:
      - openjdk-11-jre
```

Users: `snap install geeksocial`

### 4. AppImage
Distribute as self-contained `.AppImage` file (advanced):
```bash
# Users download .AppImage and run:
chmod +x GeekSocial.AppImage
./GeekSocial.AppImage
```

---

## 🖥️ Desktop Integration

### Automatic (File Manager)

Create: `~/.local/share/applications/geeksocial.desktop`

```ini
[Desktop Entry]
Version=1.0
Type=Application
Name=GeekSocial
Comment=Social networking application
Exec=/path/to/release/GeekSocial.sh
Icon=application-x-java
Categories=Network;Communication;
Keywords=social;network;java;
Terminal=false
```

Then app appears in applications menu.

### Alternative: GNOME Integration
```bash
# Create launcher
gsettings set org.gnome.desktop.app-folders folder-children "['geeksocial']"
```

---

## 📊 Linux Distribution Priority

**Tier 1 (Ready Now):**
- ✅ GitHub Releases (ZIP file)

**Tier 2 (Easy, Next Version):**
- ⏳ AUR for Arch users
- ⏳ Snap package (universal)

**Tier 3 (Professional, v2.0+):**
- ⏳ AppImage (universal binary)
- ⏳ Flatpak (sandboxed)
- ⏳ DEB/RPM packages (distro-specific)

---

## 🐛 Linux Troubleshooting

### "Permission denied"
```bash
chmod +x ~/Downloads/release/GeekSocial.sh
./GeekSocial.sh
```

### "java: command not found"
```bash
# Ubuntu/Debian:
sudo apt install openjdk-11-jre

# Fedora:
sudo dnf install java-11-openjdk

# Check:
java -version
```

### "Cannot execute binary file"
```bash
# Verify it's executable:
ls -la GeekSocial.sh

# If not, fix:
chmod +x GeekSocial.sh
```

### Display Issues on Wayland
```bash
# Force X11:
export GDK_BACKEND=x11
./GeekSocial.sh
```

---

## 📋 System Requirements

| Requirement | Minimum |
|---|---|
| OS | Any Linux distro |
| Architecture | x86_64, ARM64, or other Java-supported |
| Java | 11+ (OpenJDK recommended) |
| RAM | 256 MB |
| Disk | 100 MB |
| GUI | Any (X11, Wayland, etc.) |

---

## 🔧 Advanced: Build from Source

Linux users who prefer source:
```bash
git clone https://github.com/jjjjohnson0628-byte/GeekSocial
cd GeekSocial
javac -cp "lib/*" *.java
java -cp ".:lib/*" SocialApp
```

---

## 📡 CI/CD Integration

For automatic releases on GitHub:

```yaml
# .github/workflows/release.yml
name: Release

on:
  release:
    types: [created]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Build
        run: javac -cp "lib/*" *.java && jar cfm GeekSocial.jar MANIFEST.MF *.class
      - name: Upload
        run: |
          wget https://github.com/cli/cli/releases/download/v1.13.1/gh_1.13.1_linux_amd64.tar.gz
          tar xzf gh_1.13.1_linux_amd64.tar.gz
          ./gh/bin/gh release upload ${{ github.event.release.tag_name }} GeekSocial.jar
```

---

## 📞 Support Channels

- **Terminal Help**: `./GeekSocial.sh --help`
- **GitHub Issues**: Report Linux-specific problems
- **Communities**: r/linux, linux-users groups
- **Java Support**: https://openjdk.java.net/

---

## ✅ Checklist for Linux Distribution

- [x] Universal ZIP file created
- [x] Installation guide written
- [x] Launcher script included
- [x] Desktop integration supported
- [x] Multiple package formats documented
- [x] Troubleshooting guide provided

**Linux users are fully supported!**

---

## Next Steps

1. Upload `GeekSocial-linux.zip` to GitHub Releases
2. Share with Linux communities (Reddit, blogs, etc.)
3. Monitor feedback
4. Create snap/AppImage for v2.0
5. Submit AUR package

Your app now supports Windows, macOS, and Linux! 🎉
