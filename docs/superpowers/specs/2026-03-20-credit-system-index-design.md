# 银行信贷管理系统 - 首页功能设计文档

**日期：** 2026-03-20
**版本：** 1.0
**状态：** 已批准

---

## 1. 概述

### 1.1 项目背景

在现有登录功能基础上，开发银行信贷管理系统的登录后首页，包含工作台、公告栏、菜单导航，以及用户管理、角色管理的完整功能。

### 1.2 功能范围

| 模块 | 状态 | 说明 |
|------|------|------|
| 首页框架 | 完整实现 | 菜单栏、顶部导航、主内容区 |
| 工作台 | 完整实现 | 待办任务卡片展示（静态数据） |
| 公告栏 | 完整实现 | 数据库管理，动态加载 |
| 用户管理 | 完整实现 | CRUD、角色分配、密码重置、状态管理 |
| 角色管理 | 完整实现 | CRUD、菜单权限分配、状态管理 |
| 公告管理 | 完整实现 | 后台 CRUD、发布/下架 |
| 其他 9 个菜单 | 占位 | 显示菜单，点击提示"敬请期待" |

---

## 2. 系统架构

### 2.1 模块结构

```
银行信贷管理系统
├── 认证模块（已完成）
│   └── 登录/登出
├── 首页模块
│   ├── 工作台（待办任务统计）
│   └── 公告栏（最新公告）
├── 用户管理模块
│   ├── 用户列表/搜索/筛选
│   ├── 新增/编辑/删除用户
│   ├── 角色分配
│   ├── 密码重置
│   └── 状态管理（启用/禁用）
├── 角色管理模块
│   ├── 角色列表
│   ├── 新增/编辑/删除角色
│   ├── 菜单权限分配
│   └── 状态管理
├── 公告管理模块
│   ├── 公告列表
│   ├── 新增/编辑/删除公告
│   └── 发布/下架
└── 占位菜单（9 个）
    ├── 申请管理、审批管理、合同管理
    ├── 放款管理、还款管理、预警管理
    └── 贷后管理、流程管理、报表管理
```

### 2.2 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Java | 17 | OpenJDK |
| Spring Boot | 3.2.5 | Web 框架 |
| Thymeleaf | 3.1.2 | 模板引擎 |
| MyBatis | 3.0.4 | ORM |
| MySQL | 8.0 | 数据库 |
| Lombok | 1.18.32 | 代码简化 |

---

## 3. 数据库设计

### 3.1 角色表 (role)

```sql
CREATE TABLE `role` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `code` VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';
```

### 3.2 菜单表 (menu)

```sql
CREATE TABLE `menu` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父菜单 ID',
    `path` VARCHAR(100) DEFAULT NULL COMMENT '路径',
    `icon` VARCHAR(50) DEFAULT NULL COMMENT '图标',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `permission` VARCHAR(50) DEFAULT NULL COMMENT '权限标识',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';
```

### 3.3 用户 - 角色关联表 (user_role)

```sql
CREATE TABLE `user_role` (
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`role_id`) REFERENCES `role`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户 - 角色关联表';
```

### 3.4 角色 - 菜单关联表 (role_menu)

```sql
CREATE TABLE `role_menu` (
    `role_id` BIGINT NOT NULL,
    `menu_id` BIGINT NOT NULL,
    PRIMARY KEY (`role_id`, `menu_id`),
    FOREIGN KEY (`role_id`) REFERENCES `role`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`menu_id`) REFERENCES `menu`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色 - 菜单关联表';
```

### 3.5 公告表 (announcement)

```sql
CREATE TABLE `announcement` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT COMMENT '内容',
    `publish_status` TINYINT DEFAULT 0 COMMENT '发布状态：0-草稿，1-已发布，2-已下架',
    `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';
```

### 3.6 初始化数据

```sql
-- 初始化角色
INSERT INTO `role` (`name`, `code`, `description`, `status`) VALUES
('系统管理员', 'ADMIN', '拥有所有权限', 1),
('信贷审批员', 'APPROVER', '负责贷款审批', 1),
('客户经理', 'MANAGER', '负责客户管理', 1),
('普通用户', 'USER', '基础权限', 1);

-- 初始化菜单
INSERT INTO `menu` (`name`, `parent_id`, `path`, `icon`, `sort_order`, `permission`, `status`) VALUES
('工作台', 0, '/index', 'dashboard', 1, 'index:view', 1),
('申请管理', 0, '/application', 'file-text', 2, 'application:view', 1),
('审批管理', 0, '/approval', 'check-circle', 3, 'approval:view', 1),
('合同管理', 0, '/contract', 'file', 4, 'contract:view', 1),
('放款管理', 0, '/loan', 'dollar', 5, 'loan:view', 1),
('还款管理', 0, '/repayment', 'refresh-cw', 6, 'repayment:view', 1),
('预警管理', 0, '/warning', 'alert-triangle', 7, 'warning:view', 1),
('贷后管理', 0, '/postloan', 'clipboard', 8, 'postloan:view', 1),
('用户管理', 0, '/user', 'users', 9, 'user:view', 1),
('角色管理', 0, '/role', 'user-check', 10, 'role:view', 1),
('流程管理', 0, '/workflow', 'settings', 11, 'workflow:view', 1),
('报表管理', 0, '/report', 'bar-chart', 12, 'report:view', 1);

