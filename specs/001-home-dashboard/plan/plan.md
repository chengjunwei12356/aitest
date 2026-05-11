# Implementation Plan: 首页工作台功能

**Branch**: `001-home-dashboard` | **Date**: 2026-03-24 | **Spec**: [spec.md](../spec.md)

---

## Summary

实现银行信贷管理系统首页工作台功能，包括：
- **待办任务概览**: 显示 8 类待办任务数量统计，每 30 秒自动刷新
- **公告栏**: 显示最新 5 条公告，点击跳转详情页
- **导航菜单**: 12 个功能模块入口，当前页高亮
- **顶部导航**: 系统名称、用户信息、退出登录

技术方法：基于现有 Spring Boot + Thymeleaf 架构，新增 DashboardController 和公告详情页面。

---

## Technical Context

**Language/Version**: Java 17 (OpenJDK 17.0.2)

**Primary Dependencies**:
- Spring Boot 3.2.5 (Web, Thymeleaf)
- MyBatis 3.0.4
- MySQL 8.0

**Storage**: MySQL 8.0 数据库

**Testing**: JUnit 5 + Spring Boot Test

**Target Platform**: 服务器端 Java Web 应用

**Project Type**: Web 应用 (后端 + Thymeleaf 前端)

**Performance Goals**:
- 首页加载时间 < 3 秒
- API 响应时间 < 1 秒
- 支持并发用户 100+

**Constraints**:
- 现有 Spring Boot 3.2.5 架构
- 使用 MyBatis ORM
- Thymeleaf 模板引擎

**Scale/Scope**:
- 单页面 + 1 个 API 控制器
- 2 个数据实体 (待办统计、公告)
- 公告详情页

---

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### 代码质量优先 ✅

| 检查项 | 状态 |
|--------|------|
| Controller-Service-Mapper 分层 | 通过 - DashboardController 遵循 REST 规范 |
| 统一响应格式 (Result) | 通过 - API 使用 Result 返回 |
| 业务异常使用 BusinessException | 通过 - 无业务异常场景 |
| 禁止静默捕获 Exception | 通过 - 无异常处理代码 |

### 测试覆盖 ✅

| 检查项 | 状态 |
|--------|------|
| Service 层单元测试 > 80% | 待完成 - 任务阶段创建 |
| Controller 层接口测试 | 待完成 - 任务阶段创建 |

### 安全合规 ✅

| 检查项 | 状态 |
|--------|------|
| 密码 BCrypt 加密 | N/A - 本功能不涉及密码 |
| SQL 注入防护 | 通过 - MyBatis 参数化查询 |
| XSS 防护 | 通过 - Thymeleaf 自动转义 |
| 敏感操作日志 | N/A - 本功能为只读展示 |

### 数据一致性 ✅

| 检查项 | 状态 |
|--------|------|
| 事务边界明确 | 通过 - 只读查询无需事务 |
| 外键约束 | 通过 - 使用现有公告表 |

---

## Project Structure

### Documentation

```text
specs/001-home-dashboard/
├── spec.md              # 功能规范
├── plan.md              # 实现计划 (本文件)
├── research.md          # Phase 0 - 技术研究
├── data-model.md        # Phase 1 - 数据模型
├── quickstart.md        # Phase 1 - 快速开始
└── tasks.md             # Phase 2 - 任务清单
```

### Source Code

项目采用单体 Web 应用结构：

```text
src/main/java/com/example/aitest/
├── controller/
│   └── DashboardController.java    # 待办统计 API
│   └── AnnouncementController.java # 公告 API (新增详情接口)
├── service/
│   └── impl/
│       └── DashboardService.java   # 待办统计服务
│       └── AnnouncementService.java# 公告服务
├── mapper/
│   └── AnnouncementMapper.java     # 公告数据访问
└── entity/
    └── Announcement.java           # 公告实体

src/main/resources/
├── templates/
│   ├── index.html                  # 首页 (已存在，需修改)
│   └── announcement-detail.html    # 公告详情页 (新增)
├── mapper/
│   └── AnnouncementMapper.xml      # MyBatis 映射
└── application.properties          # 应用配置
```

