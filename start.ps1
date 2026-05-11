# 银行信贷管理系统 - PowerShell 启动脚本
# 使用方法: .\start.ps1

$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  银行信贷管理系统 - 启动脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# ==================== 配置参数 ====================
$AppName = "aitest-0.0.1-SNAPSHOT.jar"
$TargetDir = Join-Path $PSScriptRoot "target"
$LogFile = Join-Path $PSScriptRoot "logs\application.log"
$Port = 8080
$JarPath = Join-Path $TargetDir $AppName

# ==================== 创建日志目录 ====================
$LogDir = Join-Path $PSScriptRoot "logs"
if (-not (Test-Path $LogDir)) {
    New-Item -ItemType Directory -Path $LogDir | Out-Null
    Write-Host "[INFO] 创建日志目录: $LogDir" -ForegroundColor Gray
}

# ==================== 步骤1: 检查并清理旧进程 ====================
Write-Host "[1/3] 检查端口 $Port 占用情况..." -ForegroundColor Yellow
Write-Host ""

$ExistingProcess = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue

if ($ExistingProcess) {
    $PID = $ExistingProcess.OwningProcess
    $ProcessName = (Get-Process -Id $PID -ErrorAction SilentlyContinue).ProcessName
    
    Write-Host "[WARN] 发现进程 PID=$PID ($ProcessName) 正在使用端口 $Port" -ForegroundColor Yellow
    Write-Host ""
    
    $Confirm = Read-Host "是否终止该进程？(Y/N)"
    
    if ($Confirm -eq 'Y' -or $Confirm -eq 'y') {
        Write-Host "正在终止进程 $PID..." -ForegroundColor Gray
        
        try {
            Stop-Process -Id $PID -Force -ErrorAction Stop
            Write-Host "[OK] 进程已终止" -ForegroundColor Green
            Write-Host "等待端口释放..." -ForegroundColor Gray
            Start-Sleep -Seconds 3
        }
        catch {
            Write-Host "[ERROR] 无法终止进程，请手动关闭后重试" -ForegroundColor Red
            Read-Host "按回车键退出"
            exit 1
        }
    }
    else {
        Write-Host "[INFO] 已取消操作" -ForegroundColor Gray
        Read-Host "按回车键退出"
        exit 0
    }
}
else {
    Write-Host "[OK] 端口 $Port 可用" -ForegroundColor Green
}

Write-Host ""

# ==================== 步骤2: 检查并构建项目 ====================
Write-Host "[2/3] 检查应用文件..." -ForegroundColor Yellow
Write-Host ""

if (-not (Test-Path $JarPath)) {
    Write-Host "[WARN] 未找到 JAR 文件，开始构建..." -ForegroundColor Yellow
    Write-Host ""
    
    # 检查 Maven
    $MavenExists = Get-Command mvn -ErrorAction SilentlyContinue
    if (-not $MavenExists) {
        Write-Host "[ERROR] 未找到 Maven，请先安装 Maven 并添加到 PATH" -ForegroundColor Red
        Read-Host "按回车键退出"
        exit 1
    }
    
    Write-Host "执行: mvn clean package -DskipTests" -ForegroundColor Gray
    Write-Host ""
    
    Push-Location $PSScriptRoot
    & mvn clean package -DskipTests
    $BuildResult = $LASTEXITCODE
    Pop-Location
    
    if ($BuildResult -ne 0) {
        Write-Host ""
        Write-Host "[ERROR] 构建失败，请检查编译错误" -ForegroundColor Red
        Read-Host "按回车键退出"
        exit 1
    }
    
    Write-Host ""
    Write-Host "[OK] 构建成功" -ForegroundColor Green
}
else {
    Write-Host "[OK] 找到 JAR 文件: $JarPath" -ForegroundColor Green
}

Write-Host ""

# ==================== 步骤3: 启动应用 ====================
Write-Host "[3/3] 启动应用..." -ForegroundColor Yellow
Write-Host ""
Write-Host "配置信息:" -ForegroundColor Cyan
Write-Host "  - JAR 文件: $JarPath"
Write-Host "  - 日志文件: $LogFile"
Write-Host "  - 访问地址: http://localhost:$Port"
Write-Host ""

# 后台启动应用
$JavaArgs = "-jar", $JarPath
$StartArgs = @{
    FilePath = "javaw"
    ArgumentList = $JavaArgs
    WorkingDirectory = $PSScriptRoot
    WindowStyle = "Hidden"
    RedirectStandardOutput = $LogFile
    RedirectStandardError = $LogFile
    NoNewWindow = $true
}

try {
    $Process = Start-Process @StartArgs -PassThru
    Write-Host "[INFO] 应用启动命令已执行 (PID: $($Process.Id))" -ForegroundColor Green
}
catch {
    Write-Host "[ERROR] 启动命令执行失败: $_" -ForegroundColor Red
    Read-Host "按回车键退出"
    exit 1
}

Write-Host "等待应用初始化..." -ForegroundColor Gray
Write-Host ""

# ==================== 等待并验证启动 ====================
$MaxWait = 30
$Count = 0

while ($Count -lt $MaxWait) {
    Start-Sleep -Seconds 2
    $Count++
    
    # 检查端口是否监听
    $Listening = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    
    if ($Listening) {
        break
    }
    
    Write-Host "启动中... ($Count/$MaxWait)" -ForegroundColor Gray
}

if (-not $Listening) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "  [ERROR] 应用启动超时" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "请检查日志文件: $LogFile" -ForegroundColor Yellow
    Write-Host ""
    
    if (Test-Path $LogFile) {
        Write-Host "=== 最近日志 ===" -ForegroundColor Cyan
        Get-Content $LogFile -Tail 20
    }
    
    Write-Host ""
    Read-Host "按回车键退出"
    exit 1
}

# 获取 PID
$AppPID = $Listening.OwningProcess

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  应用启动成功！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "进程 ID: $AppPID" -ForegroundColor Cyan
Write-Host "访问地址: http://localhost:$Port" -ForegroundColor Cyan
Write-Host "测试账号: admin / 123456" -ForegroundColor Cyan
Write-Host "日志文件: $LogFile" -ForegroundColor Cyan
Write-Host ""
Write-Host "提示:" -ForegroundColor Yellow
Write-Host "  - 浏览器将自动打开"
Write-Host "  - 关闭窗口不会停止服务"
Write-Host "  - 停止服务请运行: stop.ps1"
Write-Host ""

# 自动打开浏览器
Start-Process "http://localhost:$Port"

Read-Host "按回车键退出"
