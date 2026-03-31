# Tasks: 首页工作台功能

**Input**: Design documents from `/specs/001-home-dashboard/`

**Prerequisites**:
- plan.md ✅
- spec.md ✅
- research.md ✅
- data-model.md ✅
- quickstart.md ✅

**Tests**: 不包含测试任务（规范未明确要求 TDD）

**Organization**: 任务按用户故事组织，支持独立实现和测试

---

## Format: `[ID] [P?] [Story] Description`

- **[P]**: 可并行执行（不同文件，无依赖）
- **[Story]**: 任务所属用户故事（US1, US2, US3）
- 描述中包含精确文件路径

---

## Phase 1: Setup (共享基础设施)

**Purpose**: 项目初始化（现有项目已完成）

- [x] T001 [P] 确认公告表结构与 Announcement 实体字段匹配
- [x] T002 [P] 确认数据库连接配置正确

---

## Phase 2: Foundational (阻塞前提)

**Purpose**: 核心基础设施 - 所有用户故事实现前必须完成

**⚠️ 关键**: 本阶段完成后才能开始用户故事工作

**现状分析**: 项目已有 DashboardController、AnnouncementController、AnnouncementService 等基础代码

需要完成的任务：

- [x] T003 [P] 验证 AnnouncementController 列表接口支持 size 参数 `/api/announcements?size=5`
- [x] T004 [P] 验证 AnnouncementService.findByPage() 方法按 created_at 降序排序
- [x] T005 [P] 修改公告 API 返回格式仅包含 id, title, createdAt 字段

**Checkpoint**: 基础 API 就绪 - 用户故事可以开始实现

---

## Phase 3: User Story 1 - 登录后查看待办任务概览 (Priority: P1) 🎯 MVP

**Goal**: 首页显示 8 类待办任务数量统计，每 30 秒自动刷新

**Independent Test**: 访问 `/index` 页面，8 个待办任务卡片显示正确数量

### Implementation for User Story 1

**现状**: index.html 和 DashboardController 已存在

- [x] T006 [P] [US1] 修改 index.html 添加 30 秒定时刷新功能 `src/main/resources/templates/index.html`
- [x] T007 [P] [US1] 添加错误处理逻辑（数据加载失败时显示提示）
- [x] T008 [US1] 验证 API 数据正确绑定到页面元素

**Checkpoint**: User Story 1 完成 - MVP 可独立使用

---

## Phase 4: User Story 2 - 查看系统公告 (Priority: P2)

**Goal**: 首页显示最新 5 条公告，点击跳转详情页

**Independent Test**: 访问 `/index`，公告栏显示 5 条公告，点击标题跳转详情

### Implementation for User Story 2

**现状**: AnnouncementController 已存在，需要添加详情页

- [x] T009 [P] [US2] 修改 AnnouncementController 列表接口返回简化字段 `src/main/java/com/example/aitest/controller/AnnouncementController.java`
- [x] T010 [P] [US2] 创建公告详情页模板 `src/main/resources/templates/announcement-detail.html`
- [x] T011 [P] [US2] 添加公告详情路由到 IndexController `src/main/java/com/example/aitest/controller/IndexController.java`
- [x] T012 [US2] 修改 index.html 公告链接跳转到详情页
- [x] T013 [US2] 详情页展示标题、内容、发布日期、返回按钮

**Checkpoint**: User Story 1 + 2 完成 - 首页核心功能就绪

---

## Phase 5: User Story 3 - 通过导航菜单访问功能模块 (Priority: P3)

**Goal**: 左侧导航菜单可跳转各功能模块，当前页高亮

**Independent Test**: 点击菜单项正确跳转，对应菜单高亮

### Implementation for User Story 3

**现状**: IndexController 和 index.html 菜单已存在

- [x] T014 [P] [US3] 验证 IndexController 各路由映射正确 `src/main/java/com/example/aitest/controller/IndexController.java`
- [x] T015 [P] [US3] 添加菜单项高亮逻辑到各页面 `src/main/resources/templates/*.html`
- [x] T016 [US3] 实现退出登录功能 `src/main/resources/templates/index.html` 和 `src/main/resources/js/common.js`

**Checkpoint**: 所有用户故事完成

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: 跨功能的改进和优化

- [x] T017 [P] 添加数据加载失败的用户友好提示
- [x] T018 [P] 添加网络超时的重试机制
- [ ] T019 [P] 优化页面加载性能（CSS/JS 压缩）
- [ ] T020 运行 quickstart.md 验证功能
- [ ] T021 代码审查和清理
- [ ] T022 更新 API 文档

---

## 依赖关系与执行顺序

### Phase Dependencies

| 阶段 | 依赖 | 说明 |
|------|------|------|
| Setup (Phase 1) | 无 | 可立即开始 |
| Foundational (Phase 2) | Setup 完成 | 阻塞所有用户故事 |
| User Story 1 (Phase 3) | Foundational 完成 | P1 - MVP |
| User Story 2 (Phase 4) | Foundational 完成 | P2 - 可在 US1 后并行 |
| User Story 3 (Phase 5) | Foundational 完成 | P3 - 可在 US2 后并行 |
| Polish (Phase 6) | 所有用户故事完成 | 最后的优化 |

### User Story Dependencies

- **User Story 1 (P1)**: 无依赖 - Foundational 完成后立即可开始
- **User Story 2 (P2)**: 依赖 US1 的页面框架，但 API 独立
- **User Story 3 (P3)**: 可独立于 US1/US2 实现

### Within Each User Story

- API 层 → 前端页面 → 集成
- 核心功能 → 错误处理 → 优化

---

## 并行机会

### Phase 内并行

- **Setup**: T001, T002 可并行
- **Foundational**: T003, T004, T005 可并行
- **US1**: T006, T007 可并行
- **US2**: T009, T010, T011 可并行
- **US3**: T014, T015 可并行

### 跨 Story 并行

Foundational 阶段完成后：
- Developer A: User Story 1 (待办任务)
- Developer B: User Story 2 (公告)
- Developer C: User Story 3 (导航)

---

## 实现策略

### MVP First (仅 User Story 1)

1. 完成 Phase 1 + 2 → 基础 API 就绪
2. 完成 Phase 3 → 待办任务展示
3. **停止并验证**: 测试 US1 功能
4. 如果 MVP 可用则部署

### 增量交付

1. Setup + Foundational → 基础就绪
2. + User Story 1 → MVP（待办展示）→ 可部署
3. + User Story 2 → 公告功能 → 可部署
4. + User Story 3 → 导航完善 → 可交付

### 并行团队策略

多开发人员场景：
1. 团队共同完成 Setup + Foundational
2. Foundational 完成后分工：
   - 开发 A: User Story 1 (待办 + 刷新)
   - 开发 B: User Story 2 (公告 + 详情)
   - 开发 C: User Story 3 (导航 + 高亮)
3. 各故事独立完成后集成

---

## 任务统计

| 阶段 | 任务数 | 说明 |
|------|--------|------|
| Setup | 2 | 项目确认 |
| Foundational | 3 | API 验证 |
| User Story 1 | 3 | 待办任务展示 |
| User Story 2 | 5 | 公告 + 详情页 |
| User Story 3 | 3 | 导航菜单 |
| Polish | 6 | 优化和验证 |
| **总计** | **22** |

---

## MVP 范围建议

**最小可行产品（仅 User Story 1）**:

- T001-T005: 基础 API 验证
- T006-T008: 待办任务展示 + 自动刷新

**可交付价值**: 用户登录后查看待办任务概览

**后续增量**:
- User Story 2: 增加公告功能
- User Story 3: 完善导航体验