-- 管理员角色分配所有菜单权限
INSERT INTO `role_menu` (`role_id`, `menu_id`)
SELECT 1, id FROM `menu`;
```

---

## 4. API 设计

### 4.1 首页 API

| 方法 | 路径 | 说明 | 请求参数 | 响应 |
|------|------|------|----------|------|
| GET | `/index` | 首页页面 | - | HTML |
| GET | `/api/dashboard/todo` | 待办统计 | - | `{todoStats}` |
| GET | `/api/announcements` | 公告列表 | `page, size` | `Page<Announcement>` |

### 4.2 用户管理 API

| 方法 | 路径 | 说明 | 请求参数 | 响应 |
|------|------|------|----------|------|
| GET | `/api/users` | 用户列表 | `page, size, keyword, roleId, status` | `Page<UserVO>` |
| POST | `/api/users` | 新增用户 | `UserDTO` | `Result<User>` |
| PUT | `/api/users/{id}` | 编辑用户 | `UserDTO` | `Result<User>` |
| DELETE | `/api/users/{id}` | 删除用户 | - | `Result<Void>` |
| PUT | `/api/users/{id}/password` | 重置密码 | `{password}` | `Result<Void>` |
| PUT | `/api/users/{id}/status` | 修改状态 | `{status}` | `Result<Void>` |
| PUT | `/api/users/{id}/roles` | 分配角色 | `{roleIds}` | `Result<Void>` |

### 4.3 角色管理 API

| 方法 | 路径 | 说明 | 请求参数 | 响应 |
|------|------|------|----------|------|
| GET | `/api/roles` | 角色列表 | `page, size` | `Page<Role>` |
| GET | `/api/roles/{id}` | 角色详情 | - | `RoleVO` |
| POST | `/api/roles` | 新增角色 | `RoleDTO` | `Result<Role>` |
| PUT | `/api/roles/{id}` | 编辑角色 | `RoleDTO` | `Result<Role>` |
| DELETE | `/api/roles/{id}` | 删除角色 | - | `Result<Void>` |
| PUT | `/api/roles/{id}/status` | 修改状态 | `{status}` | `Result<Void>` |
| PUT | `/api/roles/{id}/menus` | 分配菜单 | `{menuIds}` | `Result<Void>` |
| GET | `/api/roles/menus` | 所有菜单 | - | `List<MenuVO>` |

### 4.4 公告管理 API

| 方法 | 路径 | 说明 | 请求参数 | 响应 |
|------|------|------|----------|------|
| GET | `/api/announcements` | 公告列表 | `page, size, status` | `Page<Announcement>` |
| POST | `/api/announcements` | 新增公告 | `AnnouncementDTO` | `Result<Announcement>` |
| PUT | `/api/announcements/{id}` | 编辑公告 | `AnnouncementDTO` | `Result<Announcement>` |
| DELETE | `/api/announcements/{id}` | 删除公告 | - | `Result<Void>` |
| PUT | `/api/announcements/{id}/publish` | 发布公告 | - | `Result<Void>` |
| PUT | `/api/announcements/{id}/unpublish` | 下架公告 | - | `Result<Void>` |

---

## 5. 页面设计

### 5.1 配色方案

| 类型 | 颜色 | 用途 |
|------|------|------|
| 主色 | `#1a3a5c` | 顶部导航、主菜单 |
| 辅色 | `#2c5282` | 次级菜单、悬停 |
| 背景 | `#f5f7fa` | 页面背景 |
| 卡片 | `#ffffff` | 内容卡片 |
| 文字 | `#333333` | 主要文字 |
| 次要文字 | `#666666` | 次要文字 |
| 边框 | `#d1d9e6` | 分割线、边框 |
| 成功 | `#28a745` | 状态、按钮 |
| 警告 | `#ffc107` | 状态、提示 |
| 危险 | `#dc3545` | 删除、禁用 |

### 5.2 布局结构

