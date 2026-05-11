# 启动说明

## 快速启动

### 方法一：使用启动脚本（推荐）

双击运行 `start.bat` 文件，脚本会自动：
1. 检查并清理占用 8080 端口的旧进程
2. 启动应用（后台运行）
3. 等待应用启动（最多30秒）
4. 自动打开浏览器访问 http://localhost:8080

### 方法二：命令行启动

```batch
cd c:\Users\Admin\git\aitest
start.bat
```

## 停止服务

### 方法一：使用停止脚本

双击运行 `stop.bat` 文件

### 方法二：命令行停止

```batch
cd c:\Users\Admin\git\aitest
stop.bat
```

### 方法三：任务管理器

1. 打开任务管理器 (Ctrl+Shift+Esc)
2. 找到 "Java(TM) Platform SE Binary" 或窗口标题为 "aitest-server" 的进程
3. 右键 -> 结束任务

## 常见问题

### 1. 启动失败 - 端口被占用

**症状**: 提示端口 8080 已被占用

**解决**:
- 运行 `stop.bat` 停止旧服务
- 或者手动终止进程：`taskkill /F /PID <PID>`

### 2. 启动失败 - 找不到 Java

**症状**: 提示 'java' 不是内部或外部命令

**解决**:
确保已安装 JDK 17，并设置环境变量：
```batch
set JAVA_HOME=C:\path\to\jdk17
set PATH=%JAVA_HOME%\bin;%PATH%
```

### 3. 数据库连接失败

**症状**: 日志中出现 "Communications link failure"

**解决**:
1. 确保 MySQL 服务已启动
2. 创建数据库：`CREATE DATABASE aitest;`
3. 检查 `application.properties` 中的数据库配置

### 4. 应用启动慢

首次启动可能需要 30-60 秒，请耐心等待。

## 查看日志

日志文件位置：`logs/application.log`

实时查看日志：
```batch
type logs\application.log
```

或使用 PowerShell：
```powershell
Get-Content logs\application.log -Wait -Tail 50
```

## 测试账号

- 用户名：admin
- 密码：123456

## 访问地址

- 首页：http://localhost:8080
- 客户经理助手：http://localhost:8080/customer-assistant
- API 文档：查看 `docs/api/` 目录

## 故障排查

如果 start.bat 无法正常工作，可以手动执行以下步骤：

```batch
cd c:\Users\Admin\git\aitest

REM 1. 构建项目
mvn clean package -DskipTests

REM 2. 启动应用
java -jar target\aitest-0.0.1-SNAPSHOT.jar

REM 3. 在另一个窗口检查状态
netstat -ano | findstr :8080
```
