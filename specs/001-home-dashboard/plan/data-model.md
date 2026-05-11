# Data Model Design: 首页工作台功能

**Feature**: 首页工作台 (001-home-dashboard)

**Date**: 2026-03-24

---

## Entity Definitions

### 1. Announcement (公告)

**描述**: 系统发布的通知信息

| 字段 | 类型 | 必填 | 说明 | 约束 |
|------|------|------|------|------|
| id | BIGINT | 是 | 主键 | AUTO_INCREMENT |
| title | VARCHAR(200) | 是 | 公告标题 | NOT NULL |
| content | TEXT | 是 | 公告内容 | NOT NULL |
| status | TINYINT | 是 | 状态：0-草稿 1-发布 | DEFAULT 1 |
| created_by | BIGINT | 否 | 创建人 ID | FOREIGN KEY -> user(id) |
| created_at | DATETIME | 是 | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| updated_at | DATETIME | 是 | 更新时间 | ON UPDATE CURRENT_TIMESTAMP |

**索引**:
- `PRIMARY KEY (id)`
- `INDEX idx_status_created_at (status, created_at)` - 用于查询已发布的最新公告

---

### 2. TodoStatsDTO (待办统计 DTO)

**描述**: 待办任务数量统计数据传输对象

| 字段 | 类型 | 说明 |
|------|------|------|
| application | Integer | 待审批申请数量 |
| approval | Integer | 待审批数量 |
| contract | Integer | 待签署合同数量 |
| loan | Integer | 待放款数量 |
| repayment | Integer | 待处理还款数量 |
| warning | Integer | 预警数量 |
| postloan | Integer | 贷后任务数量 |
| other | Integer | 其他任务数量 |

**注意**: 此为 DTO，非持久化实体

---

## Relationships

```
┌─────────────┐
│   User      │
│  (创建人)    │
└──────┬──────┘
       │ 1:N
       ▼
┌─────────────┐
│ Announcement│
│  (公告)     │
└─────────────┘
```

---

## SQL Queries

### 查询最新 5 条已发布公告

```sql
SELECT id, title, created_at
FROM announcement
WHERE status = 1
ORDER BY created_at DESC
LIMIT 5;
```

### 查询公告详情

```sql
SELECT id, title, content, created_by, created_at
FROM announcement
WHERE id = ? AND status = 1;
```

### 待办统计查询模板 (待实现)

```sql
-- 待审批申请数量
SELECT COUNT(*) FROM application WHERE status = 'PENDING_AUDIT';

-- 待审批数量
SELECT COUNT(*) FROM approval WHERE status = 'PENDING';

-- 待签署合同数量
SELECT COUNT(*) FROM contract WHERE status = 'PENDING_SIGN';

-- 待放款数量
SELECT COUNT(*) FROM loan WHERE status = 'PENDING_LEND';

-- 待处理还款数量
SELECT COUNT(*) FROM repayment WHERE status = 'PENDING_PROCESS';

-- 预警数量
SELECT COUNT(*) FROM warning WHERE status = 'UNHANDLED';

-- 贷后任务数量
SELECT COUNT(*) FROM postloan_task WHERE status = 'PENDING';
```

---

## MyBatis Mapper

### AnnouncementMapper.java

```java
@Mapper
public interface AnnouncementMapper {

    /**
     * 查询最新已发布公告列表
     */
    List<Announcement> selectLatestAnnouncements(@Param("limit") int limit);

    /**
     * 查询公告详情
     */
    Announcement selectById(@Param("id") Long id);
}
```

### AnnouncementMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.aitest.mapper.AnnouncementMapper">

    <resultMap id="AnnouncementResult" type="Announcement">
        <id property="id" column="id"/>
        <result property="title" column="title"/>
        <result property="content" column="content"/>
        <result property="status" column="status"/>
        <result property="createdBy" column="created_by"/>
        <result property="createdAt" column="created_at"/>
        <result property="updatedAt" column="updated_at"/>
    </resultMap>

    <!-- 查询最新已发布公告 -->
    <select id="selectLatestAnnouncements" resultMap="AnnouncementResult">
        SELECT id, title, created_at
        FROM announcement
        WHERE status = 1
        ORDER BY created_at DESC
        LIMIT #{limit}
    </select>

    <!-- 查询公告详情 -->
    <select id="selectById" resultMap="AnnouncementResult">
        SELECT id, title, content, created_by, created_at
        FROM announcement
        WHERE id = #{id} AND status = 1
    </select>

</mapper>
```

---

## API Response DTO

### AnnouncementListResponse

```java
public class AnnouncementListResponse {
    private Long id;
    private String title;
    private String createdAt;
    // getters/setters
}
```

### AnnouncementDetailResponse

```java
public class AnnouncementDetailResponse {
    private Long id;
    private String title;
    private String content;
    private String createdBy;
    private String createdAt;
    // getters/setters
}
```

---

## Validation Rules

### Announcement

| 字段 | 验证规则 |
|------|----------|
| title | 非空，长度 1-200 |
| content | 非空 |
| status | 0 或 1 |
| created_by | 有效的用户 ID |

---

## State Transitions

公告状态流转:

```
[草稿 status=0] ──发布──> [已发布 status=1]
```

---

## Notes

1. 待办统计 DTO 为虚拟实体，实际数据来源于各业务模块
2. 第一阶段可使用静态数据，后续逐步集成真实业务数据
3. 公告表假设已存在，需确认字段与实际数据库一致