```
┌─────────────────────────────────────────────────────────────┐
│ 顶部导航栏 (高度 60px, 背景#1a3a5c)                          │
│ [Logo] 银行信贷管理系统                    用户名 [退出]     │
├───────────────┬─────────────────────────────────────────────┤
│ 左侧菜单      │ 主内容区 (背景#f5f7fa)                       │
│ (宽度 220px)  │ ┌─────────────────────────────────────────┐ │
│ 固定不滚动    │ │ 工作台区域                               │ │
│ 背景#1a3a5c   │ │ ┌────┐ ┌────┐ ┌────┐ ┌────┐           │ │
│ 文字白色      │ │ │卡片│ │卡片│ │卡片│ │卡片│           │ │
│               │ │ └────┘ └────┘ └────┘ └────┘           │ │
│               │ └─────────────────────────────────────────┘ │
│               │ ┌─────────────────────────────────────────┐ │
│               │ │ 公告栏区域                               │ │
│               │ │ • 公告 1                                │ │
│               │ │ • 公告 2                                │ │
│               │ └─────────────────────────────────────────┘ │
└───────────────┴─────────────────────────────────────────────┘
```

### 5.3 页面列表

| 页面 | 路径 | 模板文件 | 说明 |
|------|------|----------|------|
| 首页 | `/index` | `index.html` | 工作台 + 公告栏 |
| 用户管理 | `/user` | `user/index.html` | 用户列表 + 操作 |
| 角色管理 | `/role` | `role/index.html` | 角色列表 + 操作 |
| 公告管理 | `/announcement` | `announcement/index.html` | 公告列表 + 操作 |
| 占位页面 | `/{module}` | `placeholder.html` | 通用占位页面 |

---

## 6. 实体设计

### 6.1 User 扩展现字段

```java
// 在现有 User 实体基础上，增加角色关联查询
// 通过 @Transient 或 VO 方式处理一对多关系
```

### 6.2 新增实体类

| 实体类 | 包路径 | 说明 |
|--------|--------|------|
| `Role` | `entity` | 角色实体 |
| `Menu` | `entity` | 菜单实体 |
| `Announcement` | `entity` | 公告实体 |

### 6.3 DTO/VO 设计

| 类名 | 包路径 | 说明 |
|------|--------|------|
| `UserDTO` | `dto` | 用户请求 DTO |
| `UserVO` | `dto` | 用户响应 VO（含角色信息） |
| `RoleDTO` | `dto` | 角色请求 DTO |
| `RoleVO` | `dto` | 角色响应 VO（含菜单信息） |
| `MenuVO` | `dto` | 菜单树形响应 VO |
| `AnnouncementDTO` | `dto` | 公告请求 DTO |
| `TodoStatsVO` | `dto` | 待办统计响应 VO |

---

## 7. 错误处理

### 7.1 新增业务异常码

| 异常码 | 说明 |
|--------|------|
| 2001 | 用户已存在 |
| 2002 | 角色已存在 |
| 2003 | 用户已禁用 |
| 2004 | 角色已禁用 |
| 2005 | 公告无权限操作 |

### 7.2 全局异常处理

复用现有 `GlobalExceptionHandler`，新增对上述异常码的处理。

---

## 8. 安全设计

### 8.1 认证拦截

- 所有 `/api/**` 接口需要登录验证
- 使用拦截器检查 Session/Token

### 8.2 权限控制

- 基于角色的菜单显示控制（简单方案）
- 后端接口校验用户角色权限

### 8.3 密码安全

- 新增用户密码使用 BCrypt 加密
- 密码长度至少 6 位

---

## 9. 测试计划

### 9.1 功能测试

| 模块 | 测试项 |
|------|--------|
| 登录 | 正常登录、错误密码、验证码错误 |
| 首页 | 待办数据显示、公告加载 |
| 用户管理 | 增删改查、角色分配、密码重置 |
| 角色管理 | 增删改查、菜单分配 |
| 公告管理 | 增删改查、发布/下架 |

### 9.2 集成测试

- 登录后跳转到首页
- 菜单权限控制验证
- 分页功能验证

---

## 10. 交付清单

### 10.1 后端代码

- [ ] Entity: Role, Menu, Announcement
- [ ] Mapper: RoleMapper, MenuMapper, AnnouncementMapper
- [ ] Service: 对应接口和实现
- [ ] Controller: IndexController, UserController, RoleController, AnnouncementController
- [ ] DTO/VO: 数据传输对象

### 10.2 前端代码

- [ ] templates/index.html
- [ ] templates/user/index.html
- [ ] templates/role/index.html
- [ ] templates/announcement/index.html
- [ ] templates/placeholder.html
- [ ] static/css/common.css
- [ ] static/js/common.js

### 10.3 数据库

- [ ] schema.sql 更新（新增 5 张表）
- [ ] data.sql 更新（初始化数据）

---

## 11. 依赖项

| 依赖 | 说明 | 状态 |
|------|------|------|
| 现有登录功能 | 会话管理 | 已完成 |
| 现有统一响应 | Result 类 | 已完成 |
| 现有异常处理 | GlobalExceptionHandler | 已完成 |

---

## 12. 风险评估

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 权限控制复杂度 | 中 | 采用简单方案，先控制菜单显示 |
| 数据迁移 | 低 | 新增表不影响现有数据 |
| UI 兼容性 | 低 | 使用标准 CSS，不依赖特定浏览器 |
