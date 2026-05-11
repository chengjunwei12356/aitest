# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

Spring Boot 3.2.5 银行信贷管理系统，提供用户认证、角色权限、贷款审批等功能。

## 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Java | 17 | OpenJDK 17.0.2 |
| Spring Boot | 3.2.5 | Web 框架 |
| MyBatis | 3.0.4 | ORM 框架 |
| MySQL | 8.0 | 数据库 |
| Thymeleaf | 3.1.2 | 模板引擎 |
| Lombok | 1.18.32 | 代码简化 |

## 构建和运行

```bash
# 设置 Java 环境
set JAVA_HOME=C:/Users/Admin/AppData/Local/Temp/jdk17/jdk-17.0.2

# 编译打包
mvn clean package -DskipTests

# 运行项目
java -jar target/aitest-0.0.1-SNAPSHOT.jar

# 或直接使用批处理
run.bat
```

## 数据库配置

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/aitest
spring.datasource.username=root
spring.datasource.password=root
```

数据库脚本：`src/main/resources/schema.sql`、`src/main/resources/data.sql`

## 代码架构

```
src/main/java/com/example/aitest/
├── AitestApplication.java          # 启动类
├── controller/                      # REST 控制器（/api/*）
├── service/                         # 服务接口
│   └── impl/                        # 服务实现
├── mapper/                          # MyBatis Mapper 接口
├── entity/                          # 实体类（User, Role, Menu 等）
├── dto/                             # 数据传输对象（*Request, *Response）
├── common/                          # 通用类（Result, ResultCode）
├── config/                          # 配置类（CORS, 全局异常等）
└── util/                            # 工具类
```

## 编码规范

### 统一响应格式

```java
// 成功
return Result.success(data);

// 业务异常（推荐）
throw new BusinessException(ResultCode.USER_NOT_FOUND);

// 错误示例 - 禁止直接返回错误
return Result.error("用户不存在");
```

### 异常处理

- 使用 `BusinessException` 抛出业务异常
- 全局异常处理器 `GlobalExceptionHandler` 统一处理
- 禁止静默捕获 `Exception`

### 密码加密

所有密码必须使用 `BCryptPasswordEncoder` 加密：

```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
boolean matches = encoder.matches(rawPassword, hashedPassword);
```

### 日志规范

```java
log.info("用户登录，username: {}", request.getUsername());
log.warn("验证码错误，sessionId: {}", sessionId);
log.error("数据库查询失败", e);
```

## API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/auth/captcha | 获取验证码 |
| POST | /api/auth/login | 登录 |
| POST | /api/auth/logout | 登出 |
| GET | /api/users | 用户列表 |
| POST | /api/users | 创建用户 |
| GET | /api/roles | 角色列表 |
| POST | /api/roles | 创建角色 |

## 测试账号

- 用户名：`admin`
- 密码：`123456`

## 常见问题

### 端口被占用
```bash
netstat -ano | findstr :8080
```

### 验证码失效
- 验证码只能使用一次
- 有效期 5 分钟
- 确保使用最新的 sessionId
