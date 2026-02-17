# GeekSocial - macOS Distribution Guide

## 📦 Package for Mac Users

**File:** `GeekSocial-mac.zip` (contains universal app)

### What Mac Users Get
```
GeekSocial-mac.zip
├── INSTALL_CROSS_PLATFORM.md
└── release/
    ├── GeekSocial.sh        ← Click or run in Terminal
    ├── GeekSocial.bash      ← Alternative launcher
    ├── GeekSocial.jar       ← Main app
    └── README.md
```

---

## 🚀 Installation for Mac Users

### Step 1: Download & Extract
1. Download `GeekSocial-mac.zip`
2. Finder will auto-extract (or double-click)
3. You'll see a `release` folder

### Step 2: Ensure Java is Installed
Open Terminal and check:
```bash
java -version
```

If not installed:
```bash
# Using Homebrew (recommended):
brew install openjdk@11

# Then:
echo 'export PATH="/usr/local/opt/openjdk@11/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

Or download from: https://www.oracle.com/java/technologies/downloads/

### Step 3: Make Executable (First Time)
```bash
cd ~/Downloads/release
chmod +x GeekSocial.sh
```

### Step 4: Run the App
Either:
- **Terminal method:**
  ```bash
  ./GeekSocial.sh
  ```
- **Finder method:**
  - Right-click `GeekSocial.sh`
  - Select "Open"
  - Click "Open" when security prompt appears

---

## 🛠️ Homebrew Installation (Advanced)

For Mac users who prefer Homebrew:

**Create a Homebrew formula** (developer task):
```ruby
# geeksocial.rb
class Geeksocial < Formula
  desc "Lightweight Java social networking application"
  homepage "https://github.com/jjjjohnson0628-byte/GeekSocial"
  url "https://github.com/jjjjohnson0628-byte/GeekSocial/releases/download/v1.0-universal/GeekSocial.jar"
  sha256 "..."
  depends_on "openjdk@11"
  
  def install
    libexec.install "GeekSocial.jar"
    bin.write_jar_wrapper libexec/"GeekSocial.jar", "geeksocial"
  end
end
```

Users could then install with:
```bash
brew install geeksocial
geeksocial
```

---

## 📊 Mac Distribution Channels

### 1. GitHub Releases ⭐ (Easiest)
- Upload `GeekSocial-mac.zip`
- Mac users download directly
- Tag: `v1.0-universal`

### 2. MacPorts/Homebrew
- Create formula
- Submit to repository
- Users: `brew install geeksocial`

### 3. Direct Link
- Share ZIP link
- Include Java install instructions

### 4. MacApp Store
- Requires Apple Developer Account
- Complex process (not recommended for early versions)

---

## 🐛 Mac-Specific Troubleshooting

### "Cannot open GeekSocial.sh"
**Solution:** Right-click → Open (not double-click)

### "Permission denied"
```bash
chmod +x ~/Downloads/release/GeekSocial.sh
```

### "java: command not found"
```bash
# Install Java:
brew install openjdk@11

# Add to PATH:
echo 'export PATH="/usr/local/opt/openjdk@11/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

### "GateKeeper" warning
- Click "Open" when prompted
- Or: `sudo spctl --add ~/Downloads/GeekSocial.sh`

---

## 📋 System Requirements

- macOS 10.12 (Sierra) or later
- Java 11+ (free download)
- 100 MB disk space
- Intel or Apple Silicon (M1/M2/M3)

---

## 🎓 For Mac Users: Converting to App

Advanced users can create a `.app` bundle:

```bash
#!/bin/bash
APP_NAME="GeekSocial"
APP_PATH="$HOME/Applications/$APP_NAME.app/Contents/MacOS"

mkdir -p "$APP_PATH"
cp GeekSocial.sh "$APP_PATH/$APP_NAME"
chmod +x "$APP_PATH/$APP_NAME"
```

Then launch with: `open ~/Applications/GeekSocial.app`

---

## 📞 Support

- Terminal: `./release/GeekSocial.sh`
- GitHub Issues: Report Mac-specific problems
- Java Help: https://java.com/en/download/help/

---

## Next Steps

1. ✅ Create `GeekSocial-mac.zip`
2. ✅ Upload to GitHub Releases
3. ✅ Share with Mac users
4. ✅ Gather feedback
5. 📈 Consider Homebrew formula for v2.0

**Mac users are now supported!**
