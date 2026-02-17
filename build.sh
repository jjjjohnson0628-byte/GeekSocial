#!/bin/bash
# Build script for SocialApp (macOS/Linux)

JFX_LIB="javafx-sdk-25.0.2/lib"

javac \
  --module-path "$JFX_LIB" \
  --add-modules javafx.controls,javafx.media,javafx.swing \
  -cp ".:gson-2.10.1.jar:webcam-capture-0.3.12.jar:jna-5.13.0.jar:bridj-0.7.0.jar:slf4j-api-2.0.5.jar:slf4j-nop-2.0.5.jar" \
  SocialApp.java

jar cfm GeekSocial.jar manifest.txt *.class data/users.json data/posts.json 2>/dev/null || jar cfm GeekSocial.jar manifest.txt *.class

echo "Build complete!"
