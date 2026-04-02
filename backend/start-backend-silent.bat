@echo off
cd /d "%~dp0"
netstat -ano | findstr ":8080" >nul
if %errorlevel% equ 0 (
    echo SMA Backend is already running on port 8080
    timeout /t 2
    exit /b
)
start "SMA Backend" cmd /c "cd /d %~dp0 && apache-maven-3.9.6\bin\mvn.cmd spring-boot:run"
echo Starting SMA Backend...
timeout /t 5 /nobreak >nul
netstat -ano | findstr ":8080" >nul
if %errorlevel% equ 0 (
    echo SMA Backend started successfully on http://localhost:8080
)
timeout /t 3
