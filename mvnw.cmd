@REM ----------------------------------------------------------------------------
@REM Simplified SmartUni Maven Wrapper for Beginners (v3)
@REM ----------------------------------------------------------------------------

@echo off
setlocal

set ERROR_CODE=0

@REM Try to find Java if JAVA_HOME is not set
if "%JAVA_HOME%" == "" (
    for /f "tokens=*" %%i in ('where java.exe 2^>nul') do (
        set "JAVA_EXE=%%i"
        goto FoundJava
    )
) else (
    set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
)

:FoundJava
if not exist "%JAVA_EXE%" (
    echo.
    echo ❌ Error: Java not found! 
    echo Please install JDK 17 and set JAVA_HOME.
    echo.
    set ERROR_CODE=1
    goto end
)

@REM Find the project base dir - force absolute path
set "MAVEN_PROJECTBASEDIR=%~dp0"
set "CLASSPATH=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"
set "WRP_CLASS=org.apache.maven.wrapper.MavenWrapperMain"

@REM Download wrapper jar if missing
if not exist "%CLASSPATH%" (
    echo 📥 Downloading Maven Wrapper jar...
    powershell -Command "New-Item -ItemType Directory -Force -Path '%MAVEN_PROJECTBASEDIR%.mvn\wrapper'; (New-Object System.Net.WebClient).DownloadFile('https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar', '%CLASSPATH%')"
)

@REM Run Maven with multiModuleProjectDirectory set (required by Maven 3.3.1+)
"%JAVA_EXE%" %MAVEN_OPTS% -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%." -classpath "%CLASSPATH%" %WRP_CLASS% %*
if ERRORLEVEL 1 set ERROR_CODE=1

:end
exit /B %ERROR_CODE%
