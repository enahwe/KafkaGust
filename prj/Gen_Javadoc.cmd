@echo off

echo ==================================
echo = GENERATE THE KAFKAGUST JAVADOC =
echo ==================================

set MODULE_DIR=%~dp0

set WORK_DIR=%MODULE_DIR%\.gen

set LOG_FILE_NAME=Gen_Javadoc.log
set LOG_DIR=log
if not exist "%WORK_DIR%\%LOG_DIR%" (
    mkdir "%WORK_DIR%\%LOG_DIR%"
)

echo.
echo - Please wait...
call "%M2_HOME%\bin\mvn" generate-sources javadoc:javadoc > "%WORK_DIR%\%LOG_DIR%\%LOG_FILE_NAME%"

echo.
echo [ GENERATION ENDED ]
echo.
echo - Logs in the file ".\.gen\%LOG_DIR%\%LOG_FILE_NAME%".
echo.

pause
