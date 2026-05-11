# 申请管理模块 - 设计文档

**日期：** 2026-03-30
**版本：** 1.0
**状态：** 已批准

---

## 1. 概述

### 1.1 模块背景

申请管理是银行信贷管理系统的核心业务模块，作为信贷流程的起点，负责贷款申请的创建、材料上传、提交审批等功能。

### 1.2 功能范围

| 功能 | 说明 | 优先级 |
|------|------|--------|
| 申请创建 | 新建贷款申请，录入客户和贷款信息 | P0 |
| 申请编辑 | 修改草稿状态的申请 | P0 |
| 申请列表 | 分页、筛选、搜索申请记录 | P0 |
| 申请详情 | 查看申请完整信息和材料 | P0 |
| 材料管理 | 分类上传/删除申请材料 | P1 |
| 提交审批 | 将申请提交至审批流程 | P0 |
| 审批操作 | 初审/终审/风控审批 | P1 |
| 客户复用 | 自动保存客户信息至客户库 | P2 |

### 1.3 设计决策汇总

| 决策点 | 选择 |
|--------|------|
| 数据字段 | 方案 A - 基础字段 |
| 审批流程 | 方案 C - 多级条件审批（金额阈值 10 万） |
| 客户信息 | 方案 C - 混合模式（自动入库） |
| 附件材料 | 方案 C - 分类材料管理 |
| 列表筛选 | 方案 C - 完整筛选 |

---

## 2. 数据库设计

### 2.1 贷款申请表 (loan_application)

