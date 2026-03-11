#!/bin/bash
# ============================================================
# Run the Student Result & Attendance System
# ============================================================

JAR_NAME="StudentSystem.jar"
LIB_DIR="lib"
MAIN_CLASS="com.student.Main"

if [ ! -f "$JAR_NAME" ]; then
    echo "[!] JAR not found. Running build first..."
    bash build.sh
fi

echo ""
echo "Starting Student Result & Attendance System..."
echo ""

java -cp "$JAR_NAME:$LIB_DIR/postgresql.jar" "$MAIN_CLASS"