**Structure Decision**: 采用现有项目结构，遵循 Controller-Service-Mapper 分层架构。

---

## Phase 0: Research & Analysis

### Research Tasks

1. **现有代码分析** - 了解现有 Controller、Service、Mapper 实现模式
2. **公告数据表结构** - 确认公告表字段和查询方式
3. **待办数据来源** - 确认各业务模块待办统计接口是否存在

### Research Findings

#### 1. 现有代码模式

从 IndexController 和 DashboardController 分析：
- Controller 使用 `@Controller` 和 `@RestController`
- 统一响应使用 `Result.success(data)` 和 `Result.error(msg)`
- 路径映射使用 `@GetMapping` 和 `@RequestMapping`

#### 2. 公告数据表

```sql
-- 公告表 (announcement)
id          BIGINT PRIMARY KEY
title       VARCHAR(200) NOT NULL
content     TEXT
status      TINYINT      # 0-草稿 1-发布
created_at  DATETIME
updated_at  DATETIME
```

#### 3. 待办数据来源

当前 DashboardController 使用静态模拟数据，需要：
- 确认各业务模块是否有待办统计接口
- 或创建 Service 聚合各业务数据

### Decisions

| Decision | Rationale | Alternatives |
|----------|-----------|--------------|
| 公告详情使用独立页面 | 符合传统 Web 模式，SEO 友好 | 弹窗模式 - 不利于书签和分享 |
| 待办数据使用定时轮询 | 实现简单，用户体验好 | WebSocket 推送 - 复杂度高 |
| 轮询间隔 30 秒 | 平衡实时性和服务器压力 | 60 秒 (太长)/10 秒 (压力大) |

---

## Phase 1: Design & Contracts

### Data Model Design

#### 实体定义

**Announcement (公告)**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| title | VARCHAR(200) | 标题 |
| content | TEXT | 内容 |
| status | TINYINT | 状态 (0-草稿 1-发布) |
| createdBy | BIGINT | 创建人 ID |
| createdAt | DATETIME | 创建时间 |
| updatedAt | DATETIME | 更新时间 |

#### 待办统计 DTO

```java
public class TodoStatsDTO {
    private Integer application;   // 待审批申请
    private Integer approval;      // 待审批
    private Integer contract;      // 待签署合同
    private Integer loan;          // 待放款
    private Integer repayment;     // 待处理还款
    private Integer warning;       // 预警
    private Integer postloan;      // 贷后任务
    private Integer other;         // 其他
}
```

### API Contracts

#### GET /api/dashboard/todo

获取待办任务统计

**Response**:
```json
{
  "code": 200,
  "data": {
    "application": 12,
    "approval": 5,
    "contract": 3,
    "loan": 8,
    "repayment": 20,
    "warning": 3,
    "postloan": 7,
    "other": 15
  }
}
```

#### GET /api/announcements

获取公告列表

**Parameters**:
| Name | Type | Required | Default |
|------|------|----------|---------|
| size | int | No | 5 |

**Response**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "title": "系统维护通知",
      "createdAt": "2026-03-24 10:00:00"
    }
  ]
}
```

#### GET /api/announcements/{id}

获取公告详情 (新增)

**Response**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "title": "系统维护通知",
    "content": "系统将于本周六进行维护...",
    "createdAt": "2026-03-24 10:00:00",
    "createdBy": "管理员"
  }
}
```

### Quickstart

**开发前准备**:

1. 确认公告数据表存在且字段匹配
2. 确认数据库连接配置正确
3. 准备测试数据

**数据库准备**:
```sql
INSERT INTO announcement (title, content, status, created_at) VALUES
('系统上线通知', '银行信贷管理系统正式上线运行', 1, NOW());
```

---

## Complexity Tracking

本功能无 Constitution 违反项，无需特别说明。

---

## Next Steps

1. ✅ Phase 0 Research - 完成
2. ✅ Phase 1 Design - 完成
3. 执行 `/speckit.tasks` - 生成任务清单
4. 执行 `/speckit.implement` - 实现功能
