@echo off
setlocal
call "%~dp0gradlew.bat" -p "%~dp0." %*
exit /b %ERRORLEVEL%
