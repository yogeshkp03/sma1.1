@echo off
cd /d "%~dp0"
echo ========================================
echo   SMA Backend Launcher
echo ========================================
echo.
netstat -ano | findstr ":8080" >nul
if %errorlevel% equ 0 (
    echo WARNING: Port 8080 is already in use!
    set /p choice="Do you want to kill it and restart? (y/n): "
    if /i "%choice%"=="y" (
        for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do taskkill /F /PID %%a
        echo Port 8080 freed.
    ) else (
        echo Exiting.
        exit /b
    )
)
echo.
echo Starting SMA Backend on port 8080...
call "%~dp0apache-maven-3.9.6\bin\mvn.cmd" spring-boot:run
pause
