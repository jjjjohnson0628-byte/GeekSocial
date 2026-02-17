#!/bin/bash

# Start the SocialApp Server
# This enables online interaction between multiple instances

echo "============================================================"
echo "SocialApp Server Startup"
echo "============================================================"
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    echo "Please install Java JDK 11 or higher"
    echo "Download from: https://www.oracle.com/java/technologies/downloads/"
    exit 1
fi

echo "Compiling SocialAppServer.java..."
javac -cp "gson-2.10.1.jar" SocialAppServer.java
if [ $? -ne 0 ]; then
    echo "ERROR: Compilation failed"
    exit 1
fi

echo ""
echo "Starting server on http://127.0.0.1:8080..."
echo "Press Ctrl+C to stop the server"
echo ""

java -cp ".:gson-2.10.1.jar" SocialAppServer