```sql
CREATE TABLE `loan_application` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `application_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '申请编号',
    `customer_id` BIGINT DEFAULT NULL COMMENT '客户 ID（关联 customer 表）',

    -- 客户信息（冗余存储，方便查询）
    `customer_name` VARCHAR(50) NOT NULL COMMENT '客户姓名',
    `customer_id_no` VARCHAR(18) NOT NULL COMMENT '身份证号',
    `customer_phone` VARCHAR(20) NOT NULL COMMENT '手机号',

    -- 贷款信息
    `loan_amount` DECIMAL(15,2) NOT NULL COMMENT '申请金额',
    `loan_term` INT NOT NULL COMMENT '贷款期限 (月)',
    `loan_purpose` VARCHAR(100) DEFAULT NULL COMMENT '贷款用途',
    `guarantee_type` VARCHAR(20) NOT NULL COMMENT '担保方式',

    -- 审批流程
    `current_stage` VARCHAR(20) DEFAULT 'DRAFT' COMMENT '当前阶段',
    `status` VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态',
    `assigned_to` BIGINT DEFAULT NULL COMMENT '当前处理人',

    -- 审计字段
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX `idx_customer` (`customer_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='贷款申请表';
```

### 2.2 客户信息表 (customer)

```sql
CREATE TABLE `customer` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `id_no` VARCHAR(18) NOT NULL UNIQUE COMMENT '身份证号',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '地址',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX `idx_id_no` (`id_no`),
    INDEX `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户信息表';
```

### 2.3 申请材料表 (application_document)

```sql
CREATE TABLE `application_document` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `application_id` BIGINT NOT NULL COMMENT '申请 ID',
    `doc_type` VARCHAR(30) NOT NULL COMMENT '材料类型',
    `doc_name` VARCHAR(100) NOT NULL COMMENT '文件名称',
    `file_path` VARCHAR(255) NOT NULL COMMENT '存储路径',
    `file_size` BIGINT DEFAULT NULL COMMENT '文件大小 (字节)',
    `is_required` TINYINT DEFAULT 1 COMMENT '是否必传',
    `uploaded_by` BIGINT DEFAULT NULL COMMENT '上传人',
    `uploaded_at` DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (`application_id`) REFERENCES `loan_application`(`id`) ON DELETE CASCADE,
    INDEX `idx_application` (`application_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申请材料表';
```

### 2.4 审批记录表 (approval_record)

```sql
CREATE TABLE `approval_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `application_id` BIGINT NOT NULL COMMENT '申请 ID',
    `stage` VARCHAR(20) NOT NULL COMMENT '审批阶段',
    `approver_id` BIGINT NOT NULL COMMENT '审批人 ID',
    `action` VARCHAR(20) NOT NULL COMMENT '操作：APPROVE/REJECT',
    `comment` TEXT DEFAULT NULL COMMENT '审批意见',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (`application_id`) REFERENCES `loan_application`(`id`) ON DELETE CASCADE,
    INDEX `idx_application` (`application_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批记录表';
```

---

## 3. 状态机设计

### 3.1 状态定义

```java
public enum ApplicationStatus {
    DRAFT("草稿"),           // 未提交
    INITIAL("待初审"),       // 等待初审
    FINAL("待终审"),         // 等待终审
    RISK("待风控审批"),      // 等待风控审批（金额≥10 万）
    APPROVED("已批准"),      // 审批通过
    REJECTED("已拒绝"),      // 审批拒绝
    PENDING_LOAN("待放款"),  // 等待放款
    COMPLETED("已完成");     // 已放款

    private final String description;
}
```

### 3.2 状态流转图

```
                    ┌──────────┐
                    │  草稿    │ ◄──── 新建申请
                    │  DRAFT   │
                    └────┬─────┘
                         │ 提交
                         ▼
      ┌─────────────────────────────────────┐
      │              金额 < 10 万             │
      ▼                                     ▼
┌──────────┐                          ┌──────────┐
│ 待初审   │                          │ 待初审   │
│ INITIAL  │                          │ INITIAL  │
└────┬─────┘                          └────┬─────┘
     │ 通过                                 │ 通过
     ▼                                     ▼
┌──────────┐                          ┌──────────┐
│ 待终审   │                          │ 待终审   │
│ FINAL    │                          │ FINAL    │
└────┬─────┘                          └────┬─────┘
     │ 通过                                 │ 通过
     ▼                                     ▼
┌──────────┐                          ┌───────────┐
│ 已批准   │                          │ 待风控审批 │
│APPROVED  │                          │ RISK      │
└────┬─────┘                          └─────┬─────┘
     │                                     │ 通过
     └──────────────────┬──────────────────┘
                        ▼
                   ┌──────────┐
                   │ 已批准   │
                   │APPROVED  │
                   └────┬─────┘
                        │ 放款
                        ▼
                   ┌──────────┐
                   │ 已完成   │
                   │COMPLETED │
                   └──────────┘

任何阶段 ──────► 已拒绝 (REJECTED)
```

---

## 4. API 设计

### 4.1 申请管理 API

| 方法 | 路径 | 说明 | 请求参数 |
|------|------|------|----------|
| GET | `/api/applications` | 申请列表 | `page, size, status, guaranteeType, minAmount, maxAmount, keyword, startDate, endDate, sortBy, sortOrder` |
| POST | `/api/applications` | 创建申请 | `ApplicationDTO` |
| GET | `/api/applications/{id}` | 申请详情 | - |
| PUT | `/api/applications/{id}` | 编辑申请 | `ApplicationDTO` |
| DELETE | `/api/applications/{id}` | 删除申请 | - |
| POST | `/api/applications/{id}/submit` | 提交审批 | - |
| POST | `/api/applications/{id}/approve` | 审批操作 | `{stage, action, comment}` |

### 4.2 材料管理 API

| 方法 | 路径 | 说明 | 请求参数 |
|------|------|------|----------|
| GET | `/api/applications/{id}/documents` | 材料列表 | - |
| POST | `/api/applications/{id}/documents` | 上传材料 | Multipart File + `docType` |
| DELETE | `/api/applications/{id}/documents/{docId}` | 删除材料 | - |

### 4.3 客户管理 API

| 方法 | 路径 | 说明 | 请求参数 |
|------|------|------|----------|
| GET | `/api/customers` | 客户列表（搜索） | `keyword, page, size` |
| GET | `/api/customers/{id}` | 客户详情 | - |

### 4.4 审批记录 API

| 方法 | 路径 | 说明 | 请求参数 |
|------|------|------|----------|
| GET | `/api/applications/{id}/records` | 审批记录 | - |

---

## 5. DTO/VO 设计

### 5.1 ApplicationDTO

```java
@Data
public class ApplicationDTO {
    private Long id;
    private String applicationNo;
    private Long customerId;

    @NotBlank(message = "客户姓名不能为空")
    private String customerName;

    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^\\d{17}[\\dXx]$", message = "身份证号格式不正确")
    private String customerIdNo;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String customerPhone;

    @NotNull(message = "贷款金额不能为空")
    @DecimalMin(value = "1000.00", message = "贷款金额至少为 1000 元")
    @DecimalMax(value = "10000000.00", message = "贷款金额不能超过 1000 万元")
    private BigDecimal loanAmount;

    @NotNull(message = "贷款期限不能为空")
    @Min(value = 1, message = "贷款期限至少 1 个月")
    @Max(value = 360, message = "贷款期限不能超过 360 个月")
    private Integer loanTerm;

    private String loanPurpose;

    @NotNull(message = "担保方式不能为空")
    private GuaranteeType guaranteeType;
}
```

### 5.2 ApplicationVO

```java
@Data
public class ApplicationVO {
    private Long id;
    private String applicationNo;
    private String customerName;
    private String customerIdNo;
    private String customerPhone;
    private BigDecimal loanAmount;
    private Integer loanTerm;
    private String loanPurpose;
    private GuaranteeType guaranteeType;
    private ApplicationStatus status;
    private String currentStage;
    private String currentHandler;  // 当前处理人姓名
    private Long createdBy;
    private String creatorName;     // 创建人姓名
    private LocalDateTime createdAt;
    private List<DocumentVO> documents;  // 材料列表
    private List<ApprovalRecordVO> records;  // 审批记录
}
```

### 5.3 DocumentVO

```java
@Data
public class DocumentVO {
    private Long id;
    private String docType;
    private String docName;
    private String filePath;
    private Long fileSize;
    private Boolean isRequired;
    private LocalDateTime uploadedAt;
}
```

### 5.4 ApprovalRecordVO

```java
@Data
public class ApprovalRecordVO {
    private Long id;
    private String stage;
    private String approverName;
    private String action;
    private String comment;
    private LocalDateTime createdAt;
}
```

---

## 6. 枚举设计

### 6.1 GuaranteeType (担保方式)

```java
public enum GuaranteeType {
    CREDIT("信用贷款"),
    MORTGAGE("抵押贷款"),
    PLEDGE("质押贷款"),
    GUARANTEE("保证贷款");

    private final String description;
}
```

### 6.2 DocumentType (材料类型)

```java
public enum DocumentType {
    // 必传材料
    ID_CARD_FRONT("身份证正面", true),
    ID_CARD_BACK("身份证反面", true),
    INCOME_PROOF("收入证明", true),
    BANK_STATEMENT("银行流水", true),

    // 可选材料
    PROPERTY_CERT("房产证", false),
    VEHICLE_CERT("车辆证明", false),
    OTHER("其他材料", false);

    private final String description;
    private final boolean required;
}
```

---

## 7. 页面设计

### 7.1 申请列表页

```
┌─────────────────────────────────────────────────────────────────┐
│ 申请管理                                                        │
├─────────────────────────────────────────────────────────────────┤
│ [＋ 新建申请]                                                   │
├─────────────────────────────────────────────────────────────────┤
│ 筛选条件：                                                       │
│ 状态：[全部 ▼]  担保方式：[全部 ▼]  金额：[    ] - [    ]        │
│ 客户姓名/手机号：[            ]  日期：[    ] 至 [    ]  [搜索]  │
│ 排序：[创建日期 ▼]  [重置]                                      │
├─────────────────────────────────────────────────────────────────┤
│ 申请编号      客户    金额      期限   担保   状态    日期    操作│
│ ─────────────────────────────────────────────────────────────── │
│ A2026030001  张三   50,000   12 月  信用   待初审  03-25  [查看]│
│ A2026030002  李四   200,000  36 月  抵押   待风控  03-24  [查看]│
│ A2026030003  王五   80,000   24 月  保证   已批准  03-23  [查看]│
│ A2026030004  赵六   15,000   6 月   信用   草稿    03-22  [编辑]│
│ ...                                                             │
│                                        [导出 Excel]  « 1 2 3 »  │
└─────────────────────────────────────────────────────────────────┘
```

### 7.2 申请详情页

```
┌─────────────────────────────────────────────────────────────────┐
│ 申请详情                         [返回列表] [编辑] [提交审批]    │
├─────────────────────────────────────────────────────────────────┤
│ 基本信息                                                        │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │ 申请编号：A2026030001    申请日期：2026-03-25 14:30         │ │
│ │ 客户姓名：张三           身份证号：110101199001011234       │ │
│ │ 手机号：138****1234      当前状态：待初审                    │ │
│ │ 当前处理人：李审批                                     │ │
│ └─────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│ 贷款信息                                                        │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │ 申请金额：50,000.00 元     贷款期限：12 个月                 │ │
│ │ 贷款用途：消费装修       担保方式：信用贷款                 │ │
│ └─────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│ 申请材料                                                        │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │ 必传材料 (4/4)                                               │ │
│ │   ✓ 身份证正面  id_card_front.jpg  [预览] [删除]             │ │
│ │   ✓ 身份证反面  id_card_back.jpg   [预览] [删除]             │ │
│ │   ✓ 收入证明    income.pdf         [预览] [删除]             │ │
│ │   ✓ 银行流水    bank_statement.pdf [预览] [删除]             │ │
│ │ 可选材料 (0/3)                                               │ │
│ │   ○ 房产证       -              [上传]                       │ │
│ │   ○ 车辆证明     -              [上传]                       │ │
│ │   ○ 其他材料     -              [上传]                       │ │
│ └─────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│ 审批记录                                                        │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │ 2026-03-25 15:00  初审  审批员 A  通过  材料齐全，建议批准    │ │
│ │ 2026-03-25 10:00  提交  张三     -      提交申请             │ │
│ └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## 8. 业务规则

### 8.1 申请编号生成规则

```
格式：A + YYYYMMDD + 4 位序号
示例：A202603300001
```

### 8.2 金额阈值配置

```java
// 可配置化阈值
private static final BigDecimal RISK_APPROVAL_THRESHOLD = new BigDecimal("100000"); // 10 万
```

### 8.3 材料上传规则

- 支持格式：JPG, PNG, PDF
- 单文件大小限制：10MB
- 必传材料必须在提交审批前上传完成

### 8.4 权限控制

| 操作 | 权限要求 |
|------|----------|
| 创建申请 | 所有用户 |
| 编辑/删除 | 仅创建人（草稿状态） |
| 提交审批 | 仅创建人（草稿状态） |
| 初审 | 审批员角色 |
| 终审 | 高级审批员角色 |
| 风控审批 | 风控专员角色 |

---

## 9. 错误处理

### 9.1 业务异常码

| 异常码 | 说明 |
|--------|------|
| 3001 | 申请不存在 |
| 3002 | 申请状态不允许此操作 |
| 3003 | 无权操作此申请 |
| 3004 | 客户不存在 |
| 3005 | 材料类型不正确 |
| 3006 | 必传材料未上传 |
| 3007 | 文件大小超限 |
| 3008 | 文件格式不支持 |

---

## 10. 测试计划

### 10.1 单元测试

- [ ] ApplicationService 测试
- [ ] 状态机流转测试
- [ ] 金额阈值判断测试
- [ ] 材料上传验证测试

### 10.2 集成测试

- [ ] 申请创建流程测试
- [ ] 审批流程测试（金额<10 万）
- [ ] 审批流程测试（金额≥10 万）
- [ ] 文件上传下载测试

### 10.3 端到端测试

- [ ] 创建申请 → 上传材料 → 提交审批 → 初审 → 终审 → 批准
- [ ] 创建申请 → 上传材料 → 提交审批 → 拒绝

---

## 11. 交付清单

### 11.1 后端代码

- [ ] Entity: LoanApplication, Customer, ApplicationDocument, ApprovalRecord
- [ ] Enum: ApplicationStatus, GuaranteeType, DocumentType
- [ ] Mapper: 对应 4 个 Mapper 接口 + XML
- [ ] Service: 对应接口和实现
- [ ] Controller: ApplicationController, DocumentController, CustomerController
- [ ] DTO/VO: 数据传输对象

### 11.2 前端代码

- [ ] templates/application/index.html (列表页)
- [ ] templates/application/create.html (创建页)
- [ ] templates/application/detail.html (详情页)
- [ ] static/js/application.js

### 11.3 数据库

- [ ] schema.sql 更新（新增 4 张表）
- [ ] data.sql 更新（初始化数据）

---

## 12. 依赖项

| 依赖 | 说明 | 状态 |
|------|------|------|
| 用户认证模块 | 获取当前登录用户 | 已完成 |
| 角色管理模块 | 权限控制 | 已完成 |
| 文件存储 | 材料上传 | 需实现 |

---

## 13. 风险评估

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 文件存储复杂度 | 中 | 采用简单本地存储，后续可扩展云存储 |
| 状态机复杂度 | 中 | 编写完整单元测试覆盖 |
| 审批流程变更 | 低 | 阈值配置化，便于调整 |
