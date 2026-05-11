@echo off
chcp 65001 >nul
setlocal

echo ========================================
echo   银行信贷管理系统 - 停止脚本
echo ========================================
echo.

set PORT=8080

echo 正在查找占用端口 %PORT% 的进程...
echo.

REM 查找占用端口的进程
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%PORT% ^| findstr LISTENING') do (
    set PID=%%a
    goto :found
)

echo [提示] 未找到运行中的应用
pause
exit /b 0

:found
echo 发现进程 PID=%PID%
echo.
set /p CONFIRM="确认要终止该进程吗？(Y/N): "

if /i not "%CONFIRM%"=="Y" (
    echo 已取消
    pause
    exit /b 0
)

echo.
echo 正在终止进程 %PID%...
taskkill /F /PID %PID%

if errorlevel 1 (
    echo [错误] 终止进程失败
) else (
    echo [成功] 应用已停止
)

echo.
pause
