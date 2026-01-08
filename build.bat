@echo off
REM Catering Platform Build Script
REM This script compiles the project with detailed error output

echo ========================================
echo Catering Platform - Build Script
echo ========================================
echo.

REM Check Java version
echo [1/5] Checking Java version...
java -version 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 and add it to PATH
    pause
    exit /b 1
)
echo.

REM Check Maven version
echo [2/5] Checking Maven version...
call mvn -version 2>&1
if errorlevel 1 (
    echo ERROR: Maven is not installed or not in PATH
    echo Using Maven Wrapper instead...
    set MAVEN_CMD=mvnw.cmd
) else (
    set MAVEN_CMD=mvn
)
echo.

REM Clean previous build
echo [3/5] Cleaning previous build...
call %MAVEN_CMD% clean
if errorlevel 1 (
    echo ERROR: Clean failed
    pause
    exit /b 1
)
echo.

REM Compile project
echo [4/5] Compiling project...
echo This may take a few minutes...
call %MAVEN_CMD% compile -e -X > build.log 2>&1
if errorlevel 1 (
    echo.
    echo ========================================
    echo BUILD FAILED
    echo ========================================
    echo.
    echo Check build.log for detailed error information
    echo.
    echo Common issues:
    echo - Java version mismatch (needs Java 17+)
    echo - Maven compiler plugin issues
    echo - Dependency conflicts
    echo.
    echo Showing last 50 lines of build.log:
    echo ----------------------------------------
    powershell -Command "Get-Content build.log | Select-Object -Last 50"
    echo ----------------------------------------
    pause
    exit /b 1
)
echo.

REM Success
echo [5/5] Build completed successfully!
echo.
echo ========================================
echo BUILD SUCCESS
echo ========================================
echo.
echo Next steps:
echo - Run: mvn spring-boot:run
echo - Or:  java -jar target/catering-platform-1.0.0.jar
echo.
pause

