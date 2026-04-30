# Implementation Plan: 客户经理助手功能

**Branch**: `001-home-dashboard` | **Date**: 2026-04-28 | **Spec**: [design.md](../../docs/superpowers/specs/2024-12-24-customer-assistant-design.md)

---

## Summary

实现银行信贷管理系统客户经理助手功能，包括：
- **智能时效性提醒**: 5类提醒（申请过期、材料过期、审批超时、回访、续贷）
- **知识库查询**: 业务流程、FAQ、产品信息、审批标准四类文章
- **REST API**: 提供提醒管理和知识库查询接口

技术方法：基于现有 Spring Boot + MyBatis 架构，新增 CustomerAssistantController、Service、Mapper。

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
- API 响应时间 < 1 秒
- 支持并发用户 100+

**Constraints**:
- 现有 Spring Boot 3.2.5 架构
- 使用 MyBatis ORM
- Thymeleaf 模板引擎

**Scale/Scope**:
- 2 个新数据库表 (customer_reminder, knowledge_article)
- 1 个 Controller + 1 个 Service + 1 个 Mapper
- 4 个 Entity (含 2 个枚举)
- 3 个 DTO

---

## Constitution Check

*GATE: Must pass before implementation. Re-check after design.*

### 代码质量优先 ✅

| 检查项 | 状态 |
|--------|------|
| Controller-Service-Mapper 分层 | 通过 - 遵循现有架构 |
| 统一响应格式 (Result) | 通过 - API 使用 Result 返回 |
| 业务异常使用 BusinessException | 通过 - 需添加异常处理 |
| 禁止静默捕获 Exception | 待确认 |

### 测试覆盖 ⚠️

| 检查项 | 状态 |
|--------|------|
| Service 层单元测试 > 80% | 待完成 |
| Controller 层接口测试 | 待完成 |

### 安全合规 ✅

| 检查项 | 状态 |
|--------|------|
| SQL 注入防护 | 通过 - MyBatis 参数化查询 |
| XSS 防护 | 通过 - API 返回 JSON |
| 数据权限 | 需实现 - 客户经理只能查看自己的客户提醒 |

---

## Project Structure

### Documentation

```
specs/customer-assistant/
├── spec.md              # 功能规范 (引用设计文档)
├── plan.md              # 实现计划 (本文件)
└── tasks.md             # 任务清单
```

### Source Code

```
src/main/java/com/example/aitest/
├── controller/
│   └── CustomerAssistantController.java    # REST API 控制器
├── service/
│   ├── CustomerAssistantService.java      # 服务接口 (已存在)
│   └── impl/
│       └── CustomerAssistantServiceImpl.java  # 服务实现
├── mapper/
│   └── CustomerAssistantMapper.java       # Mapper 接口 (已存在)
├── entity/
│   ├── CustomerReminder.java             # 提醒实体 (已存在)
│   ├── KnowledgeArticle.java             # 文章实体 (已存在)
│   ├── ReminderType.java                 # 提醒类型枚举 (已存在)
│   └── KnowledgeCategory.java            # 文章分类枚举 (已存在)
└── dto/
    ├── CustomerReminderDTO.java          # 提醒 DTO (已存在)
    ├── KnowledgeArticleDTO.java          # 文章 DTO (已存在)
    └── ReminderResolveRequest.java       # 解决请求 DTO (已存在)

src/main/resources/
├── mapper/
│   └── CustomerAssistantMapper.xml      # MyBatis 映射
├── schema.sql                            # 添加新表定义
└── data.sql                              # 添加测试数据
```

---

## Phase 0: Research & Analysis

### Research Tasks

1. **现有代码分析** - 确认 Controller、Service、Mapper 实现模式
2. **数据库表设计** - 确认 customer_reminder 和 knowledge_article 表结构
3. **API 设计** - 确认 REST API 接口设计

### Research Findings

#### 1. 现有代码模式 (从首页工作台学习)

- Controller 使用 `@RestController` + `@RequestMapping`
- Service 接口定义业务方法，ServiceImpl 实现
- Mapper 接口 + XML 映射文件
- 统一响应使用 `Result.success(data)`

#### 2. 数据库表设计 (来自设计文档)

**customer_reminder 表**:
```sql
CREATE TABLE `customer_reminder` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `customer_id` BIGINT NOT NULL,
    `reminder_type` VARCHAR(50) NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `content` TEXT,
    `priority` TINYINT DEFAULT 1,
    `status` VARCHAR(20) DEFAULT 'PENDING',
    `due_date` DATETIME NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_customer_id` (`customer_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_due_date` (`due_date`)
);
```

**knowledge_article 表**:
```sql
CREATE TABLE `knowledge_article` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `category` VARCHAR(50) NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `content` TEXT,
    `tags` VARCHAR(500),
    `view_count` INT DEFAULT 0,
    `is_active` TINYINT DEFAULT 1,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_category` (`category`),
    INDEX `idx_is_active` (`is_active`)
);
```

