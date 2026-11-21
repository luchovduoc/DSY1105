@echo off
title DSY1105 - Backend Spring Boot

echo ==========================================
echo       DSY1105 - BACKEND SPRING BOOT
echo ==========================================
echo.

cd /d "%~dp0backend"

if not exist "src\main\resources\application-local.properties" (
    echo ERROR:
    echo Falta application-local.properties
    echo.
    echo Copia el archivo privado dentro de:
    echo backend\src\main\resources
    echo.
    pause
    exit /b 1
)

echo Buscando Java 21...
echo.

set "JAVA_HOME="

REM JDK usado actualmente en el PC de desarrollo
if exist "%USERPROFILE%\.jdks\ms-21.0.10\bin\java.exe" (
    set "JAVA_HOME=%USERPROFILE%\.jdks\ms-21.0.10"
)

REM Buscar cualquier JDK 21 instalado por Android Studio / JetBrains
if not defined JAVA_HOME (
    for /d %%D in ("%USERPROFILE%\.jdks\*21*") do (
        if exist "%%~fD\bin\java.exe" set "JAVA_HOME=%%~fD"
    )
)

REM Microsoft OpenJDK 21
if not defined JAVA_HOME (
    for /d %%D in ("%ProgramFiles%\Microsoft\jdk-21*") do (
        if exist "%%~fD\bin\java.exe" set "JAVA_HOME=%%~fD"
    )
)

REM Oracle / OpenJDK 21
if not defined JAVA_HOME (
    for /d %%D in ("%ProgramFiles%\Java\jdk-21*") do (
        if exist "%%~fD\bin\java.exe" set "JAVA_HOME=%%~fD"
    )
)

REM Eclipse Temurin / Adoptium 21
if not defined JAVA_HOME (
    for /d %%D in ("%ProgramFiles%\Eclipse Adoptium\jdk-21*") do (
        if exist "%%~fD\bin\java.exe" set "JAVA_HOME=%%~fD"
    )
)

if not defined JAVA_HOME (
    echo ==========================================
    echo ERROR: NO SE ENCONTRO JAVA 21
    echo ==========================================
    echo.
    echo Este backend requiere Java 21.
    echo No se utilizara automaticamente el Java
    echo incluido con Android Studio si es Java 25.
    echo.
    echo Instala o selecciona un JDK 21 y vuelve
    echo a ejecutar este archivo.
    echo.
    pause
    exit /b 1
)

set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Java encontrado:
echo %JAVA_HOME%
echo.
java -version

echo.
echo ==========================================
echo Iniciando backend...
echo ==========================================
echo.
echo Cuando aparezca:
echo Started BackendApplicationKt
echo.
echo el backend estara disponible en:
echo http://localhost:8080/eventos
echo.

call mvnw.cmd spring-boot:run

echo.
echo El backend se detuvo.
pause