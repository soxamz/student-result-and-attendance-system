#!/bin/bash
# ============================================================
# Student Result & Attendance System - Build & Run Script
# ============================================================

set -e

LIB_DIR="lib"
SRC_DIR="src/main/java"
BUILD_DIR="build/classes"
JAR_NAME="StudentSystem.jar"
MAIN_CLASS="com.student.Main"
PG_JAR_URL="https://jdbc.postgresql.org/download/postgresql-42.7.3.jar"
DOTENV_JAR_URL="https://repo1.maven.org/maven2/io/github/cdimascio/dotenv-java/3.0.0/dotenv-java-3.0.0.jar"

echo "=============================================="
echo " Student Result & Attendance System Builder"
echo "=============================================="

# Create dirs
mkdir -p "$LIB_DIR" "$BUILD_DIR"

# Download PostgreSQL JDBC driver if missing
if [ ! -f "$LIB_DIR/postgresql.jar" ]; then
    echo "[*] Downloading PostgreSQL JDBC driver..."
    if command -v curl &>/dev/null; then
        curl -L "$PG_JAR_URL" -o "$LIB_DIR/postgresql.jar"
    elif command -v wget &>/dev/null; then
        wget -q "$PG_JAR_URL" -O "$LIB_DIR/postgresql.jar"
    else
        echo "[ERROR] Neither curl nor wget found. Please manually download:"
        echo "  $PG_JAR_URL"
        echo "  -> Place it at: $LIB_DIR/postgresql.jar"
        exit 1
    fi
    echo "[✔] PostgreSQL driver downloaded."
fi

# Build classpath
CP="$LIB_DIR/postgresql.jar"

# Collect all .java source files
echo "[*] Compiling Java sources..."
find "$SRC_DIR" -name "*.java" > /tmp/sources.txt

# Compile
javac -cp "$CP" -d "$BUILD_DIR" @/tmp/sources.txt
echo "[✔] Compilation successful."

# Create manifest
MANIFEST_FILE="$BUILD_DIR/MANIFEST.MF"
echo "Main-Class: $MAIN_CLASS" > "$MANIFEST_FILE"
echo "Class-Path: lib/postgresql.jar" >> "$MANIFEST_FILE"

# Package JAR
echo "[*] Packaging JAR..."
jar cfm "$JAR_NAME" "$MANIFEST_FILE" -C "$BUILD_DIR" .
echo "[✔] JAR created: $JAR_NAME"

echo ""
echo "=============================================="
echo " Build Complete! To run the application:"
echo "   java -cp \"$JAR_NAME:$LIB_DIR/postgresql.jar\" $MAIN_CLASS"
echo ""
echo " Or use the run.sh script."
echo "=============================================="
