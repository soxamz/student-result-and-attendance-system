@echo off
REM ============================================================
REM Run the Student Result & Attendance System (Windows)
REM ============================================================

set JAR_NAME=StudentSystem.jar
set LIB_DIR=lib
set MAIN_CLASS=com.student.Main

if not exist %JAR_NAME% (
    echo [!] JAR not found. Please run build.bat first.
    pause
    exit /b 1
)

echo.
echo Starting Student Result and Attendance System...
echo.

java -cp "%JAR_NAME%;%LIB_DIR%\postgresql.jar" %MAIN_CLASS%
pause
