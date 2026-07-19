@echo off
setlocal EnableExtensions EnableDelayedExpansion

set "SCRIPT_DIR=%~dp0"
if "%SCRIPT_DIR:~-1%"=="\" set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"

if defined LOCALAPPDATA (
    set "JISOCREATOR_LOGS=%LOCALAPPDATA%\jisocreator\logs"
) else (
    set "JISOCREATOR_LOGS=%APPDATA%\jisocreator\logs"
)

set "JAVA_HOME=%SCRIPT_DIR%\jre"
set "JAVA_HOME_FROM_ARG="
set "APP_ARGS="

:parse_args
if "%~1"=="" goto args_done

if /I "%~1"=="--help" goto usage
if /I "%~1"=="-h" goto usage
if /I "%~1"=="/?" goto usage

if /I "%~1"=="--java-home" (
    if "%~2"=="" (
        echo [ERROR] Falta valor para --java-home
        goto usage_error
    )
    set "JAVA_HOME=%~2"
    set "JAVA_HOME_FROM_ARG=1"
    shift
    shift
    goto parse_args
)

if /I "%~1"=="--logs" (
    if "%~2"=="" (
        echo [ERROR] Falta valor para --logs
        goto usage_error
    )
    set "JISOCREATOR_LOGS=%~2"
    shift
    shift
    goto parse_args
)

set "APP_ARGS=!APP_ARGS! "%~1""
shift
goto parse_args

:args_done
if not defined JAVA_HOME_FROM_ARG if not exist "%JAVA_HOME%\bin\javaw.exe" set "JAVA_HOME=%ProgramFiles%\jisocreator\jre"

if not exist "%JAVA_HOME%\bin\javaw.exe" (
    echo [ERROR] No se encontro javaw.exe en "%JAVA_HOME%\bin\javaw.exe"
    exit /b 1
)

if not exist "%JISOCREATOR_LOGS%" mkdir "%JISOCREATOR_LOGS%"

start "" /b "%JAVA_HOME%\bin\javaw.exe" -Dpath.logs="%JISOCREATOR_LOGS%" --add-opens java.base/java.util=ALL-UNNAMED --enable-native-access=ALL-UNNAMED -jar "%SCRIPT_DIR%\jisocreator.jar" !APP_ARGS!
exit /b 0

:usage
echo Uso:
echo   %~nx0 [--java-home RUTA] [--logs RUTA] [args_app...]
echo.
echo Opciones del launcher:
echo   --java-home RUTA   Define el JAVA_HOME a usar (contiene bin\javaw.exe)
echo   --logs RUTA        Define el directorio de logs
exit /b 0

:usage_error
echo.
echo Uso:
echo   %~nx0 [--java-home RUTA] [--logs RUTA] [args_app...]
echo.
echo Opciones del launcher:
echo   --java-home RUTA   Define el JAVA_HOME a usar (contiene bin\javaw.exe)
echo   --logs RUTA        Define el directorio de logs
exit /b 2