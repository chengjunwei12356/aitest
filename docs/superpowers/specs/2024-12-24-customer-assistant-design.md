# 客户经理助手功能 - 设计文档

**日期**: 2026-04-24  
**项目**: aitest (银行信贷管理系统)  
**功能模块**: Customer Assistant (客户经理助手)

## 1. 设计概述

### 1.1 目标
为客户经理提供智能时效性提醒和知识库查询功能，提升客户服务效率和贷款办理流程。

### 1.2 核心功能
1. **智能时效性提醒**
   - 申请时效提醒（7天后自动关闭）
   - 材料时效提醒（提前30天）
   - 审批时效提醒（超过3个工作日）
   - 客户回访提醒（24小时内未回复）
   - 续贷提醒（贷款即将到期）

2. **知识库查询**
   - 业务流程指南
   - 常见问题解答
   - 产品知识库
   - 审批标准参考

## 2. 架构设计

### 2.1 整体架构
```
Customer Assistant 模块
├── Controller: CustomerAssistantController
├── Service: CustomerAssistantService
│   ├── ReminderService
│   ├── KnowledgeBaseService
│   └── CustomerEnhancementService
├── Mapper: CustomerAssistantMapper
├── Entity: 新增实体类
└── DTO: 新增数据传输对象
```

### 2.2 技术栈
- Spring Boot 3.2.5
- MyBatis ORM
- MySQL 8.0
- WebSocket (可选，用于实时推送)

## 3. 数据库设计

### 3.1 新增表结构

#### customer_reminder (提醒记录表)
```sql
CREATE TABLE `customer_reminder` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `customer_id` BIGINT NOT NULL,
    `reminder_type` VARCHAR(50) NOT NULL,  -- REMINDER_TYPE 枚举
    `title` VARCHAR(200) NOT NULL,
    `content` TEXT,
    `priority` TINYINT DEFAULT 1,           -- 1-低, 2-中, 3-高
    `status` VARCHAR(20) DEFAULT 'PENDING', -- PENDING, NOTIFIED, RESOLVED
    `due_date` DATETIME NOT NULL,           -- 提醒截止时间
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### knowledge_article (知识库文章表)
```sql
CREATE TABLE `knowledge_article` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `category` VARCHAR(50) NOT NULL,        -- CATEGORY 枚举
    `title` VARCHAR(200) NOT NULL,
    `content` TEXT,
    `tags` VARCHAR(500),                   -- 标签，逗号分隔
    `view_count` INT DEFAULT 0,
    `is_active` TINYINT DEFAULT 1,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 3.2 枚举定义

#### ReminderType (提醒类型)
```java
public enum ReminderType {
    APPLICATION_EXPIRE("申请即将过期"),
    MATERIAL_EXPIRE("材料即将过期"),
    APPROVAL_TIMEOUT("审批超时"),
    FOLLOWUP_REQUIRED("需要回访"),
    LOAN_RENEW("续贷提醒");
}
```

#### KnowledgeCategory (知识库分类)
```java
public enum KnowledgeCategory {
    BUSINESS_PROCESS("业务流程"),
    FAQ("常见问题"),
    PRODUCT_INFO("产品信息"),
    APPROVAL_STANDARD("审批标准");
}
```

## 4. API 设计

### 4.1 时效性提醒 API

```java
@GetMapping("/api/customer-assistant/reminders")
Result<List<CustomerReminderDTO>> getReminders(
    @RequestParam(required = false) ReminderType type,
    @RequestParam(required = false) String status
);

@GetMapping("/api/customer-assistant/reminders/{customerId}")
Result<List<CustomerReminderDTO>> getCustomerReminders(@PathVariable Long customerId);

@PostMapping("/api/customer-assistant/reminders/resolve")
Result<Void> resolveReminder(@RequestBody ReminderResolveRequest request);
```

### 4.2 知识库查询 API

```java
@GetMapping("/api/customer-assistant/knowledge")
Result<List<KnowledgeArticleDTO>> searchKnowledge(
    @RequestParam(required = false) KnowledgeCategory category,
    @RequestParam(required = false) String keyword
);

@GetMapping("/api/customer-assistant/knowledge/{id}")
Result<KnowledgeArticleDTO> getKnowledgeArticle(@PathVariable Long id);
```

## 5. 核心功能实现

### 5.1 时效性提醒服务

#### 功能特性
- **定时任务**：每天凌晨扫描需要提醒的记录
- **推送机制**：通过 API 或 WebSocket 推送
- **自动处理**：过期申请自动关闭、超时审批自动升级
- **模板管理**：支持自定义提醒模板

#### 业务逻辑
1. **申请即将过期**（7天后）
   - 条件：申请状态为 DRAFT，距离过期时间7天
   - 动作：发送提醒，标记为 NOTIFIED

2. **材料即将过期**（提前30天）
   - 条件：材料有效期距当前30天内
   - 动作：发送材料更新提醒

3. **审批超时**（超过3个工作日）
   - 条件：审批时间超过规定时长
   - 动作：升级提醒，通知主管

4. **需要回访**（24小时内）
   - 条件：客户咨询后24小时内未回复
   - 动作：发送回访提醒

5. **续贷提醒**（贷款到期前30天）
   - 条件：贷款距到期时间30天
   - 动作：推荐续贷产品，发送续贷提醒

### 5.2 知识库服务

#### 功能特性
- **全文搜索**：基于标题、内容、标签的模糊搜索
- **分类浏览**：按类别展示知识文章
- **热门推荐**：基于浏览量的智能推荐
- **收藏功能**：客户经理可收藏常用知识

#### 业务逻辑
1. **搜索算法**
   - 支持关键词匹配
   - 支持标签搜索
   - 支持分类过滤
   - 按相关度排序

2. **推荐机制**
   - 基于浏览量排序
   - 基于最近访问推荐
   - 基于用户行为推荐

## 6. 权限设计

### 6.1 角色权限
- **CUSTOMER_MANAGER**: 完整访问权限
- **LOAN_OFFICER**: 基础查询权限
- **ADMIN**: 管理员权限（包括知识库管理）

### 6.2 数据权限
- 客户经理只能查看自己负责的客户提醒
- 知识库对所有授权角色开放

## 7. 前端集成

### 7.1 页面设计
- **提醒中心**: 展示所有待处理提醒
- **知识库**: 搜索和浏览知识文章
- **快速操作**: 从提醒直接跳转到相关操作

### 7.2 交互设计
- 实时更新提醒状态
- 支持批量操作
- 提供操作反馈

## 8. 测试计划

### 8.1 单元测试
- CustomerAssistantControllerTest
- CustomerAssistantServiceTest
- CustomerAssistantMapperTest

### 8.2 集成测试
- API 接口测试
- 数据库事务测试
- 权限验证测试

### 8.3 性能测试
- 提醒查询性能测试
- 知识库搜索性能测试
- 并发访问测试

## 9. 部署计划

### 9.1 数据库迁移
```bash
# 执行 SQL 脚本创建新表
mysql -u root -p aitest < schema/customer_assistant.sql
```

### 9.2 应用部署
- 集成到现有 aitest 应用
- 无需额外部署配置

## 10. 监控和维护

### 10.1 监控指标
- 提醒处理成功率
- 知识库访问量
- API 响应时间

### 10.2 定期维护
- 清理过期提醒
- 更新知识库内容
- 优化搜索算法

## 11. 文档

### 11.1 API 文档
Swagger/OpenAPI 文档生成

### 11.2 用户手册
功能使用说明
常见问题解答

### 11.3 开发文档
架构设计说明
扩展指南