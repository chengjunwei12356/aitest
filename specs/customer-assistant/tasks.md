# Tasks: 客户经理助手功能

**Input**: Design documents from `docs/superpowers/specs/2024-12-24-customer-assistant-design.md` and `specs/customer-assistant/plan/plan.md`

**Prerequisites**:
- Entity 层 ✅ (CustomerReminder, KnowledgeArticle, ReminderType, KnowledgeCategory)
- DTO 层 ✅ (CustomerReminderDTO, KnowledgeArticleDTO, ReminderResolveRequest)
- Mapper 接口 ✅ (CustomerAssistantMapper)
- Service 接口 ✅ (CustomerAssistantService)

**Tests**: 包含测试任务

**Organization**: 任务按层次组织，支持独立实现和测试

---

## Format: `[ID] [P?] [Layer] Description`

- **[P]**: 可并行执行（不同文件，无依赖）
- **[Layer]**: 任务所属层（DB, MAPPER, SERVICE, CONTROLLER, TEST）
- 描述中包含精确文件路径

---

## Phase 1: Database (基础设施)

**Purpose**: 创建数据库表并添加测试数据

**⚠️ 关键**: 必须先完成本阶段才能开始 Mapper 实现

- [ ] T001 [P] [DB] 在 schema.sql 中添加 customer_reminder 表定义 `src/main/resources/schema.sql`
- [ ] T002 [P] [DB] 在 schema.sql 中添加 knowledge_article 表定义 `src/main/resources/schema.sql`
- [ ] T003 [P] [DB] 在 data.sql 中添加测试数据（提醒 + 文章） `src/main/resources/data.sql`

**Checkpoint**: 数据库表就绪 - Mapper 可以实现

---

## Phase 2: Mapper XML (数据访问层)

**Purpose**: 实现 MyBatis SQL 映射

**依赖**: Phase 1 完成（数据库表存在）

- [ ] T004 [P] [MAPPER] 创建 CustomerAssistantMapper.xml 实现所有 SQL 映射 `src/main/resources/mapper/CustomerAssistantMapper.xml`
  - getReminders: 按 type 和 status 查询
  - getCustomerReminders: 按 customerId 查询
  - searchKnowledge: 按 category 和 keyword 搜索
  - getKnowledgeArticle: 获取文章详情并增加浏览量
  - resolveReminder: 更新提醒状态为 RESOLVED

**Checkpoint**: Mapper 层就绪 - Service 可以实现

---

## Phase 3: Service Implementation (业务逻辑层)

**Purpose**: 实现业务逻辑

**依赖**: Phase 2 完成（Mapper XML 存在）

- [ ] T005 [P] [SERVICE] 创建 CustomerAssistantServiceImpl.java 实现所有业务方法 `src/main/java/com/example/aitest/service/impl/CustomerAssistantServiceImpl.java`
  - getReminders: 调用 mapper，转换 Entity 到 DTO（填充 description）
  - getCustomerReminders: 调用 mapper，返回客户提醒列表
  - resolveReminder: 调用 mapper 更新状态
  - searchKnowledge: 调用 mapper，转换 tags 字符串为数组
  - getKnowledgeArticle: 调用 mapper 获取文章，增加浏览次数

**Checkpoint**: Service 层就绪 - Controller 可以实现

---

## Phase 4: Controller (API 层)

**Purpose**: 提供 REST API 接口

**依赖**: Phase 3 完成（Service 实现存在）

- [ ] T006 [P] [CONTROLLER] 创建 CustomerAssistantController.java 实现 REST API `src/main/java/com/example/aitest/controller/CustomerAssistantController.java`
  - GET /api/customer-assistant/reminders
  - GET /api/customer-assistant/reminders/{customerId}
  - POST /api/customer-assistant/reminders/resolve
  - GET /api/customer-assistant/knowledge
  - GET /api/customer-assistant/knowledge/{id}

**Checkpoint**: API 层就绪 - 可以开始测试

---

## Phase 5: Testing (测试层)

**Purpose**: 确保功能正确性

**依赖**: Phase 4 完成（Controller 实现存在）

- [ ] T007 [P] [TEST] 创建 CustomerAssistantServiceImplTest.java 单元测试 `src/test/java/com/example/aitest/service/impl/CustomerAssistantServiceImplTest.java`
- [ ] T008 [P] [TEST] 创建 CustomerAssistantControllerTest.java 接口测试 `src/test/java/com/example/aitest/controller/CustomerAssistantControllerTest.java`

**Checkpoint**: 测试完成 - 功能可交付

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: 跨功能的改进和优化

- [ ] T009 [P] 添加数据权限控制（客户经理只能查看自己的客户提醒）
  - 需要集成 Spring Security 或 Session 获取当前登录用户
  - 在 Service 层添加 userId 过滤
- [x] T010 [P] 添加参数校验（请求参数有效性检查）
  - ReminderResolveRequest.reminderId 添加 @NotNull
  - Controller 方法添加 @Valid 注解
  - GlobalExceptionHandler 已支持校验异常处理
- [x] T011 代码审查和清理
  - 代码结构良好，遵循项目规范
  - 异常处理完善
  - 枚举使用 @JsonCreator 支持反序列化
- [x] T012 更新 API 文档
  - 已创建 docs/api/customer-assistant-api.md

---

## 依赖关系与执行顺序

### Phase Dependencies

| 阶段 | 依赖 | 说明 |
|------|------|------|
| Database (Phase 1) | 无 | 可立即开始 |
| Mapper (Phase 2) | Phase 1 完成 | 需要数据库表存在 |
| Service (Phase 3) | Phase 2 完成 | 需要 Mapper XML |
| Controller (Phase 4) | Phase 3 完成 | 需要 Service 实现 |
| Testing (Phase 5) | Phase 4 完成 | 需要 Controller 实现 |
| Polish (Phase 6) | Phase 5 完成 | 最后的优化 |

### Within Each Phase

- 核心功能 → 错误处理 → 优化
- 单个文件内代码顺序：imports → 字段 → 构造器 → 方法

---

## 并行机会

### Phase 内并行

- **Database**: T001, T002, T003 可并行（不同 SQL 块）
- **Mapper**: T004 独立完成（单个 XML 文件）
- **Service**: T005 独立完成（单个 Java 文件）
- **Controller**: T006 独立完成（单个 Java 文件）
- **Testing**: T007, T008 可并行

---

## 任务统计

| 阶段 | 任务数 | 说明 |
|------|--------|------|
| Database | 3 | 表定义 + 测试数据 |
| Mapper | 1 | XML 映射文件 |
| Service | 1 | 业务逻辑实现 |
| Controller | 1 | REST API |
| Testing | 2 | 单元测试 + 接口测试 |
| Polish | 4 | 优化和验证 |
| **总计** | **12** |

---

## MVP 范围建议

**最小可行产品（Phase 1-4）**:
- T001-T003: 数据库表 + 测试数据
- T004: Mapper XML
- T005: Service 实现
- T006: Controller API

**可交付价值**: 客户经理可以通过 API 查询提醒和知识库文章

**后续增量**:
- Phase 5: 添加单元测试和接口测试
- Phase 6: 完善权限控制和参数校验
