@echo off
REM SkinTrack Dev Setup — 安装 app-dev skill 到 ~/.claude/skills/app-dev/
REM 适用于 Windows

set SKILL_DIR=%USERPROFILE%\.claude\skills\app-dev

echo Installing app-dev skill...
xcopy /E /Y /I "%~dp0skill" "%SKILL_DIR%" >nul

echo Skill installed to %SKILL_DIR%
echo Ready to use /app commands.
