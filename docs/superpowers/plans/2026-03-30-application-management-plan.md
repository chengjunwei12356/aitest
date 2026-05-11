# 申请管理模块 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现银行信贷管理系统的申请管理模块，包括贷款申请 CRUD、材料上传、审批流程功能

**Architecture:** 采用标准 Spring Boot 三层架构（Controller-Service-Mapper），遵循现有代码规范。状态机模式管理申请状态流转，基于金额阈值动态路由审批流程。

**Tech Stack:** Spring Boot 3.2.5, MyBatis, Thymeleaf, MySQL 8.0, Lombok

---

## 文件结构总览

### 新增文件清单

| 类型 | 路径 | 说明 |
|------|------|------|
| Entity | `entity/LoanApplication.java` | 贷款申请实体 |
| Entity | `entity/Customer.java` | 客户信息实体 |
| Entity | `entity/ApplicationDocument.java` | 申请材料实体 |
| Entity | `entity/ApprovalRecord.java` | 审批记录实体 |
| Enum | `entity/ApplicationStatus.java` | 申请状态枚举 |
| Enum | `entity/GuaranteeType.java` | 担保方式枚举 |
| Enum | `entity/DocumentType.java` | 材料类型枚举 |
| Mapper | `mapper/LoanApplicationMapper.java` | 申请 Mapper 接口 |
| Mapper | `mapper/CustomerMapper.java` | 客户 Mapper 接口 |
| Mapper | `mapper/ApplicationDocumentMapper.java` | 材料 Mapper 接口 |
| Mapper | `mapper/ApprovalRecordMapper.java` | 审批记录 Mapper 接口 |
| Mapper XML | `resources/mapper/LoanApplicationMapper.xml` | 申请 Mapper XML |
| Mapper XML | `resources/mapper/CustomerMapper.xml` | 客户 Mapper XML |
| Mapper XML | `resources/mapper/ApplicationDocumentMapper.xml` | 材料 Mapper XML |
| Mapper XML | `resources/mapper/ApprovalRecordMapper.xml` | 审批记录 Mapper XML |
| DTO | `dto/ApplicationDTO.java` | 申请请求 DTO |
| DTO | `dto/ApplicationVO.java` | 申请响应 VO |
| DTO | `dto/DocumentVO.java` | 材料响应 VO |
| DTO | `dto/ApprovalRecordVO.java` | 审批记录 VO |
| Service | `service/ApplicationService.java` | 申请服务接口 |
| Service | `service/CustomerService.java` | 客户服务接口 |
| Service | `service/DocumentService.java` | 材料服务接口 |
| Service | `service/ApprovalService.java` | 审批服务接口 |
| Service | `service/impl/*Impl.java` | 服务实现 (4 个) |
| Controller | `controller/ApplicationController.java` | 申请控制器 |
| Controller | `controller/DocumentController.java` | 材料控制器 |
| Controller | `controller/ApprovalController.java` | 审批控制器 |
| Template | `templates/application/index.html` | 申请列表页 |
| Template | `templates/application/create.html` | 创建申请页 |
| Template | `templates/application/detail.html` | 申请详情页 |
| JS | `static/js/application.js` | 前端逻辑 |
| Test | `test/.../service/ApplicationServiceTest.java` | 服务测试 |

### 修改文件清单

| 文件 | 修改内容 |
|------|----------|
| `schema.sql` | 新增 4 张表 |
| `data.sql` | 初始化数据 |
| `ResultCode.java` | 新增业务异常码 |
| `IndexController.java` | 修改申请管理路由 |

---

## Phase 1: 数据库和实体层

### Task 1: 更新数据库脚本

**Files:**
- Modify: `src/main/resources/schema.sql`
- Modify: `src/main/resources/data.sql`

- [ ] **Step 1: 在 schema.sql 末尾添加 4 张新表**

在 `schema.sql` 文件末尾（`announcement` 表之后）添加：

