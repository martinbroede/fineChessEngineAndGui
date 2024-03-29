@echo off
if NOT exist "%~dp0\BLOCK_UPDATE" (
java -jar fineChess.jar UPDATE
rd "%~dp0\src" /s /q
rd "%~dp0\fonts" /s /q
powershell Expand-Archive latestRelease.zip -FORCE
xcopy "%~dp0\latestRelease\fineChessEngineAndGui-main" "%~dp0" /s /e /h /i /y
rd "%~dp0\latestRelease" /s /q
del "%~dp0\latestRelease.zip"
del "%~dp0\WinFineChess.exe"
) ELSE (
echo You need to delete file BLOCK_UPDATE to update fineChess
)
pause