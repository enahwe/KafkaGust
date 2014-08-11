@echo off

echo ==============================
echo = BUILD THE KAFKAGUST MODULE =
echo ==============================

set MODULE_DIR=%~dp0

set WORK_DIR=%MODULE_DIR%\.gen

set LOG_FILE_NAME=Build_Module.log
set LOG_DIR=log
if not exist "%WORK_DIR%\%LOG_DIR%" (
    mkdir "%WORK_DIR%\%LOG_DIR%"
)

echo.
echo - Please wait...
call "%M2_HOME%\bin\mvn" -e clean install > "%WORK_DIR%\%LOG_DIR%\%LOG_FILE_NAME%"

echo.
echo [ BUILD ENDED ]
echo.
echo - Logs in the file ".\.gen\%LOG_DIR%\%LOG_FILE_NAME%".
echo - Module Jar in the sub-directory ".\.gen\target".
echo.

pause
