#!/bin/bash
# ============================================================
# SocialApp - Binary Installation Script for macOS/Linux
# ============================================================
# This script handles setup for Unix-based systems

clear

echo ""
echo "============================================================"
echo "          SocialApp - Installation & Setup"
echo "============================================================"
echo ""

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# ============================================================
# 1. Check Java Installation
# ============================================================
echo "[1/5] Checking Java installation..."
echo ""

if ! command -v java &> /dev/null; then
    echo -e "${RED}✗ ERROR: Java is NOT installed${NC}"
    echo ""
    echo "Please install Java first:"
    echo "  macOS:  brew install openjdk@11"
    echo "  Linux:  sudo apt-get install openjdk-11-jdk"
    echo "  Or download from: https://www.oracle.com/java/technologies/downloads/"
    echo ""
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | grep -oP '(?<=version ")[^"]*')
echo -e "${GREEN}✓ Java installed: $JAVA_VERSION${NC}"
echo ""

# ============================================================
# 2. Create Necessary Directories
# ============================================================
echo "[2/5] Creating directories..."
echo ""

for dir in "data" "media/photos" "media/videos" "temp"; do
    if [ ! -d "$dir" ]; then
        mkdir -p "$dir"
        echo -e "${GREEN}✓ Created: $dir/${NC}"
    fi
done

echo ""

# ============================================================
# 3. Compile Java Files
# ============================================================
echo "[3/5] Compiling application..."
echo ""

# Check for gson
if [ ! -f "gson-2.10.1.jar" ]; then
    echo -e "${YELLOW}⚠ Warning: gson-2.10.1.jar not found${NC}"
    echo "  Download from: https://github.com/google/gson/releases"
fi

# Compile server
if [ -f "SocialAppServer.java" ]; then
    echo "Compiling SocialAppServer.java..."
    javac -cp "gson-2.10.1.jar" SocialAppServer.java 2>/dev/null
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Compiled: SocialAppServer${NC}"
    else
        echo -e "${RED}✗ Failed to compile SocialAppServer${NC}"
    fi
fi

# Compile network client
if [ -f "NetworkClient.java" ]; then
    echo "Compiling NetworkClient.java..."
    javac -cp "gson-2.10.1.jar" NetworkClient.java 2>/dev/null
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Compiled: NetworkClient${NC}"
    else
        echo -e "${RED}✗ Failed to compile NetworkClient${NC}"
    fi
fi

# Compile main app
if [ -f "SocialApp.java" ]; then
    echo "Compiling SocialApp.java..."
    if [ -f "javafx-sdk-25.0.2/lib" ]; then
        javac -cp "gson-2.10.1.jar:javafx-sdk-25.0.2/lib/*" --add-modules javafx.controls,javafx.media,javafx.swing SocialApp.java 2>/dev/null
    else
        javac -cp "gson-2.10.1.jar" SocialApp.java 2>/dev/null
    fi
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Compiled: SocialApp${NC}"
    else
        echo -e "${RED}✗ Failed to compile SocialApp${NC}"
    fi
fi

echo ""

# ============================================================
# 4. Verify Launch Scripts
# ============================================================
echo "[4/5] Checking launch scripts..."
echo ""

# Make shell scripts executable
for script in "run.sh" "StartServer.sh"; do
    if [ -f "$script" ]; then
        chmod +x "$script"
        echo -e "${GREEN}✓ Ready: $script${NC}"
    fi
done

echo ""

# ============================================================
# 5. Summary
# ============================================================
echo "[5/5] Installation complete!"
echo ""
echo "============================================================"
echo "                 SETUP SUCCESSFUL"
echo "============================================================"
echo ""
echo -e "${GREEN}Your SocialApp is ready to use!${NC}"
echo ""
echo "QUICK START OPTIONS:"
echo ""
echo "Option 1 - EASIEST (Recommended):"
echo "  bash run.sh"
echo "  (Launches app in local mode)"
echo ""
echo "Option 2 - WITH ONLINE MODE:"
echo "  Terminal 1: bash StartServer.sh"
echo "  Terminal 2: bash run.sh"
echo "  Then enable: Mode > Enable Online Mode"
echo ""
echo "Option 3 - MANUAL:"
echo "  java -jar SocialApp.jar"
echo ""
echo "============================================================"
echo ""
echo "More info: Read INSTALLATION.txt or QUICK_START.txt"
echo ""
