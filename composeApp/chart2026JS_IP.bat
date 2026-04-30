@echo off
title Industrial Monitor - 192.168.0.110
color 0A


set DIST_PATH=build\dist\wasmJs\productionExecutable
set PORT=8001
set MY_IP=192.168.0.110

echo ======================================================
echo    Start monitoring (WASM)
echo    Address: http://%MY_IP%:%PORT%
echo ======================================================


if not exist %DIST_PATH% (
    echo [ERROR] path dosn't find: %DIST_PATH%
    echo take it: ./gradlew wasmJsBrowserDistribution
    pause
    exit
)

:: my path
cd /d %DIST_PATH%

:: open brauser
echo [INFO] Open brauser...
start "" "http://%MY_IP%:%PORT%"

:: run server Python
echo [INFO] server start on port %PORT%...
echo [INFO] Put Ctrl+C for stop.
python -m http.server %PORT% --bind %MY_IP%

if %errorlevel% neq 0 (
    echo [ERROR] server dosn't start.
    echo cheak port  %PORT%.
    pause
)