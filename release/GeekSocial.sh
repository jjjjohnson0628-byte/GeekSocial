#!/bin/bash
# GeekSocial Application Launcher
# Works on macOS and Linux

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# Check if JAR exists
if [ ! -f "GeekSocial.jar" ]; then
    echo "Error: GeekSocial.jar not found!"
    echo "Expected at: $SCRIPT_DIR/GeekSocial.jar"
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed!"
    echo ""
    echo "Please install Java 11 or higher:"
    echo "  macOS: brew install openjdk@11"
    echo "  Linux: sudo apt install openjdk-11-jre (Ubuntu/Debian)"
    echo ""
    echo "Or download from: https://www.oracle.com/java/technologies/downloads/"
    exit 1
fi

# Get Java version
JAVA_VERSION=$(java -version 2>&1 | head -1)
echo "Using: $JAVA_VERSION"
echo ""

# Optional: Set the server mode
# export SOCIAL_APP_SERVER=https://geeksocial.onrender.com

# Run the application
java -jar GeekSocial.jar

if [ $? -ne 0 ]; then
    echo ""
    echo "An error occurred while running GeekSocial"
    echo "Please ensure Java 11+ is installed"
    exit 1
fi
