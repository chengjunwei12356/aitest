@echo off
chcp 65001 >nul
setlocal

echo ========================================
echo   银行信贷管理系统 - 启动脚本
echo ========================================
echo.

REM 配置参数
set APP_NAME=aitest-0.0.1-SNAPSHOT.jar
set TARGET_DIR=%~dp0target
set LOG_FILE=%~dp0logs\application.log
set PORT=8080

REM 创建日志目录
if not exist "%~dp0logs" mkdir "%~dp0logs"

echo [1/4] 检查并清理旧进程...
echo.

REM 查找并终止占用端口的进程
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%PORT% ^| findstr LISTENING') do (
    echo 发现进程 PID=%%a 占用端口 %PORT%，正在终止...
    taskkill /F /PID %%a >nul 2>&1
    if errorlevel 1 (
        echo [警告] 无法自动终止进程，请手动关闭
    ) else (
        echo [成功] 进程已终止
    )
    timeout /t 2 /nobreak >nul
)

echo [OK] 端口检查完成
echo.

echo [2/4] 重新构建项目...
echo.
echo 正在执行 mvn clean package -DskipTests...
echo.
cd /d "%~dp0"
call mvn clean package -DskipTests
if errorlevel 1 (
    echo.
    echo [错误] 构建失败，请检查编译错误
    pause
    exit /b 1
)
echo.
echo [成功] 项目构建完成
echo.

echo [3/4] 启动应用...
echo.
echo JAR: %TARGET_DIR%\%APP_NAME%
echo 日志: %LOG_FILE%
echo.

REM 后台启动应用
start "aitest-server" javaw -jar "%TARGET_DIR%\%APP_NAME%" > "%LOG_FILE%" 2>&1

echo [4/4] 等待应用启动...
echo.

REM 等待应用启动（最多30秒）
set COUNT=0
:wait_loop
timeout /t 2 /nobreak >nul
set /a COUNT+=1

REM 检查端口是否监听
netstat -ano | findstr :%PORT% | findstr LISTENING >nul 2>&1
if errorlevel 1 (
    if !COUNT! LSS 15 (
        echo 启动中... (!COUNT!/15)
        goto wait_loop
    ) else (
        echo.
        echo [错误] 应用启动超时，请检查日志
        echo.
        type "%LOG_FILE%"
        pause
        exit /b 1
    )
)

echo.
echo ========================================
echo   应用启动成功！
echo ========================================
echo.
echo 访问地址: http://localhost:%PORT%
echo 测试账号: admin / 123456
echo 日志文件: %LOG_FILE%
echo.
echo 停止服务: 关闭窗口或运行 stop.bat
echo.

REM 自动打开浏览器
start "" "http://localhost:%PORT%"

pause
