# 客户经理助手 API 文档

## 概述

客户经理助手提供智能提醒管理和知识库查询功能，帮助客户经理更高效地处理日常工作。

**Base URL**: `/api/customer-assistant`

---

## 1. 提醒管理 API

### 1.1 获取提醒列表

获取当前用户的提醒列表，支持按类型和状态过滤。

**请求**:
```
GET /api/customer-assistant/reminders?type=APPLICATION_EXPIRE&status=PENDING
```

**参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | String | 否 | 提醒类型（APPLICATION_EXPIRE/MATERIAL_EXPIRE/APPROVAL_TIMEOUT/FOLLOWUP_REQUIRED/LOAN_RENEW） |
| status | String | 否 | 状态（PENDING/NOTIFIED/RESOLVED） |

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "customerId": 1,
      "reminderType": "APPLICATION_EXPIRE",
      "reminderTypeDescription": "申请即将过期",
      "title": "申请即将过期提醒",
      "content": "客户张三的贷款申请将于7天后过期",
      "priority": 2,
      "status": "PENDING",
      "dueDate": "2026-05-07T10:00:00"
    }
  ]
}
```

---

### 1.2 获取指定客户的提醒

获取某个客户的所有提醒。

**请求**:
```
GET /api/customer-assistant/reminders/{customerId}
```

**参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| customerId | Long | 是 | 客户 ID（路径参数） |

**响应**: 同 1.1

---

### 1.3 解决提醒

标记某个提醒为已解决。

**请求**:
```
POST /api/customer-assistant/reminders/resolve
Content-Type: application/json

{
  "reminderId": 1,
  "note": "已处理，客户已补充材料"
}
```

**参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| reminderId | Long | 是 | 提醒 ID |
| note | String | 否 | 解决备注 |

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**错误响应** (参数校验失败):
```json
{
  "code": 400,
  "message": "提醒 ID 不能为空",
  "data": null
}
```

---

## 2. 知识库 API

### 2.1 搜索知识库文章

搜索知识库文章，支持按分类和关键词过滤。

**请求**:
```
GET /api/customer-assistant/knowledge?category=FAQ&keyword=贷款
```

**参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| category | String | 否 | 分类（BUSINESS_PROCESS/FAQ/PRODUCT_INFO/APPROVAL_STANDARD） |
| keyword | String | 否 | 搜索关键词（匹配标题和内容） |

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "category": "FAQ",
      "categoryDescription": "常见问题",
      "title": "常见问题：如何查询贷款进度？",
      "content": "您可以通过以下方式查询...",
      "tags": ["FAQ", "进度查询", "客服"],
      "viewCount": 90,
      "isActive": 1
    }
  ]
}
```

---

### 2.2 获取文章详情

获取知识库文章详情，访问次数自动 +1。

**请求**:
```
GET /api/customer-assistant/knowledge/{id}
```

**参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 文章 ID（路径参数） |

**响应**: 同 2.1

**错误响应** (文章不存在或已下架):
```json
{
  "code": 400,
  "message": "文章不存在或已下架",
  "data": null
}
```

---

## 枚举值说明

### ReminderType（提醒类型）

| 值 | 描述 |
|----|------|
| APPLICATION_EXPIRE | 申请即将过期 |
| MATERIAL_EXPIRE | 材料即将过期 |
| APPROVAL_TIMEOUT | 审批超时 |
| FOLLOWUP_REQUIRED | 需要回访 |
| LOAN_RENEW | 续贷提醒 |

### KnowledgeCategory（知识库分类）

| 值 | 描述 |
|----|------|
| BUSINESS_PROCESS | 业务流程 |
| FAQ | 常见问题 |
| PRODUCT_INFO | 产品信息 |
| APPROVAL_STANDARD | 审批标准 |

### 提醒状态

| 值 | 描述 |
|----|------|
| PENDING | 待处理 |
| NOTIFIED | 已通知 |
| RESOLVED | 已解决 |

---

## 测试数据

启动应用后，数据库自动创建以下测试数据：

**提醒数据**:
- 申请即将过期提醒（张三，PENDING，7天后到期）
- 材料即将过期（李四，PENDING，30天后到期）
- 审批超时提醒（王五，PENDING，1天后到期）
- 客户回访提醒（张三，NOTIFIED，12小时后）
- 续贷提醒（李四，PENDING，30天后到期）

**知识库文章**:
- 贷款申请流程指南（业务流程）
- 常见问题：如何查询贷款进度？（FAQ）
- 个人消费贷款产品介绍（产品信息）
- 信用贷款审批标准（审批标准）