#### 3. API 设计 (来自设计文档)

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/customer-assistant/reminders | 获取提醒列表 |
| GET | /api/customer-assistant/reminders/{customerId} | 获取客户提醒 |
| POST | /api/customer-assistant/reminders/resolve | 解决提醒 |
| GET | /api/customer-assistant/knowledge | 搜索知识库 |
| GET | /api/customer-assistant/knowledge/{id} | 获取知识文章 |

### Decisions

| Decision | Rationale | Alternatives |
|----------|-----------|--------------|
| 路径前缀 /api/customer-assistant | 清晰的功能模块划分 | /api/assistant - 不够明确 |
| 使用枚举存储类型和分类 | 类型安全，减少错误 | VARCHAR 自由文本 - 容易出错 |
| DTO 与 Entity 分离 | 控制 API 输出，添加描述字段 | 直接使用 Entity - 暴露内部细节 |

---

## Phase 1: Database & Foundation

### Database Schema

#### customer_reminder 表

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | BIGINT | 主键 | AUTO_INCREMENT |
| customer_id | BIGINT | 客户 ID | NOT NULL, INDEX |
| reminder_type | VARCHAR(50) | 提醒类型 | NOT NULL (枚举) |
| title | VARCHAR(200) | 标题 | NOT NULL |
| content | TEXT | 内容 | - |
| priority | TINYINT | 优先级 1-3 | DEFAULT 1 |
| status | VARCHAR(20) | 状态 | DEFAULT 'PENDING', INDEX |
| due_date | DATETIME | 截止时间 | NOT NULL, INDEX |
| created_at | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| updated_at | DATETIME | 更新时间 | ON UPDATE CURRENT_TIMESTAMP |

#### knowledge_article 表

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | BIGINT | 主键 | AUTO_INCREMENT |
| category | VARCHAR(50) | 分类 | NOT NULL (枚举), INDEX |
| title | VARCHAR(200) | 标题 | NOT NULL |
| content | TEXT | 内容 | - |
| tags | VARCHAR(500) | 标签 (逗号分隔) | - |
| view_count | INT | 浏览次数 | DEFAULT 0 |
| is_active | TINYINT | 是否激活 | DEFAULT 1, INDEX |
| created_at | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| updated_at | DATETIME | 更新时间 | ON UPDATE CURRENT_TIMESTAMP |

### MyBatis Mapper XML

需要创建 `CustomerAssistantMapper.xml`，实现：
- `getReminders` - 按类型和状态查询提醒
- `getCustomerReminders` - 按客户 ID 查询提醒
- `searchKnowledge` - 搜索知识库文章
- `getKnowledgeArticle` - 获取文章详情
- `resolveReminder` - 标记提醒为已解决

---

## Phase 2: Service Implementation

### Service 方法实现

| 方法 | 功能 | 说明 |
|------|------|------|
| getReminders | 获取提醒列表 | 支持按类型和状态过滤 |
| getCustomerReminders | 获取客户提醒 | 按 customerId 查询 |
| resolveReminder | 解决提醒 | 更新状态为 RESOLVED |
| searchKnowledge | 搜索知识库 | 支持分类和关键词 |
| getKnowledgeArticle | 获取文章详情 | 并增加浏览次数 |

---

## Phase 3: Controller & API

### REST API 端点

#### 提醒管理 API

```
GET /api/customer-assistant/reminders?type=APPLICATION_EXPIRE&status=PENDING
→ Result<List<CustomerReminderDTO>>

GET /api/customer-assistant/reminders/{customerId}
→ Result<List<CustomerReminderDTO>>

POST /api/customer-assistant/reminders/resolve
Body: { "reminderId": 1, "note": "已处理" }
→ Result<Void>
```

#### 知识库 API

```
GET /api/customer-assistant/knowledge?category=FAQ&keyword=贷款
→ Result<List<KnowledgeArticleDTO>>

GET /api/customer-assistant/knowledge/{id}
→ Result<KnowledgeArticleDTO>
```

---

## Phase 4: Testing & Polish

### 单元测试

- CustomerAssistantServiceImplTest - Service 层测试
- CustomerAssistantControllerTest - Controller 层测试

### 集成测试

- Mapper XML SQL 正确性验证
- API 接口端到端测试

---

## Next Steps

1. ✅ Phase 0 Research - 完成
2. ✅ Phase 1 Database & Foundation - 完成
3. ✅ Phase 2 Service Implementation - 完成
4. ✅ Phase 3 Controller & API - 完成
5. ✅ Phase 4 Testing - 完成（测试代码已写，待运行验证）
6. 🔄 Phase 6 Polish & Cross-Cutting Concerns - 部分完成
   - ✅ T010 参数校验
   - ✅ T011 代码审查
   - ✅ T012 API 文档
   - ⏳ T009 数据权限控制（需要认证系统集成）
   - ⏳ 运行测试验证