```sql
-- ===========================
-- 申请管理模块表
-- ===========================

-- 客户信息表
CREATE TABLE IF NOT EXISTS `customer` (
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

-- 贷款申请表
CREATE TABLE IF NOT EXISTS `loan_application` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `application_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '申请编号',
    `customer_id` BIGINT DEFAULT NULL COMMENT '客户 ID',
    `customer_name` VARCHAR(50) NOT NULL COMMENT '客户姓名',
    `customer_id_no` VARCHAR(18) NOT NULL COMMENT '身份证号',
    `customer_phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `loan_amount` DECIMAL(15,2) NOT NULL COMMENT '申请金额',
    `loan_term` INT NOT NULL COMMENT '贷款期限 (月)',
    `loan_purpose` VARCHAR(100) DEFAULT NULL COMMENT '贷款用途',
    `guarantee_type` VARCHAR(20) NOT NULL COMMENT '担保方式',
    `current_stage` VARCHAR(20) DEFAULT 'DRAFT' COMMENT '当前阶段',
    `status` VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态',
    `assigned_to` BIGINT DEFAULT NULL COMMENT '当前处理人',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_customer` (`customer_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='贷款申请表';

-- 申请材料表
CREATE TABLE IF NOT EXISTS `application_document` (
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

-- 审批记录表
CREATE TABLE IF NOT EXISTS `approval_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `application_id` BIGINT NOT NULL COMMENT '申请 ID',
    `stage` VARCHAR(20) NOT NULL COMMENT '审批阶段',
    `approver_id` BIGINT NOT NULL COMMENT '审批人 ID',
    `action` VARCHAR(20) NOT NULL COMMENT '操作',
    `comment` TEXT DEFAULT NULL COMMENT '审批意见',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`application_id`) REFERENCES `loan_application`(`id`) ON DELETE CASCADE,
    INDEX `idx_application` (`application_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批记录表';
```

- [ ] **Step 2: 在 data.sql 添加初始化数据**

在 `data.sql` 文件末尾添加：

```sql
-- 初始化客户数据
INSERT INTO `customer` (`name`, `id_no`, `phone`, `email`) VALUES
('张三', '110101199001011234', '13800138001', 'zhangsan@example.com'),
('李四', '110101199002022345', '13800138002', 'lisi@example.com'),
('王五', '110101199003033456', '13800138003', 'wangwu@example.com');

-- 初始化贷款申请数据
INSERT INTO `loan_application` (`application_no`, `customer_id`, `customer_name`, `customer_id_no`, `customer_phone`, `loan_amount`, `loan_term`, `loan_purpose`, `guarantee_type`, `status`, `current_stage`, `created_by`) VALUES
('A202603300001', 1, '张三', '110101199001011234', '13800138001', 50000.00, 12, '消费装修', 'CREDIT', 'INITIAL', 'INITIAL', 1),
('A202603300002', 2, '李四', '110101199002022345', '13800138002', 200000.00, 36, '企业经营', 'MORTGAGE', 'RISK', 'RISK', 1),
('A202603300003', 3, '王五', '110101199003033456', '13800138003', 80000.00, 24, '购车', 'GUARANTEE', 'APPROVED', 'APPROVED', 1);
```

- [ ] **Step 3: 验证 SQL 语法**

确认 SQL 脚本无语法错误，表名、字段名与设计文档一致。

- [ ] **Step 4: 提交**

```bash
git add src/main/resources/schema.sql src/main/resources/data.sql
git commit -m "feat(application): add database schema and init data for application module"
```

---

### Task 2: 创建实体类和枚举

**Files:**
- Create: `src/main/java/com/example/aitest/entity/ApplicationStatus.java`
- Create: `src/main/java/com/example/aitest/entity/GuaranteeType.java`
- Create: `src/main/java/com/example/aitest/entity/DocumentType.java`
- Create: `src/main/java/com/example/aitest/entity/LoanApplication.java`
- Create: `src/main/java/com/example/aitest/entity/Customer.java`
- Create: `src/main/java/com/example/aitest/entity/ApplicationDocument.java`
- Create: `src/main/java/com/example/aitest/entity/ApprovalRecord.java`

- [ ] **Step 1: 创建 ApplicationStatus 枚举**

```java
package com.example.aitest.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 贷款申请状态枚举
 */
@Getter
@AllArgsConstructor
public enum ApplicationStatus {
    DRAFT("草稿"),
    INITIAL("待初审"),
    FINAL("待终审"),
    RISK("待风控审批"),
    APPROVED("已批准"),
    REJECTED("已拒绝"),
    PENDING_LOAN("待放款"),
    COMPLETED("已完成");

    private final String description;
}
```

- [ ] **Step 2: 创建 GuaranteeType 枚举**

```java
package com.example.aitest.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 担保方式枚举
 */
@Getter
@AllArgsConstructor
public enum GuaranteeType {
    CREDIT("信用贷款"),
    MORTGAGE("抵押贷款"),
    PLEDGE("质押贷款"),
    GUARANTEE("保证贷款");

    private final String description;
}
```

- [ ] **Step 3: 创建 DocumentType 枚举**

```java
package com.example.aitest.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 材料类型枚举
 */
@Getter
@AllArgsConstructor
public enum DocumentType {
    ID_CARD_FRONT("身份证正面", true),
    ID_CARD_BACK("身份证反面", true),
    INCOME_PROOF("收入证明", true),
    BANK_STATEMENT("银行流水", true),
    PROPERTY_CERT("房产证", false),
    VEHICLE_CERT("车辆证明", false),
    OTHER("其他材料", false);

    private final String description;
    private final boolean required;
}
```

- [ ] **Step 4: 创建 LoanApplication 实体**

```java
package com.example.aitest.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 贷款申请实体
 */
@Data
public class LoanApplication {
    /**
     * 申请 ID
     */
    private Long id;

    /**
     * 申请编号
     */
    private String applicationNo;

    /**
     * 客户 ID
     */
    private Long customerId;

    /**
     * 客户姓名
     */
    private String customerName;

    /**
     * 身份证号
     */
    private String customerIdNo;

    /**
     * 手机号
     */
    private String customerPhone;

    /**
     * 申请金额
     */
    private BigDecimal loanAmount;

    /**
     * 贷款期限 (月)
     */
    private Integer loanTerm;

    /**
     * 贷款用途
     */
    private String loanPurpose;

    /**
     * 担保方式
     */
    private GuaranteeType guaranteeType;

    /**
     * 当前阶段
     */
    private String currentStage;

    /**
     * 状态
     */
    private ApplicationStatus status;

    /**
     * 当前处理人 ID
     */
    private Long assignedTo;

    /**
     * 创建人 ID
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 5: 创建 Customer 实体**

```java
package com.example.aitest.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客户信息实体
 */
@Data
public class Customer {
    /**
     * 客户 ID
     */
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 身份证号
     */
    private String idNo;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 地址
     */
    private String address;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 6: 创建 ApplicationDocument 实体**

```java
package com.example.aitest.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 申请材料实体
 */
@Data
public class ApplicationDocument {
    /**
     * 材料 ID
     */
    private Long id;

    /**
     * 申请 ID
     */
    private Long applicationId;

    /**
     * 材料类型
     */
    private String docType;

    /**
     * 文件名称
     */
    private String docName;

    /**
     * 存储路径
     */
    private String filePath;

    /**
     * 文件大小 (字节)
     */
    private Long fileSize;

    /**
     * 是否必传
     */
    private Boolean isRequired;

    /**
     * 上传人 ID
     */
    private Long uploadedBy;

    /**
     * 上传时间
     */
    private LocalDateTime uploadedAt;
}
```

- [ ] **Step 7: 创建 ApprovalRecord 实体**

```java
package com.example.aitest.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 审批记录实体
 */
@Data
public class ApprovalRecord {
    /**
     * 记录 ID
     */
    private Long id;

    /**
     * 申请 ID
     */
    private Long applicationId;

    /**
     * 审批阶段
     */
    private String stage;

    /**
     * 审批人 ID
     */
    private Long approverId;

    /**
     * 操作
     */
    private String action;

    /**
     * 审批意见
     */
    private String comment;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
```

- [ ] **Step 8: 提交**

```bash
git add src/main/java/com/example/aitest/entity/*.java
git commit -m "feat(application): add entity classes and enums"
```

---

### Task 3: 更新 ResultCode 异常码

**Files:**
- Modify: `src/main/java/com/example/aitest/common/ResultCode.java`

- [ ] **Step 1: 在 ResultCode 中添加申请管理业务异常码**

在 `USERNAME_EXISTS` 之后添加：

```java
// 申请管理业务错误码
APPLICATION_NOT_FOUND(3001, "申请不存在"),
APPLICATION_STATUS_INVALID(3002, "申请状态不允许此操作"),
APPLICATION_NO_PERMISSION(3003, "无权操作此申请"),
CUSTOMER_NOT_FOUND(3004, "客户不存在"),
DOCUMENT_TYPE_INVALID(3005, "材料类型不正确"),
DOCUMENT_REQUIRED(3006, "必传材料未上传"),
FILE_SIZE_EXCEEDED(3007, "文件大小超限"),
FILE_FORMAT_UNSUPPORTED(3008, "文件格式不支持");
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/com/example/aitest/common/ResultCode.java
git commit -m "feat(application): add business error codes for application module"
```

---

## Phase 2: Mapper 层

### Task 4: 创建 LoanApplicationMapper

**Files:**
- Create: `src/main/java/com/example/aitest/mapper/LoanApplicationMapper.java`
- Create: `src/main/resources/mapper/LoanApplicationMapper.xml`

- [ ] **Step 1: 创建 Mapper 接口**

```java
package com.example.aitest.mapper;

import com.example.aitest.entity.LoanApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 贷款申请 Mapper 接口
 */
@Mapper
public interface LoanApplicationMapper {

    /**
     * 根据 ID 查询申请
     */
    LoanApplication findById(@Param("id") Long id);

    /**
     * 根据申请编号查询申请
     */
    LoanApplication findByApplicationNo(@Param("applicationNo") String applicationNo);

    /**
     * 查询申请列表（带筛选）
     */
    List<LoanApplication> findAll(@Param("status") String status,
                                   @Param("guaranteeType") String guaranteeType,
                                   @Param("minAmount") String minAmount,
                                   @Param("maxAmount") String maxAmount,
                                   @Param("keyword") String keyword,
                                   @Param("startDate") String startDate,
                                   @Param("endDate") String endDate);

    /**
     * 插入申请
     */
    int insert(LoanApplication application);

    /**
     * 更新申请
     */
    int update(LoanApplication application);

    /**
     * 删除申请
     */
    int deleteById(@Param("id") Long id);

    /**
     * 更新状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 更新当前阶段
     */
    int updateCurrentStage(@Param("id") Long id, @Param("stage") String stage);
}
```

- [ ] **Step 2: 创建 Mapper XML**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.aitest.mapper.LoanApplicationMapper">

    <resultMap id="BaseResultMap" type="com.example.aitest.entity.LoanApplication">
        <id column="id" property="id"/>
        <result column="application_no" property="applicationNo"/>
        <result column="customer_id" property="customerId"/>
        <result column="customer_name" property="customerName"/>
        <result column="customer_id_no" property="customerIdNo"/>
        <result column="customer_phone" property="customerPhone"/>
        <result column="loan_amount" property="loanAmount"/>
        <result column="loan_term" property="loanTerm"/>
        <result column="loan_purpose" property="loanPurpose"/>
        <result column="guarantee_type" property="guaranteeType"/>
        <result column="current_stage" property="currentStage"/>
        <result column="status" property="status"/>
        <result column="assigned_to" property="assignedTo"/>
        <result column="created_by" property="createdBy"/>
        <result column="created_at" property="createdAt"/>
        <result column="updated_at" property="updatedAt"/>
    </resultMap>

    <select id="findById" resultMap="BaseResultMap">
        SELECT * FROM loan_application WHERE id = #{id}
    </select>

    <select id="findByApplicationNo" resultMap="BaseResultMap">
        SELECT * FROM loan_application WHERE application_no = #{applicationNo}
    </select>

    <select id="findAll" resultMap="BaseResultMap">
        SELECT * FROM loan_application
        <where>
            <if test="status != null and status != ''">
                AND status = #{status}
            </if>
            <if test="guaranteeType != null and guaranteeType != ''">
                AND guarantee_type = #{guaranteeType}
            </if>
            <if test="minAmount != null and minAmount != ''">
                AND loan_amount &gt;= #{minAmount}
            </if>
            <if test="maxAmount != null and maxAmount != ''">
                AND loan_amount &lt;= #{maxAmount}
            </if>
            <if test="keyword != null and keyword != ''">
                AND (customer_name LIKE CONCAT('%', #{keyword}, '%')
                OR customer_phone LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="startDate != null and startDate != ''">
                AND DATE(created_at) &gt;= #{startDate}
            </if>
            <if test="endDate != null and endDate != ''">
                AND DATE(created_at) &lt;= #{endDate}
            </if>
        </where>
        ORDER BY created_at DESC
    </select>

    <insert id="insert" parameterType="com.example.aitest.entity.LoanApplication" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO loan_application (
            application_no, customer_id, customer_name, customer_id_no, customer_phone,
            loan_amount, loan_term, loan_purpose, guarantee_type,
            current_stage, status, assigned_to, created_by
        ) VALUES (
            #{applicationNo}, #{customerId}, #{customerName}, #{customerIdNo}, #{customerPhone},
            #{loanAmount}, #{loanTerm}, #{loanPurpose}, #{guaranteeType},
            #{currentStage}, #{status}, #{assignedTo}, #{createdBy}
        )
    </insert>

    <update id="update" parameterType="com.example.aitest.entity.LoanApplication">
        UPDATE loan_application
        <set>
            <if test="customerName != null">customer_name = #{customerName},</if>
            <if test="customerIdNo != null">customer_id_no = #{customerIdNo},</if>
            <if test="customerPhone != null">customer_phone = #{customerPhone},</if>
            <if test="loanAmount != null">loan_amount = #{loanAmount},</if>
            <if test="loanTerm != null">loan_term = #{loanTerm},</if>
            <if test="loanPurpose != null">loan_purpose = #{loanPurpose},</if>
            <if test="guaranteeType != null">guarantee_type = #{guaranteeType},</if>
            <if test="currentStage != null">current_stage = #{currentStage},</if>
            <if test="status != null">status = #{status},</if>
            <if test="assignedTo != null">assigned_to = #{assignedTo},</if>
        </set>
        updated_at = NOW()
        WHERE id = #{id}
    </update>

    <delete id="deleteById">
        DELETE FROM loan_application WHERE id = #{id}
    </delete>

    <update id="updateStatus">
        UPDATE loan_application SET status = #{status}, updated_at = NOW()
        WHERE id = #{id}
    </update>

    <update id="updateCurrentStage">
        UPDATE loan_application SET current_stage = #{stage}, updated_at = NOW()
        WHERE id = #{id}
    </update>

</mapper>
```

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/example/aitest/mapper/LoanApplicationMapper.java src/main/resources/mapper/LoanApplicationMapper.xml
git commit -m "feat(application): add LoanApplicationMapper"
```

---

### Task 5: 创建 CustomerMapper

**Files:**
- Create: `src/main/java/com/example/aitest/mapper/CustomerMapper.java`
- Create: `src/main/resources/mapper/CustomerMapper.xml`

- [ ] **Step 1: 创建 Mapper 接口**

```java
package com.example.aitest.mapper;

import com.example.aitest.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户信息 Mapper 接口
 */
@Mapper
public interface CustomerMapper {

    /**
     * 根据 ID 查询客户
     */
    Customer findById(@Param("id") Long id);

    /**
     * 根据身份证号查询客户
     */
    Customer findByIdNo(@Param("idNo") String idNo);

    /**
     * 搜索客户列表
     */
    List<Customer> search(@Param("keyword") String keyword);

    /**
     * 插入客户
     */
    int insert(Customer customer);

    /**
     * 更新客户
     */
    int update(Customer customer);
}
```

- [ ] **Step 2: 创建 Mapper XML**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.aitest.mapper.CustomerMapper">

    <resultMap id="BaseResultMap" type="com.example.aitest.entity.Customer">
        <id column="id" property="id"/>
        <result column="name" property="name"/>
        <result column="id_no" property="idNo"/>
        <result column="phone" property="phone"/>
        <result column="email" property="email"/>
        <result column="address" property="address"/>
        <result column="created_at" property="createdAt"/>
        <result column="updated_at" property="updatedAt"/>
    </resultMap>

    <select id="findById" resultMap="BaseResultMap">
        SELECT * FROM customer WHERE id = #{id}
    </select>

    <select id="findByIdNo" resultMap="BaseResultMap">
        SELECT * FROM customer WHERE id_no = #{idNo}
    </select>

    <select id="search" resultMap="BaseResultMap">
        SELECT * FROM customer
        <where>
            <if test="keyword != null and keyword != ''">
                AND (name LIKE CONCAT('%', #{keyword}, '%')
                OR phone LIKE CONCAT('%', #{keyword}, '%')
                OR id_no LIKE CONCAT('%', #{keyword}, '%'))
            </if>
        </where>
        ORDER BY created_at DESC
    </select>

    <insert id="insert" parameterType="com.example.aitest.entity.Customer" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO customer (name, id_no, phone, email, address)
        VALUES (#{name}, #{idNo}, #{phone}, #{email}, #{address})
    </insert>

    <update id="update" parameterType="com.example.aitest.entity.Customer">
        UPDATE customer
        <set>
            <if test="name != null">name = #{name},</if>
            <if test="phone != null">phone = #{phone},</if>
            <if test="email != null">email = #{email},</if>
            <if test="address != null">address = #{address},</if>
        </set>
        updated_at = NOW()
        WHERE id = #{id}
    </update>

</mapper>
```

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/example/aitest/mapper/CustomerMapper.java src/main/resources/mapper/CustomerMapper.xml
git commit -m "feat(application): add CustomerMapper"
```

---

### Task 6: 创建 ApplicationDocumentMapper

**Files:**
- Create: `src/main/java/com/example/aitest/mapper/ApplicationDocumentMapper.java`
- Create: `src/main/resources/mapper/ApplicationDocumentMapper.xml`

- [ ] **Step 1: 创建 Mapper 接口**

```java
package com.example.aitest.mapper;

import com.example.aitest.entity.ApplicationDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 申请材料 Mapper 接口
 */
@Mapper
public interface ApplicationDocumentMapper {

    /**
     * 根据 ID 查询材料
     */
    ApplicationDocument findById(@Param("id") Long id);

    /**
     * 查询申请的所有材料
     */
    List<ApplicationDocument> findByApplicationId(@Param("applicationId") Long applicationId);

    /**
     * 插入材料
     */
    int insert(ApplicationDocument document);

    /**
     * 删除材料
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据申请 ID 删除所有材料
     */
    int deleteByApplicationId(@Param("applicationId") Long applicationId);
}
```

- [ ] **Step 2: 创建 Mapper XML**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.aitest.mapper.ApplicationDocumentMapper">

    <resultMap id="BaseResultMap" type="com.example.aitest.entity.ApplicationDocument">
        <id column="id" property="id"/>
        <result column="application_id" property="applicationId"/>
        <result column="doc_type" property="docType"/>
        <result column="doc_name" property="docName"/>
        <result column="file_path" property="filePath"/>
        <result column="file_size" property="fileSize"/>
        <result column="is_required" property="isRequired"/>
        <result column="uploaded_by" property="uploadedBy"/>
        <result column="uploaded_at" property="uploadedAt"/>
    </resultMap>

    <select id="findById" resultMap="BaseResultMap">
        SELECT * FROM application_document WHERE id = #{id}
    </select>

    <select id="findByApplicationId" resultMap="BaseResultMap">
        SELECT * FROM application_document WHERE application_id = #{applicationId}
        ORDER BY is_required DESC, uploaded_at DESC
    </select>

    <insert id="insert" parameterType="com.example.aitest.entity.ApplicationDocument" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO application_document (
            application_id, doc_type, doc_name, file_path, file_size, is_required, uploaded_by
        ) VALUES (
            #{applicationId}, #{docType}, #{docName}, #{filePath}, #{fileSize}, #{isRequired}, #{uploadedBy}
        )
    </insert>

    <delete id="deleteById">
        DELETE FROM application_document WHERE id = #{id}
    </delete>

    <delete id="deleteByApplicationId">
        DELETE FROM application_document WHERE application_id = #{applicationId}
    </delete>

</mapper>
```

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/example/aitest/mapper/ApplicationDocumentMapper.java src/main/resources/mapper/ApplicationDocumentMapper.xml
git commit -m "feat(application): add ApplicationDocumentMapper"
```

---

### Task 7: 创建 ApprovalRecordMapper

**Files:**
- Create: `src/main/java/com/example/aitest/mapper/ApprovalRecordMapper.java`
- Create: `src/main/resources/mapper/ApprovalRecordMapper.xml`

- [ ] **Step 1: 创建 Mapper 接口**

```java
package com.example.aitest.mapper;

import com.example.aitest.entity.ApprovalRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 审批记录 Mapper 接口
 */
@Mapper
public interface ApprovalRecordMapper {

    /**
     * 根据 ID 查询记录
     */
    ApprovalRecord findById(@Param("id") Long id);

    /**
     * 查询申请的所有审批记录
     */
    List<ApprovalRecord> findByApplicationId(@Param("applicationId") Long applicationId);

    /**
     * 插入记录
     */
    int insert(ApprovalRecord record);
}
```

- [ ] **Step 2: 创建 Mapper XML**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.aitest.mapper.ApprovalRecordMapper">

    <resultMap id="BaseResultMap" type="com.example.aitest.entity.ApprovalRecord">
        <id column="id" property="id"/>
        <result column="application_id" property="applicationId"/>
        <result column="stage" property="stage"/>
        <result column="approver_id" property="approverId"/>
        <result column="action" property="action"/>
        <result column="comment" property="comment"/>
        <result column="created_at" property="createdAt"/>
    </resultMap>

    <select id="findById" resultMap="BaseResultMap">
        SELECT * FROM approval_record WHERE id = #{id}
    </select>

    <select id="findByApplicationId" resultMap="BaseResultMap">
        SELECT * FROM approval_record
        WHERE application_id = #{applicationId}
        ORDER BY created_at ASC
    </select>

    <insert id="insert" parameterType="com.example.aitest.entity.ApprovalRecord" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO approval_record (
            application_id, stage, approver_id, action, comment
        ) VALUES (
            #{applicationId}, #{stage}, #{approverId}, #{action}, #{comment}
        )
    </insert>

</mapper>
```

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/example/aitest/mapper/ApprovalRecordMapper.java src/main/resources/mapper/ApprovalRecordMapper.xml
git commit -m "feat(application): add ApprovalRecordMapper"
```

---

## Phase 3: DTO/VO 层

### Task 8: 创建 DTO 和 VO 类

**Files:**
- Create: `src/main/java/com/example/aitest/dto/ApplicationDTO.java`
- Create: `src/main/java/com/example/aitest/dto/ApplicationVO.java`
- Create: `src/main/java/com/example/aitest/dto/DocumentVO.java`
- Create: `src/main/java/com/example/aitest/dto/ApprovalRecordVO.java`
- Create: `src/main/java/com/example/aitest/dto/CustomerDTO.java`
- Create: `src/main/java/com/example/aitest/dto/CustomerVO.java`

- [ ] **Step 1: 创建 ApplicationDTO**

```java
package com.example.aitest.dto;

import com.example.aitest.entity.GuaranteeType;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 贷款申请请求 DTO
 */
@Data
public class ApplicationDTO {
    private Long id;

    private Long customerId;

    @NotBlank(message = "客户姓名不能为空")
    @Length(max = 50, message = "客户姓名不能超过 50 个字符")
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

    @Length(max = 100, message = "贷款用途不能超过 100 个字符")
    private String loanPurpose;

    @NotNull(message = "担保方式不能为空")
    private GuaranteeType guaranteeType;
}
```

- [ ] **Step 2: 创建 ApplicationVO**

```java
package com.example.aitest.dto;

import com.example.aitest.entity.ApplicationStatus;
import com.example.aitest.entity.GuaranteeType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 贷款申请响应 VO
 */
@Data
@Builder
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
    private String currentHandler;
    private String creatorName;
    private LocalDateTime createdAt;
    private List<DocumentVO> documents;
    private List<ApprovalRecordVO> records;
}
```

- [ ] **Step 3: 创建 DocumentVO**

```java
package com.example.aitest.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 申请材料响应 VO
 */
@Data
@Builder
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

- [ ] **Step 4: 创建 ApprovalRecordVO**

```java
package com.example.aitest.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审批记录响应 VO
 */
@Data
@Builder
public class ApprovalRecordVO {
    private Long id;
    private String stage;
    private String approverName;
    private String action;
    private String comment;
    private LocalDateTime createdAt;
}
```

- [ ] **Step 5: 创建 CustomerDTO**

```java
package com.example.aitest.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

/**
 * 客户信息请求 DTO
 */
@Data
public class CustomerDTO {
    private Long id;

    @Length(max = 50, message = "姓名不能超过 50 个字符")
    private String name;

    @Pattern(regexp = "^\\d{17}[\\dXx]$", message = "身份证号格式不正确")
    private String idNo;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Length(max = 255, message = "地址不能超过 255 个字符")
    private String address;
}
```

- [ ] **Step 6: 创建 CustomerVO**

```java
package com.example.aitest.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户信息响应 VO
 */
@Data
@Builder
public class CustomerVO {
    private Long id;
    private String name;
    private String idNo;
    private String phone;
    private String email;
    private String address;
    private LocalDateTime createdAt;
}
```

- [ ] **Step 7: 提交**

```bash
git add src/main/java/com/example/aitest/dto/*.java
git commit -m "feat(application): add DTO and VO classes"
```

---

## Phase 4: Service 层

### Task 9: 创建 ApplicationService

**Files:**
- Create: `src/main/java/com/example/aitest/service/ApplicationService.java`
- Create: `src/main/java/com/example/aitest/service/impl/ApplicationServiceImpl.java`

- [ ] **Step 1: 创建 Service 接口**

```java
package com.example.aitest.service;

import com.example.aitest.dto.ApplicationDTO;
import com.example.aitest.dto.ApplicationVO;
import com.example.aitest.entity.LoanApplication;

import java.util.List;

/**
 * 贷款申请服务接口
 */
public interface ApplicationService {

    /**
     * 根据 ID 查询申请
     */
    ApplicationVO getById(Long id);

    /**
     * 查询申请列表
     */
    List<ApplicationVO> list(String status, String guaranteeType, String minAmount, String maxAmount,
                             String keyword, String startDate, String endDate);

    /**
     * 创建申请
     */
    ApplicationVO create(ApplicationDTO dto, Long currentUserId);

    /**
     * 更新申请
     */
    ApplicationVO update(Long id, ApplicationDTO dto);

    /**
     * 删除申请
     */
    void delete(Long id);

    /**
     * 提交审批
     */
    void submit(Long id);
}
```

- [ ] **Step 2: 创建 Service 实现**

```java
package com.example.aitest.service.impl;

import com.example.aitest.common.ResultCode;
import com.example.aitest.config.BusinessException;
import com.example.aitest.dto.ApplicationDTO;
import com.example.aitest.dto.ApplicationVO;
import com.example.aitest.entity.ApplicationStatus;
import com.example.aitest.entity.LoanApplication;
import com.example.aitest.mapper.CustomerMapper;
import com.example.aitest.mapper.LoanApplicationMapper;
import com.example.aitest.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 贷款申请服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final LoanApplicationMapper loanApplicationMapper;
    private final CustomerMapper customerMapper;

    // 申请编号生成器（简化实现，生产环境建议使用 Redis）
    private static final AtomicLong sequence = new AtomicLong(1);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public ApplicationVO getById(Long id) {
        LoanApplication application = loanApplicationMapper.findById(id);
        if (application == null) {
            throw new BusinessException(ResultCode.APPLICATION_NOT_FOUND);
        }
        return convertToVO(application);
    }

    @Override
    public List<ApplicationVO> list(String status, String guaranteeType, String minAmount, String maxAmount,
                                    String keyword, String startDate, String endDate) {
        List<LoanApplication> applications = loanApplicationMapper.findAll(
            status, guaranteeType, minAmount, maxAmount, keyword, startDate, endDate);
        return applications.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApplicationVO create(ApplicationDTO dto, Long currentUserId) {
        // 检查身份证号是否已存在
        var existingCustomer = customerMapper.findByIdNo(dto.getCustomerIdNo());
        Long customerId = null;
        if (existingCustomer != null) {
            customerId = existingCustomer.getId();
        }

        // 生成申请编号
        String applicationNo = generateApplicationNo();

        LoanApplication application = new LoanApplication();
        application.setApplicationNo(applicationNo);
        application.setCustomerId(customerId);
        application.setCustomerName(dto.getCustomerName());
        application.setCustomerIdNo(dto.getCustomerIdNo());
        application.setCustomerPhone(dto.getCustomerPhone());
        application.setLoanAmount(dto.getLoanAmount());
        application.setLoanTerm(dto.getLoanTerm());
        application.setLoanPurpose(dto.getLoanPurpose());
        application.setGuaranteeType(dto.getGuaranteeType());
        application.setStatus(ApplicationStatus.DRAFT);
        application.setCurrentStage("DRAFT");
        application.setCreatedBy(currentUserId);

        loanApplicationMapper.insert(application);
        log.info("创建申请成功，applicationNo: {}", applicationNo);

        return convertToVO(application);
    }

    @Override
    @Transactional
    public ApplicationVO update(Long id, ApplicationDTO dto) {
        LoanApplication application = loanApplicationMapper.findById(id);
        if (application == null) {
            throw new BusinessException(ResultCode.APPLICATION_NOT_FOUND);
        }

        // 只有草稿状态可以修改
        if (application.getStatus() != ApplicationStatus.DRAFT) {
            throw new BusinessException(ResultCode.APPLICATION_STATUS_INVALID);
        }

        application.setCustomerName(dto.getCustomerName());
        application.setCustomerIdNo(dto.getCustomerIdNo());
        application.setCustomerPhone(dto.getCustomerPhone());
        application.setLoanAmount(dto.getLoanAmount());
        application.setLoanTerm(dto.getLoanTerm());
        application.setLoanPurpose(dto.getLoanPurpose());
        application.setGuaranteeType(dto.getGuaranteeType());

        loanApplicationMapper.update(application);
        log.info("更新申请成功，id: {}", id);

        return convertToVO(application);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        LoanApplication application = loanApplicationMapper.findById(id);
        if (application == null) {
            throw new BusinessException(ResultCode.APPLICATION_NOT_FOUND);
        }

        // 只有草稿状态可以删除
        if (application.getStatus() != ApplicationStatus.DRAFT) {
            throw new BusinessException(ResultCode.APPLICATION_STATUS_INVALID);
        }

        loanApplicationMapper.deleteById(id);
        log.info("删除申请成功，id: {}", id);
    }

    @Override
    @Transactional
    public void submit(Long id) {
        LoanApplication application = loanApplicationMapper.findById(id);
        if (application == null) {
            throw new BusinessException(ResultCode.APPLICATION_NOT_FOUND);
        }

        // 只有草稿状态可以提交
        if (application.getStatus() != ApplicationStatus.DRAFT) {
            throw new BusinessException(ResultCode.APPLICATION_STATUS_INVALID);
        }

        // 根据金额决定审批流程
        if (application.getLoanAmount().compareTo(new java.math.BigDecimal("100000")) >= 0) {
            // 金额 >= 10 万，需要风控审批
            application.setStatus(ApplicationStatus.INITIAL);
            application.setCurrentStage("INITIAL");
        } else {
            // 金额 < 10 万，只需初审和终审
            application.setStatus(ApplicationStatus.INITIAL);
            application.setCurrentStage("INITIAL");
        }

        loanApplicationMapper.update(application);
        log.info("提交申请成功，id: {}, nextStage: {}", id, application.getCurrentStage());
    }

    /**
     * 生成申请编号：A + YYYYMMDD + 序号
     */
    private String generateApplicationNo() {
        String date = LocalDateTime.now().format(DATE_FORMAT);
        long seq = sequence.getAndIncrement();
        return "A" + date + String.format("%04d", seq);
    }

    /**
     * 转换为 VO
     */
    private ApplicationVO convertToVO(LoanApplication application) {
        return ApplicationVO.builder()
            .id(application.getId())
            .applicationNo(application.getApplicationNo())
            .customerName(application.getCustomerName())
            .customerIdNo(application.getCustomerIdNo())
            .customerPhone(application.getCustomerPhone())
            .loanAmount(application.getLoanAmount())
            .loanTerm(application.getLoanTerm())
            .loanPurpose(application.getLoanPurpose())
            .guaranteeType(application.getGuaranteeType())
            .status(application.getStatus())
            .currentStage(application.getCurrentStage())
            .createdAt(application.getCreatedAt())
            .build();
    }
}
```

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/example/aitest/service/ApplicationService.java src/main/java/com/example/aitest/service/impl/ApplicationServiceImpl.java
git commit -m "feat(application): add ApplicationService"
```

---

### Task 10: 创建 CustomerService

**Files:**
- Create: `src/main/java/com/example/aitest/service/CustomerService.java`
- Create: `src/main/java/com/example/aitest/service/impl/CustomerServiceImpl.java`

- [ ] **Step 1: 创建 Service 接口**

```java
package com.example.aitest.service;

import com.example.aitest.dto.CustomerDTO;
import com.example.aitest.dto.CustomerVO;
import com.example.aitest.entity.Customer;

import java.util.List;

/**
 * 客户服务接口
 */
public interface CustomerService {

    /**
     * 根据 ID 查询客户
     */
    CustomerVO getById(Long id);

    /**
     * 搜索客户列表
     */
    List<CustomerVO> search(String keyword);

    /**
     * 根据身份证号查询客户
     */
    Customer getBy
