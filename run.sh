#!/bin/bash
# Run script for SocialApp (macOS/Linux)

JFX_LIB="javafx-sdk-25.0.2/lib"
JAR_FILE="GeekSocial.jar"
CLASSPATH="$JAR_FILE:gson-2.10.1.jar:webcam-capture-0.3.12.jar:jna-5.13.0.jar:bridj-0.7.0.jar:slf4j-api-2.0.5.jar:slf4j-nop-2.0.5.jar"

java --module-path "$JFX_LIB" --add-modules javafx.controls,javafx.media,javafx.swing -cp "$CLASSPATH" SocialApp
