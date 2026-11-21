@echo off
title DSY1105 - Conexion telefono
echo ==========================================
echo   DSY1105 - CONEXION TELEFONO AL BACKEND
echo ==========================================
echo.

set "ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"

if not exist "%ADB%" (
    echo No se encontro ADB en la ruta habitual.
    echo Intentando usar ADB desde PATH...
    set "ADB=adb"
)

echo Buscando telefono conectado por USB...
"%ADB%" start-server >nul 2>&1

echo.
"%ADB%" devices
echo.

echo Configurando acceso al backend por puerto 8080...
"%ADB%" -d reverse tcp:8080 tcp:8080

if errorlevel 1 (
    echo.
    echo ERROR: No se pudo conectar el telefono.
    echo Revisa:
    echo - Cable USB conectado
    echo - Depuracion USB activada
    echo - Permiso de depuracion aceptado en el telefono
    echo.
    pause
    exit /b 1
)

echo.
echo Verificando conexion...
"%ADB%" -d reverse --list

echo.
echo ==========================================
echo   TELEFONO CONECTADO CORRECTAMENTE
echo ==========================================
echo.
echo Ya puedes ejecutar la app desde Android Studio.
echo El backend debe estar funcionando en el puerto 8080.
echo.
pause