# SocialApp - Social Media Platform

A cross-platform social media application built with Java Swing and JavaFX featuring live streaming, photo/video capture, messaging, and more.

## Features

### Core Features
- **User Authentication** - Secure login/signup with SHA256 password hashing
- **Live Streaming** - Real-time webcam broadcasts to other users
- **Photo & Video Capture** - Direct camera capture with instant save
- **Social Feed** - Display posts with media (photos, videos)

### Social Interactions
- **❤️ Likes** - Like/unlike posts with counters
- **💬 Comments** - Add and view comments on any post
- **💬 Direct Messaging** - Send private messages to other users
- **👥 Followers System** - Follow/unfollow other users

### User Management
- **Profile Customization** - Set display name and profile photo
- **User Directory** - View all users on the platform

## System Requirements

- **Java**: JDK 11 or higher (free download from oracle.com or adoptopenjdk.org)
- **Operating System**: Windows 7+, macOS 10.12+, or Linux
- **Webcam**: Optional (required for live streaming and camera capture)
- **Disk Space**: 500 MB minimum

## ⚡ Quick Start (Easiest!)

### Windows Users - 2 Simple Steps!

1. **Double-click**: `Install.bat`
   - Checks Java, creates folders, compiles code
   
2. **Double-click**: `QuickStart.bat`
   - Starts server + app automatically
   - Everything works together!

👉 **That's it! See INSTALLATION.txt for more details**

### Alternative Options

- **Option A**: Double-click `Launch.vbs` (app only, local mode)
- **Option B**: Run `SocialApp.bat` (command line)
- **Option C**: Manual server mode (advanced)

### macOS/Linux Users

```bash
bash run.sh
```

Or setup online mode (see ONLINE_MODE.txt):
```bash
bash StartServer.sh
```

### Need Help?
📖 **Read**: INSTALLATION.txt (step-by-step guide for beginners)

## Building from Source

### Windows
```
Build.bat
```

### macOS/Linux
```bash
bash build.sh
```

## File Structure

```
SocialApp/
├── SocialApp.java              # Main source code (1700+ lines)
├── SocialApp.bat               # Windows launcher script
├── Launch.vbs                  # Double-click launcher (Windows)
├── build.sh                    # Build script (macOS/Linux)
├── run.sh                      # Run script (macOS/Linux)
├── Build.bat                   # Build script (Windows)
├── manifest.txt                # JAR manifest
├── GeekSocial.jar             # Compiled application
├── javafx-sdk-25.0.2/         # JavaFX library
├── *.jar                       # Dependencies (GSON, Webcam, NIO, SLF4J)
├── data/                       # User and post data storage
│   ├── users.json
│   ├── posts.json
│   └── messages.json
└── media/                      # User-generated content
    ├── photos/
    └── videos/
```

## How to Use

### First Time Setup
1. Launch the app (double-click Launch.vbs or run SocialApp.bat)
2. Click "Sign Up" to create an account
3. Log in with your new credentials

### Creating a Post
1. Click on the feed
2. Enter your post text in the text area
3. Optionally click "Add Media", "📷 Take Photo", or "🎥 Record Video"
4. Click "Post" to share

### Going Live
1. Click "Go Live" button
2. Your webcam stream will start broadcasting
3. Other users can watch your stream by clicking your name in the live users list
4. Click "Stop Live" to end the broadcast (video saves as a post)

### Interacting with Posts
- **Like**: Click the ❤ button on any post
- **Comment**: Click the 💬 button and add your comment
- **Message**: Click "Messages" button in navigation to send direct messages

### Managing Relationships
- Click "👥 Followers" to see your followers and people you follow
- Click on any user's posts to view their profile content

## Sharing the App

### Package for Distribution

To share this app with others:

1. **Copy the entire folder** - All dependencies and data are included
2. **No installation needed** - Just ensure Java is installed on recipient's computer
3. **Share via:**
   - USB flash drive
   - Cloud storage (Google Drive, Dropbox, OneDrive)
   - Email as zip (if < 25MB)
   - Git repository

### Creating a Portable ZIP

Windows (Command Line):
```bat
tar -c -f SocialApp.zip SocialApp/
```

Or use 7-Zip/WinRAR to compress the folder

macOS/Linux:
```bash
zip -r SocialApp.zip SocialApp/
```

### System for Installation on Target Machine

1. Extract the ZIP file
2. Ensure Java is installed (check with: `java -version`)
3. For Windows: Double-click **Launch.vbs**
4. For macOS/Linux: Run `bash run.sh`

## Keyboard Shortcuts

- None configured (all interactions via mouse/buttons)

## Data Storage

- **User Data**: `data/users.json` - All user accounts and profiles
- **Posts**: `data/posts.json` - All posts, likes, and comments
- **Messages**: `data/messages.json` - Direct message history
- **Media**: `media/photos/` and `media/videos/` - User uploads

All data is stored locally on each machine (not synchronized across instances).

## Troubleshooting

### "Java is not installed"
- Download Java: https://www.oracle.com/java/technologies/downloads/
- Or use OpenJDK: https://adoptopenjdk.net/

### "Webcam not working"
- Check if another app is using the webcam
- Grant permission if prompted by OS
- Try rebooting the computer
- Test with another app first (e.g., Zoom, Skype)

### "JAR file not found"
- Run `Build.bat` (Windows) or `bash build.sh` (macOS/Linux) first
- This compiles the source code into GeekSocial.jar

### App runs but looks strange
- This is usually just theme loading - give it a moment
- Try resizing the window

### Data not saving
- Check that `data/` folder exists (created automatically)
- Ensure write permissions for the folder
- Try running as Administrator

## Dependencies

**Included Locally:**
- GSON 2.10.1 - JSON serialization
- Webcam Capture 0.3.12 - Camera access
- JNA 5.13.0 - Native API bindings
- BridJ 0.7.0 - Native library wrapper
- SLF4J 2.0.5 - Logging framework
- JavaFX 25.0.2 - UI framework

**System Requirements:**
- Java 11+ (JDK or JRE)

## Limitations

- Single-machine instance (no actual server networking)
- Live streams between multiple instances requires manual user coordination
- All data stored locally (no cloud sync)
- Webcam access requires local hardware

## Future Enhancements

- Multi-machine networking with WebSocket server
- Cloud storage integration
- Mobile app version
- Enhanced encryption for messages
- User search and discovery
- Post hashtags and trending
- User notifications system
- Video encoding to multiple formats

## License

Free to use and modify for personal/educational projects.

## Contact & Support

For issues or questions:
1. Check the Troubleshooting section above
2. Ensure Java is properly installed
3. Review the Build.bat or build.sh scripts for errors

## About

Built with passion using:
- Java Swing (GUI)
- JavaFX (Media playback)
- Webcam Capture (Live streaming)
- GSON (Data persistence)

Enjoy your social media platform!
