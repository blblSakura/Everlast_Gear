@echo off
setlocal
set "WORKSPACE=%~dp0.."
set "GRADLE_USER_HOME=%WORKSPACE%\gradle-home"
if not defined JAVA_HOME for /d %%J in ("%WORKSPACE%\tools\jdk-25*") do set "JAVA_HOME=%%~fJ"
if not exist "%JAVA_HOME%\bin\java.exe" (
  echo Java 25 is required. Set JAVA_HOME or extract a JDK 25 into tools.
  exit /b 1
)
set "JAVA_TOOL_OPTIONS=-Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=65532 -Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=65532 -Dhttp.nonProxyHosts=localhost|127.0.0.1 -Dhttp.keepAlive=false"
call "%~dp0gradlew.bat" -p "%~dp0." %*
exit /b %ERRORLEVEL%