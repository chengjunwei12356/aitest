@echo off
chcp 65001 >nul
echo.
echo ========================================
echo   启动脚本测试
echo ========================================
echo.

REM 检查必要文件
echo [测试1] 检查 JAR 文件...
if exist "target\aitest-0.0.1-SNAPSHOT.jar" (
    echo [PASS] JAR 文件存在
) else (
    echo [FAIL] JAR 文件不存在
    goto :end
)

echo.
echo [测试2] 检查 Java 环境...
where java >nul 2>&1
if errorlevel 1 (
    echo [WARN] Java 未在 PATH 中找到
    REM 尝试使用 bundled JDK
    if exist "C:\Users\Admin\AppData\Local\Temp\jdk17\jdk-17.0.2\bin\java.exe" (
        echo [INFO] 找到 bundled JDK
        set JAVA_HOME=C:\Users\Admin\AppData\Local\Temp\jdk17\jdk-17.0.2
        set PATH=%JAVA_HOME%\bin;%PATH%
    ) else (
        echo [FAIL] 未找到 Java
        goto :end
    )
) else (
    echo [PASS] Java 可用
)

echo.
echo [测试3] 检查端口占用...
netstat -ano | findstr :8080 | findstr LISTENING >nul 2>&1
if errorlevel 1 (
    echo [PASS] 端口 8080 未被占用
) else (
    echo [WARN] 端口 8080 已被占用
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
        echo        PID: %%a
    )
)

echo.
echo [测试4] 检查日志目录...
if not exist "logs" mkdir logs
if exist "logs" (
    echo [PASS] 日志目录就绪
) else (
    echo [FAIL] 无法创建日志目录
)

echo.
:end
echo ========================================
echo   测试完成
echo ========================================
echo.
pause
