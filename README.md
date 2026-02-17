# 🌟 GeekSocial - Cloud-Based Social Media Platform

A modern, cloud-powered social networking application with posts, messaging, live streaming, and more!

**Status:** ✅ Fully Functional | 🌐 Cloud Deployed | 📦 Windows Installer Ready

---

## 🚀 Quick Start (Choose Your Option)

### 👨‍💻 For End Users - Windows Installer (Easiest!)

1. Download the installer from [GitHub Releases](https://github.com/jjjjohnson0628-byte/GeekSocial/releases)
2. Run `GeekSocial-1.0-installer.exe`
3. Follow the wizard
4. Launch from Start Menu or Desktop shortcut

**No Java installation needed!** Everything is included.

---

### 👨‍💼 For Developers - Build from Source

```powershell
# Clone the repository
git clone https://github.com/jjjjohnson0628-byte/GeekSocial.git
cd GeekSocial

# Run the app (cloud mode)
$env:SOCIAL_APP_SERVER = "https://geeksocial.onrender.com"
java -cp ".;lib/*;javafx-sdk-25.0.2/lib/*" SocialApp

# Or build the Windows installer yourself
.\build-jar.bat
.\build-installer.bat
```

---

## ✨ Features

### Social Features
- **Posts** - Share text, photos, and videos
- **Likes** - React to posts (❤️ counter)
- **Comments** - Discuss on any post
- **Messaging** - Direct messages between users
- **Followers** - Build your network
- **Profiles** - Customize your profile with display name and photo

### Media & Streaming
- **Live Streaming** - Go live with your webcam in real-time
- **Photo Capture** - Take photos directly from your webcam
- **Video Recording** - Record videos from your camera
- **Media Upload** - Share images and videos in posts

### Smart Features
- **Cloud Sync** - All data synced across devices
- **Multi-Device** - Login from multiple computers
- **Online/Offline Mode** - Works both connected and local
- **Auto-Cloud Connection** - Seamless cloud server linking

---

## 🎯 First Steps (New Users)

1. **Create Account** - Click "Sign Up" on login screen
   - Username & password (choose something memorable!)
   
2. **Enable Cloud Mode** - Menu → Mode → "Enable Online Mode"
   - Status will show: "⚡ Online Mode (Server Connected)"
   
3. **Create Your First Post**
   - Type a message (optional: add photo/video)
   - Click "Post"
   
4. **Connect with Others**
   - Use "👥 Followers" to find and follow users
   - Message friends with "💬 Messages"
   - Go live with "Go Live" button

---

## 🌐 Cloud Architecture

### Server
- **Cloud Server:** https://geeksocial.onrender.com
- **Location:** Render.com (free tier)
- **Auto-Deploy:** Updates push to cloud instantly
- **Data:** Real-time sync across all clients

### Client
- **Desktop App:** Windows/Mac/Linux
- **Local Storage:** Offline capability
- **Smart Switching:** Auto-connect to cloud when available

---

## 💻 System Requirements

### Windows Installer
- Windows 7 or later (Vista not supported)
- 300 MB free disk space
- Internet connection (optional for offline mode)
- **Java**: Included in installer

### From Source
- Java 17+ (download: https://adoptopenjdk.net)
- JavaFX SDK 25.0.2 (included in `javafx-sdk-25.0.2/` folder)
- Windows/Mac/Linux OS
- Git (for cloning repository)

### Optional
- Webcam (for live streaming, photo/video capture)
- Microphone (for voice in live streams)

---

## 📖 Documentation

| Document | Purpose |
|----------|---------|
| **[INSTALLER_BUILD_GUIDE.md](INSTALLER_BUILD_GUIDE.md)** | Build your own Windows installer |
| **[QUICK_START.txt](QUICK_START.txt)** | Quick reference guide |
| **[CLOUD_SERVER_CONFIG.txt](CLOUD_SERVER_CONFIG.txt)** | Configure cloud server connection |
| **[RENDER_DEPLOYMENT.txt](RENDER_DEPLOYMENT.txt)** | Deploy your own server instance |

---

## 🔐 Security & Privacy

- **Passwords:** Hashed with SHA-256 (never stored in plain text)
- **Encryption:** All cloud communication uses HTTPS
- **Open Source:** Full source code available on GitHub
- **No Tracking:** Your data stays within the app and cloud
- **Local Control:** Can run completely offline without cloud

---

## 🛠️ Troubleshooting

### "Server not available"
- ✅ Check internet connection
- ✅ Verify cloud server is running: https://geeksocial.onrender.com
- ✅ Cloud might be starting up (free tier = slow start)
- ✅ Try disabling "Online Mode" for offline operation

### App won't start
- ✅ Verify Java 17+ is installed: `java -version`
- ✅ Check that `lib/` folder exists with all JAR files
- ✅ Windows installer includes Java automatically

### Can't see friends' posts
- ✅ Make sure "Online Mode" is enabled
- ✅ Check both users are using same cloud server
- ✅ Try restarting the app

### Live streaming not working
- ✅ Check webcam is connected and not in use
- ✅ Grant camera permissions if prompted
- ✅ Test camera with Windows Camera app first

---

## 📦 Building & Distribution

### For Developers: Create Windows Installer

```powershell
# Step 1: Build the JAR
.\build-jar.bat

# Step 2: Create installer
.\build-installer.bat

# Step 3: Installer is ready in 'installer' folder
.\installer\GeekSocial-1.0-installer.exe
```

See **[INSTALLER_BUILD_GUIDE.md](INSTALLER_BUILD_GUIDE.md)** for detailed instructions.

### Distribute via GitHub Releases

1. Go to: https://github.com/jjjjohnson0628-byte/GeekSocial/releases
2. Create release (tag: `v1.0`)
3. Upload: `.exe` from installer folder
4. Users download and run!

---

## 🔗 Links

- **GitHub Repository:** https://github.com/jjjjohnson0628-byte/GeekSocial
- **Cloud Server:** https://geeksocial.onrender.com
- **Report Issues:** https://github.com/jjjjohnson0628-byte/GeekSocial/issues
- **Releases:** https://github.com/jjjjohnson0628-byte/GeekSocial/releases

---

## 📈 Development

The project uses:
- **Java 17** - Core application
- **Swing/JavaFX** - GUI
- **Gson** - JSON serialization
- **Render.com** - Cloud hosting
- **Git** - Version control

### Tech Stack
- Backend: Java HTTP Server (`com.sun.net.httpserver`)
- Frontend: Java Swing/JavaFX
- Data: JSON files (local) / Cloud API (remote)
- Media: Webcam capture, image/video save
- Communication: HTTPS REST API

---

## 🎓 Learning Resources

This project demonstrates:
- ✅ Client-server architecture
- ✅ Cloud deployment and DevOps
- ✅ REST API design
- ✅ JavaFX GUI development
- ✅ Real-time data synchronization
- ✅ Windows installer creation
- ✅ Multi-platform support

---

## 📄 License

This project is open source and available under the MIT License.

---

## 💬 Support

- **Questions?** Check the documentation folders
- **Found a bug?** Open an issue on GitHub
- **Feature request?** Create a discussion on GitHub
- **Want to contribute?** Fork and submit a pull request!

---

## 🎉 Have Fun!

Start sharing, connecting, and enjoying GeekSocial!

**Remember:** Cloud sync means your data follows you everywhere. Sign up once, use on any device! 🌍📱💻
