@echo off
cd /d "%~dp0"

echo Starting SMA Backend...

REM Download dependencies if needed
echo Downloading dependencies...
call mvn dependency:go-offline -DskipTests

REM Start the application
echo Starting Spring Boot application...
call mvn spring-boot:run

pause
