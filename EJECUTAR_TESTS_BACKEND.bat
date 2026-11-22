@echo off
title DSY1105 - Tests Backend

echo ==========================================
echo        DSY1105 - TESTS BACKEND
echo ==========================================
echo.

cd /d "%~dp0backend"

echo Buscando Java 21...
echo.

set "JAVA_HOME="

if exist "%USERPROFILE%\.jdks\ms-21.0.10\bin\java.exe" (
    set "JAVA_HOME=%USERPROFILE%\.jdks\ms-21.0.10"
)

if not defined JAVA_HOME (
    for /d %%D in ("%USERPROFILE%\.jdks\*21*") do (
        if exist "%%~fD\bin\java.exe" set "JAVA_HOME=%%~fD"
    )
)

if not defined JAVA_HOME (
    for /d %%D in ("%ProgramFiles%\Microsoft\jdk-21*") do (
        if exist "%%~fD\bin\java.exe" set "JAVA_HOME=%%~fD"
    )
)

if not defined JAVA_HOME (
    for /d %%D in ("%ProgramFiles%\Java\jdk-21*") do (
        if exist "%%~fD\bin\java.exe" set "JAVA_HOME=%%~fD"
    )
)

if not defined JAVA_HOME (
    echo ERROR: No se encontro Java 21.
    echo.
    pause
    exit /b 1
)

set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Java utilizado:
java -version

echo.
echo Ejecutando tests del backend...
echo.

call mvnw.cmd clean test

echo.
echo ==========================================
echo        FIN DE LOS TESTS
echo ==========================================
echo.
pause