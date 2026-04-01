@REM Maven Wrapper startup script

@echo off
set MAVEN_PROJECTBASEDIR=%~dp0
set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_URL="https://repo.maven.apache.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar"

if exist %WRAPPER_JAR% (
    goto runMaven
) else (
    echo Downloading Maven Wrapper JAR...
    powershell -Command "(New-Object Net.WebClient).DownloadFile('%WRAPPER_URL%', '%WRAPPER_JAR%')"
)

:runMaven
"%JAVA_HOME%\bin\java" ^
    %MAVEN_OPTS% ^
    -classpath %WRAPPER_JAR% ^
    org.apache.maven.wrapper.MavenWrapperMain %*
