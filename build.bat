@echo off
REM ============================================================
REM Build Script for Student Result & Attendance System (Windows)
REM ============================================================

set LIB_DIR=lib
set SRC_DIR=src\main\java
set BUILD_DIR=build\classes
set JAR_NAME=StudentSystem.jar
set MAIN_CLASS=com.student.Main

echo ==============================================
echo  Student Result and Attendance System Builder
echo ==============================================

if not exist %LIB_DIR% mkdir %LIB_DIR%
if not exist %BUILD_DIR% mkdir %BUILD_DIR%

if not exist %LIB_DIR%\postgresql.jar (
    echo [!] postgresql.jar not found in lib\
    echo     Please download from:
    echo     https://jdbc.postgresql.org/download/postgresql-42.7.3.jar
    echo     and place it in the lib\ folder.
    pause
    exit /b 1
)

echo [*] Compiling Java sources...
dir /s /b %SRC_DIR%\*.java > sources.txt
javac -cp "%LIB_DIR%\postgresql.jar" -d %BUILD_DIR% @sources.txt

if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed.
    pause
    exit /b 1
)
echo [OK] Compilation successful.

echo Main-Class: %MAIN_CLASS% > %BUILD_DIR%\MANIFEST.MF
echo Class-Path: lib/postgresql.jar >> %BUILD_DIR%\MANIFEST.MF

echo [*] Packaging JAR...
jar cfm %JAR_NAME% %BUILD_DIR%\MANIFEST.MF -C %BUILD_DIR% .
echo [OK] JAR created: %JAR_NAME%

echo.
echo ==============================================
echo  Build Complete! Run: run.bat
echo ==============================================
pause
