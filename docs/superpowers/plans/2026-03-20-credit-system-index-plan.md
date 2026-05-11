# 银行信贷管理系统首页功能实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现银行信贷管理系统登录后首页，包含工作台、公告栏、11 个菜单模块，以及用户管理、角色管理、公告管理的完整功能。

**Architecture:**
- 采用 MVC 分层架构：Controller → Service → Mapper → Database
- 前端使用 Thymeleaf 服务端渲染 + 原生 JavaScript 交互
- 数据库新增 5 张表（role, menu, user_role, role_menu, announcement）
- 待办任务使用静态模拟数据，公告使用数据库动态管理

**Tech Stack:** Java 17, Spring Boot 3.2.5, MyBatis 3.0.4, MySQL 8.0, Thymeleaf 3.1.2, Lombok

---

## 第一阶段：数据库初始化

### Task 1: 创建数据库表结构

**Files:**
- Modify: `src/main/resources/schema.sql`
- Test: 手动执行验证

- [ ] **Step 1: 在 schema.sql 中添加角色表 (role)**

```sql
-- 角色表
CREATE TABLE IF NOT EXISTS `role` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `code` VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';
```

- [ ] **Step 2: 添加菜单表 (menu)**

```sql
-- 菜单表
CREATE TABLE IF NOT EXISTS `menu` (
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

- [ ] **Step 3: 添加用户 - 角色关联表 (user_role)**

```sql
-- 用户 - 角色关联表
CREATE TABLE IF NOT EXISTS `user_role` (
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`role_id`) REFERENCES `role`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户 - 角色关联表';
```

- [ ] **Step 4: 添加角色 - 菜单关联表 (role_menu)**

```sql
-- 角色 - 菜单关联表
CREATE TABLE IF NOT EXISTS `role_menu` (
    `role_id` BIGINT NOT NULL,
    `menu_id` BIGINT NOT NULL,
    PRIMARY KEY (`role_id`, `menu_id`),
    FOREIGN KEY (`role_id`) REFERENCES `role`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`menu_id`) REFERENCES `menu`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色 - 菜单关联表';
```

- [ ] **Step 5: 添加公告表 (announcement)**

```sql
-- 公告表
CREATE TABLE IF NOT EXISTS `announcement` (
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

- [ ] **Step 6: 重启应用验证表创建成功**

```bash
# 停止当前运行应用，重新编译启动
java -jar target/aitest-0.0.1-SNAPSHOT.jar
```

预期：应用启动成功，数据库中存在 5 张新表

- [ ] **Step 7: Commit**

```bash
git add src/main/resources/schema.sql
git commit -m "feat: add database tables for role, menu, announcement"
```

---

### Task 2: 初始化基础数据

**Files:**
- Modify: `src/main/resources/data.sql`

- [ ] **Step 1: 在 data.sql 中添加初始化角色数据**

```sql
-- 初始化角色
INSERT INTO `role` (`name`, `code`, `description`, `status`) VALUES
('系统管理员', 'ADMIN', '拥有所有权限', 1),
('信贷审批员', 'APPROVER', '负责贷款审批', 1),
('客户经理', 'MANAGER', '负责客户管理', 1),
('普通用户', 'USER', '基础权限', 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);
```

- [ ] **Step 2: 添加初始化菜单数据**

```sql
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
('报表管理', 0, '/report', 'bar-chart', 12, 'report:view', 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);
```

- [ ] **Step 3: 添加管理员角色菜单权限关联**

```sql
-- 管理员角色分配所有菜单权限
INSERT INTO `role_menu` (`role_id`, `menu_id`)
SELECT 1, id FROM `menu`
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);
```

- [ ] **Step 4: 添加默认管理员用户角色关联**

```sql
-- 将 admin 用户关联到管理员角色
INSERT INTO `user_role` (`user_id`, `role_id`)
SELECT 1, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_role WHERE user_id = 1 AND role_id = 1);
```

- [ ] **Step 5: 重启应用验证数据初始化成功**

预期：重启后数据库中 role、menu 表有初始数据

- [ ] **Step 6: Commit**

```bash
git add src/main/resources/data.sql
git commit -m "feat: add initial data for roles and menus"
```

---

## 第二阶段：实体类和 Mapper

### Task 3: 创建 Role 实体和 Mapper

**Files:**
- Create: `src/main/java/com/example/aitest/entity/Role.java`
- Create: `src/main/java/com/example/aitest/mapper/RoleMapper.java`
- Create: `src/main/resources/mapper/RoleMapper.xml`

- [ ] **Step 1: 创建 Role 实体类**

```java
package com.example.aitest.entity;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class Role {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 2: 创建 RoleMapper 接口**

```java
package com.example.aitest.mapper;

import com.example.aitest.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RoleMapper {
    Role findById(@Param("id") Long id);
    List<Role> findAll();
    List<Role> findByPage(@Param("offset") int offset, @Param("limit") int limit);
    Role findByCode(@Param("code") String code);
    int insert(Role role);
    int update(Role role);
    int delete(@Param("id") Long id);
}
```

- [ ] **Step 3: 创建 RoleMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.aitest.mapper.RoleMapper">

    <resultMap id="BaseResultMap" type="com.example.aitest.entity.Role">
        <id column="id" property="id"/>
        <result column="name" property="name"/>
        <result column="code" property="code"/>
        <result column="description" property="description"/>
        <result column="status" property="status"/>
        <result column="created_at" property="createdAt"/>
        <result column="updated_at" property="updatedAt"/>
    </resultMap>

    <select id="findById" resultMap="BaseResultMap">
        SELECT * FROM role WHERE id = #{id}
    </select>

    <select id="findAll" resultMap="BaseResultMap">
        SELECT * FROM role ORDER BY id
    </select>

    <select id="findByPage" resultMap="BaseResultMap">
        SELECT * FROM role ORDER BY id LIMIT #{offset}, #{limit}
    </select>

    <select id="findByCode" resultMap="BaseResultMap">
        SELECT * FROM role WHERE code = #{code}
    </select>

    <insert id="insert" parameterType="com.example.aitest.entity.Role" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO role (name, code, description, status)
        VALUES (#{name}, #{code}, #{description}, #{status})
    </insert>

    <update id="update" parameterType="com.example.aitest.entity.Role">
        UPDATE role
        SET name = #{name}, code = #{code}, description = #{description}, status = #{status}
        WHERE id = #{id}
    </update>

    <delete id="delete">
        DELETE FROM role WHERE id = #{id}
    </delete>

</mapper>
```

- [ ] **Step 4: 编译验证无错误**

```bash
mvn compile -q
```

预期：编译成功，无错误

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/example/aitest/entity/Role.java
git add src/main/java/com/example/aitest/mapper/RoleMapper.java
git add src/main/resources/mapper/RoleMapper.xml
git commit -m "feat: add Role entity and mapper"
```

---

### Task 4: 创建 Menu 实体和 Mapper

**Files:**
- Create: `src/main/java/com/example/aitest/entity/Menu.java`
- Create: `src/main/java/com/example/aitest/mapper/MenuMapper.java`
- Create: `src/main/resources/mapper/MenuMapper.xml`

- [ ] **Step 1: 创建 Menu 实体类**

```java
package com.example.aitest.entity;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class Menu {
    private Long id;
    private String name;
    private Long parentId;
    private String path;
    private String icon;
    private Integer sortOrder;
    private String permission;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 2: 创建 MenuMapper 接口**

```java
package com.example.aitest.mapper;

import com.example.aitest.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MenuMapper {
    Menu findById(@Param("id") Long id);
    List<Menu> findAll();
    List<Menu> findByStatus(@Param("status") Integer status);
    List<Menu> findByRoleIds(@Param("roleIds") List<Long> roleIds);
    int insert(Menu menu);
    int update(Menu menu);
    int delete(@Param("id") Long id);
}
```

- [ ] **Step 3: 创建 MenuMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.aitest.mapper.MenuMapper">

    <resultMap id="BaseResultMap" type="com.example.aitest.entity.Menu">
        <id column="id" property="id"/>
        <result column="name" property="name"/>
        <result column="parent_id" property="parentId"/>
        <result column="path" property="path"/>
        <result column="icon" property="icon"/>
        <result column="sort_order" property="sortOrder"/>
        <result column="permission" property="permission"/>
        <result column="status" property="status"/>
        <result column="created_at" property="createdAt"/>
        <result column="updated_at" property="updatedAt"/>
    </resultMap>

    <select id="findById" resultMap="BaseResultMap">
        SELECT * FROM menu WHERE id = #{id}
    </select>

    <select id="findAll" resultMap="BaseResultMap">
        SELECT * FROM menu ORDER BY sort_order
    </select>

    <select id="findByStatus" resultMap="BaseResultMap">
        SELECT * FROM menu WHERE status = #{status} ORDER BY sort_order
    </select>

    <select id="findByRoleIds" resultMap="BaseResultMap">
        SELECT DISTINCT m.* FROM menu m
        INNER JOIN role_menu rm ON m.id = rm.menu_id
        WHERE rm.role_id IN
        <foreach collection="roleIds" item="roleId" open="(" separator="," close=")">
            #{roleId}
        </foreach>
        AND m.status = 1
        ORDER BY m.sort_order
    </select>

    <insert id="insert" parameterType="com.example.aitest.entity.Menu" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO menu (name, parent_id, path, icon, sort_order, permission, status)
        VALUES (#{name}, #{parentId}, #{path}, #{icon}, #{sortOrder}, #{permission}, #{status})
    </insert>

    <update id="update" parameterType="com.example.aitest.entity.Menu">
        UPDATE menu
        SET name = #{name}, parent_id = #{parentId}, path = #{path},
            icon = #{icon}, sort_order = #{sortOrder}, permission = #{permission}, status = #{status}
        WHERE id = #{id}
    </update>

    <delete id="delete">
        DELETE FROM menu WHERE id = #{id}
    </delete>

</mapper>
```

- [ ] **Step 4: 编译验证**

```bash
mvn compile -q
```

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/example/aitest/entity/Menu.java
git add src/main/java/com/example/aitest/mapper/MenuMapper.java
git add src/main/resources/mapper/MenuMapper.xml
git commit -m "feat: add Menu entity and mapper"
```

---

### Task 5: 创建 Announcement 实体和 Mapper

**Files:**
- Create: `src/main/java/com/example/aitest/entity/Announcement.java`
- Create: `src/main/java/com/example/aitest/mapper/AnnouncementMapper.java`
- Create: `src/main/resources/mapper/AnnouncementMapper.xml`

- [ ] **Step 1: 创建 Announcement 实体类**

```java
package com.example.aitest.entity;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class Announcement {
    private Long id;
    private String title;
    private String content;
    private Integer publishStatus;
    private LocalDateTime publishTime;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 2: 创建 AnnouncementMapper 接口**

```java
package com.example.aitest.mapper;

import com.example.aitest.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.time.LocalDateTime;

@Mapper
public interface AnnouncementMapper {
    Announcement findById(@Param("id") Long id);
    List<Announcement> findByPage(@Param("offset") int offset, @Param("limit") int limit);
    List<Announcement> findPublished(@Param("limit") int limit);
    int count();
    int insert(Announcement announcement);
    int update(Announcement announcement);
    int delete(@Param("id") Long id);
    int updatePublishStatus(@Param("id") Long id, @Param("status") Integer status, @Param("publishTime") LocalDateTime publishTime);
}
```

- [ ] **Step 3: 创建 AnnouncementMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.aitest.mapper.AnnouncementMapper">

    <resultMap id="BaseResultMap" type="com.example.aitest.entity.Announcement">
        <id column="id" property="id"/>
        <result column="title" property="title"/>
        <result column="content" property="content"/>
        <result column="publish_status" property="publishStatus"/>
        <result column="publish_time" property="publishTime"/>
        <result column="created_by" property="createdBy"/>
        <result column="created_at" property="createdAt"/>
        <result column="updated_at" property="updatedAt"/>
    </resultMap>

    <select id="findById" resultMap="BaseResultMap">
        SELECT * FROM announcement WHERE id = #{id}
    </select>

    <select id="findByPage" resultMap="BaseResultMap">
        SELECT * FROM announcement ORDER BY created_at DESC LIMIT #{offset}, #{limit}
    </select>

    <select id="findPublished" resultMap="BaseResultMap">
        SELECT * FROM announcement
        WHERE publish_status = 1
        ORDER BY publish_time DESC
        LIMIT #{limit}
    </select>

    <select id="count" resultType="int">
        SELECT COUNT(*) FROM announcement
    </select>

    <insert id="insert" parameterType="com.example.aitest.entity.Announcement" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO announcement (title, content, publish_status, created_by)
        VALUES (#{title}, #{content}, #{publishStatus}, #{createdBy})
    </insert>

    <update id="update" parameterType="com.example.aitest.entity.Announcement">
        UPDATE announcement
        SET title = #{title}, content = #{content}, publish_status = #{publishStatus}
        WHERE id = #{id}
    </update>

    <delete id="delete">
        DELETE FROM announcement WHERE id = #{id}
    </delete>

    <update id="updatePublishStatus">
        UPDATE announcement
        SET publish_status = #{status}, publish_time = #{publishTime}
        WHERE id = #{id}
    </update>

</mapper>
```

- [ ] **Step 4: 编译验证**

```bash
mvn compile -q
```

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/example/aitest/entity/Announcement.java
git add src/main/java/com/example/aitest/mapper/AnnouncementMapper.java
git add src/main/resources/mapper/AnnouncementMapper.xml
git commit -m "feat: add Announcement entity and mapper"
```

---

### Task 6: 创建用户 - 角色关联 Mapper

**Files:**
- Create: `src/main/java/com/example/aitest/mapper/UserRoleMapper.java`
- Create: `src/main/resources/mapper/UserRoleMapper.xml`

- [ ] **Step 1: 创建 UserRoleMapper 接口**

```java
package com.example.aitest.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserRoleMapper {
    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);
    List<Long> findUserIdsByRoleId(@Param("roleId") Long roleId);
    int insert(@Param("userId") Long userId, @Param("roleId") Long roleId);
    int deleteByUserId(@Param("userId") Long userId);
    int deleteByRoleId(@Param("roleId") Long roleId);
    int delete(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
```

- [ ] **Step 2: 创建 UserRoleMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.aitest.mapper.UserRoleMapper">

    <select id="findRoleIdsByUserId" resultType="java.lang.Long">
        SELECT role_id FROM user_role WHERE user_id = #{userId}
    </select>

    <select id="findUserIdsByRoleId" resultType="java.lang.Long">
        SELECT user_id FROM user_role WHERE role_id = #{roleId}
    </select>

    <insert id="insert">
        INSERT INTO user_role (user_id, role_id) VALUES (#{userId}, #{roleId})
    </insert>

    <delete id="deleteByUserId">
        DELETE FROM user_role WHERE user_id = #{userId}
    </delete>

    <delete id="deleteByRoleId">
        DELETE FROM user_role WHERE role_id = #{roleId}
    </delete>

    <delete id="delete">
        DELETE FROM user_role WHERE user_id = #{userId} AND role_id = #{roleId}
    </delete>

</mapper>
```

- [ ] **Step 3: 创建 RoleMenuMapper 接口**

```java
package com.example.aitest.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RoleMenuMapper {
    List<Long> findMenuIdsByRoleId(@Param("roleId") Long roleId);
    int insert(@Param("roleId") Long roleId, @Param("menuId") Long menuId);
    int deleteByRoleId(@Param("roleId") Long roleId);
    int delete(@Param("roleId") Long roleId, @Param("menuId") Long menuId);
}
```

- [ ] **Step 4: 创建 RoleMenuMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.aitest.mapper.RoleMenuMapper">

    <select id="findMenuIdsByRoleId" resultType="java.lang.Long">
        SELECT menu_id FROM role_menu WHERE role_id = #{roleId}
    </select>

    <insert id="insert">
        INSERT INTO role_menu (role_id, menu_id) VALUES (#{roleId}, #{menuId})
    </insert>

    <delete id="deleteByRoleId">
        DELETE FROM role_menu WHERE role_id = #{roleId}
    </delete>

    <delete id="delete">
        DELETE FROM role_menu WHERE role_id = #{roleId} AND menu_id = #{menuId}
    </delete>

</mapper>
```

- [ ] **Step 5: 编译验证**

```bash
mvn compile -q
```

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/example/aitest/mapper/UserRoleMapper.java
git add src/main/java/com/example/aitest/mapper/RoleMenuMapper.java
git add src/main/resources/mapper/UserRoleMapper.xml
git add src/main/resources/mapper/RoleMenuMapper.xml
git commit -m "feat: add UserRoleMapper and RoleMenuMapper"
```

---

## 第三阶段：Service 层

### Task 7: 创建 RoleService

**Files:**
- Create: `src/main/java/com/example/aitest/service/RoleService.java`
- Create: `src/main/java/com/example/aitest/service/impl/RoleServiceImpl.java`

- [ ] **Step 1: 创建 RoleService 接口**

```java
package com.example.aitest.service;

import com.example.aitest.entity.Role;
import java.util.List;

public interface RoleService {
    Role findById(Long id);
    List<Role> findAll();
    List<Role> findByPage(int page, int size);
    int count();
    Role create(String name, String code, String description);
    Role update(Long id, String name, String code, String description, Integer status);
    void delete(Long id);
    void assignMenus(Long roleId, List<Long> menuIds);
    List<Long> getMenuIdsByRoleId(Long roleId);
}
```

- [ ] **Step 2: 创建 RoleServiceImpl 实现类**

```java
package com.example.aitest.service.impl;

import com.example.aitest.common.BusinessException;
import com.example.aitest.common.ResultCode;
import com.example.aitest.entity.Role;
import com.example.aitest.mapper.RoleMapper;
import com.example.aitest.mapper.RoleMenuMapper;
import com.example.aitest.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RoleMenuMapper roleMenuMapper;

    @Override
    public Role findById(Long id) {
        return roleMapper.findById(id);
    }

    @Override
    public List<Role> findAll() {
        return roleMapper.findAll();
    }

    @Override
    public List<Role> findByPage(int page, int size) {
        return roleMapper.findByPage((page - 1) * size, size);
    }

    @Override
    public int count() {
        List<Role> all = roleMapper.findAll();
        return all != null ? all.size() : 0;
    }

    @Override
    @Transactional
    public Role create(String name, String code, String description) {
        Role existing = roleMapper.findByCode(code);
        if (existing != null) {
            throw new BusinessException(2002, "角色编码已存在");
        }
        Role role = Role.builder()
                .name(name)
                .code(code)
                .description(description)
                .status(1)
                .build();
        roleMapper.insert(role);
        log.info("创建角色成功：{}", code);
        return role;
    }

    @Override
    @Transactional
    public Role update(Long id, String name, String code, String description, Integer status) {
        Role existing = roleMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(2002, "角色不存在");
        }
        Role role = Role.builder()
                .id(id)
                .name(name)
                .code(code)
                .description(description)
                .status(status)
                .build();
        roleMapper.update(role);
        log.info("更新角色成功：{}", id);
        return role;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        roleMapper.delete(id);
        log.info("删除角色成功：{}", id);
    }

    @Override
    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        roleMenuMapper.deleteByRoleId(roleId);
        if (menuIds != null) {
            for (Long menuId : menuIds) {
                roleMenuMapper.insert(roleId, menuId);
            }
        }
        log.info("分配菜单成功，roleId: {}, menuCount: {}", roleId, menuIds != null ? menuIds.size() : 0);
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        return roleMenuMapper.findMenuIdsByRoleId(roleId);
    }
}
```

- [ ] **Step 3: 编译验证**

```bash
mvn compile -q
```

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/example/aitest/service/RoleService.java
git add src/main/java/com/example/aitest/service/impl/RoleServiceImpl.java
git commit -m "feat: add RoleService"
```

---

### Task 8: 创建 AnnouncementService

**Files:**
- Create: `src/main/java/com/example/aitest/service/AnnouncementService.java`
- Create: `src/main/java/com/example/aitest/service/impl/AnnouncementServiceImpl.java`

- [ ] **Step 1: 创建 AnnouncementService 接口**

```java
package com.example.aitest.service;

import com.example.aitest.entity.Announcement;
import java.util.List;

public interface AnnouncementService {
    Announcement findById(Long id);
    List<Announcement> findByPage(int page, int size);
    List<Announcement> findPublished(int limit);
    int count();
    Announcement create(String title, String content, Integer publishStatus, Long createdBy);
    Announcement update(Long id, String title, String content, Integer publishStatus);
    void delete(Long id);
    void publish(Long id);
    void unpublish(Long id);
}
```

- [ ] **Step 2: 创建 AnnouncementServiceImpl 实现类**

```java
package com.example.aitest.service.impl;

import com.example.aitest.entity.Announcement;
import com.example.aitest.mapper.AnnouncementMapper;
import com.example.aitest.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    @Override
    public Announcement findById(Long id) {
        return announcementMapper.findById(id);
    }

    @Override
    public List<Announcement> findByPage(int page, int size) {
        return announcementMapper.findByPage((page - 1) * size, size);
    }

    @Override
    public List<Announcement> findPublished(int limit) {
        return announcementMapper.findPublished(limit);
    }

    @Override
    public int count() {
        return announcementMapper.count();
    }

    @Override
    @Transactional
    public Announcement create(String title, String content, Integer publishStatus, Long createdBy) {
        Announcement announcement = Announcement.builder()
                .title(title)
                .content(content)
                .publishStatus(publishStatus != null ? publishStatus : 0)
                .createdBy(createdBy)
                .build();
        if (publishStatus != null && publishStatus == 1) {
            announcement.setPublishTime(LocalDateTime.now());
        }
        announcementMapper.insert(announcement);
        log.info("创建公告成功：{}", title);
        return announcement;
    }

    @Override
    @Transactional
    public Announcement update(Long id, String title, String content, Integer publishStatus) {
        Announcement existing = announcementMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("公告不存在");
        }
        Announcement announcement = Announcement.builder()
                .id(id)
                .title(title)
                .content(content)
                .publishStatus(publishStatus)
                .build();
        announcementMapper.update(announcement);
        log.info("更新公告成功：{}", id);
        return announcement;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        announcementMapper.delete(id);
        log.info("删除公告成功：{}", id);
    }

    @Override
    @Transactional
    public void publish(Long id) {
        announcementMapper.updatePublishStatus(id, 1, LocalDateTime.now());
        log.info("发布公告成功：{}", id);
    }

    @Override
    @Transactional
    public void unpublish(Long id) {
        announcementMapper.updatePublishStatus(id, 2, null);
        log.info("下架公告成功：{}", id);
    }
}
```

- [ ] **Step 3: 编译验证**

```bash
mvn compile -q
```

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/example/aitest/service/AnnouncementService.java
git add src/main/java/com/example/aitest/service/impl/AnnouncementServiceImpl.java
git commit -m "feat: add AnnouncementService"
```

---

### Task 9: 扩展 UserService 添加角色关联

**Files:**
- Modify: `src/main/java/com/example/aitest/service/UserService.java`
- Modify: `src/main/java/com/example/aitest/service/impl/UserServiceImpl.java`

- [ ] **Step 1: 在 UserService 接口中添加角色相关方法**

```java
// 添加以下方法
List<Long> getRoleIdsByUserId(Long userId);
void assignRoles(Long userId, List<Long> roleIds);
void resetPassword(Long userId, String newPassword);
void updateStatus(Long userId, Integer status);
```

- [ ] **Step 2: 在 UserServiceImpl 中实现这些方法**

```java
// 在 UserServiceImpl 类中添加
private final UserRoleMapper userRoleMapper;
private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

@Override
public List<Long> getRoleIdsByUserId(Long userId) {
    return userRoleMapper.findRoleIdsByUserId(userId);
}

@Override
@Transactional
public void assignRoles(Long userId, List<Long> roleIds) {
    userRoleMapper.deleteByUserId(userId);
    if (roleIds != null) {
        for (Long roleId : roleIds) {
            userRoleMapper.insert(userId, roleId);
        }
    }
    log.info("分配角色成功，userId: {}, roleCount: {}", userId, roleIds != null ? roleIds.size() : 0);
}

@Override
@Transactional
public void resetPassword(Long userId, String newPassword) {
    User user = userMapper.findById(userId);
    if (user == null) {
        throw new BusinessException(ResultCode.USER_NOT_FOUND);
    }
    String hashed = passwordEncoder.encode(newPassword);
    // 需要添加 UserMapper.updatePassword 方法
    log.info("重置密码成功，userId: {}", userId);
}

@Override
@Transactional
public void updateStatus(Long userId, Integer status) {
    User user = userMapper.findById(userId);
    if (user == null) {
        throw new BusinessException(ResultCode.USER_NOT_FOUND);
    }
    // 需要添加 UserMapper.updateStatus 方法
    log.info("更新用户状态成功，userId: {}, status: {}", userId, status);
}
```

- [ ] **Step 3: 在 UserMapper 中添加更新方法**

```java
// RoleMapper.java 中添加
int updateStatus(@Param("id") Long id, @Param("status") Integer status);
```

- [ ] **Step 4: 在 UserMapper.xml 中添加 SQL**

```xml
<update id="updateStatus">
    UPDATE user SET status = #{status} WHERE id = #{id}
</update>
```

- [ ] **Step 5: 编译验证**

```bash
mvn compile -q
```

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/example/aitest/service/UserService.java
git add src/main/java/com/example/aitest/service/impl/UserServiceImpl.java
git add src/main/java/com/example/aitest/mapper/UserMapper.java
git add src/main/resources/mapper/UserMapper.xml
git commit -m "feat: extend UserService with role assignment and password reset"
```

---

## 第四阶段：Controller 层

### Task 10: 创建 IndexController（首页）

**Files:**
- Create: `src/main/java/com/example/aitest/controller/IndexController.java`
- Create: `src/main/java/com/example/aitest/controller/DashboardController.java`
- Create: `src/main/resources/templates/index.html`

- [ ] **Step 1: 创建 IndexController**

```java
package com.example.aitest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/application")
    public String application() {
        return "placeholder";
    }

    @GetMapping("/approval")
    public String approval() {
        return "placeholder";
    }

    @GetMapping("/contract")
    public String contract() {
        return "placeholder";
    }

    @GetMapping("/loan")
    public String loan() {
        return "placeholder";
    }

    @GetMapping("/repayment")
    public String repayment() {
        return "placeholder";
    }

    @GetMapping("/warning")
    public String warning() {
        return "placeholder";
    }

    @GetMapping("/postloan")
    public String postloan() {
        return "placeholder";
    }

    @GetMapping("/workflow")
    public String workflow() {
        return "placeholder";
    }

    @GetMapping("/report")
    public String report() {
        return "placeholder";
    }
}
```

- [ ] **Step 2: 创建 DashboardController（API）**

```java
package com.example.aitest.controller;

import com.example.aitest.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @GetMapping("/todo")
    public Result<Map<String, Object>> getTodoStats() {
        Map<String, Object> stats = new HashMap<>();
        // 静态模拟数据
        stats.put("application", 12);  // 待审批申请
        stats.put("approval", 5);      // 待审批
        stats.put("contract", 3);      // 待签署合同
        stats.put("loan", 8);          // 待放款
        stats.put("repayment", 20);    // 待处理还款
        stats.put("warning", 3);       // 预警
        stats.put("postloan", 7);      // 贷后任务
        stats.put("other", 15);        // 其他
        return Result.success(stats);
    }
}
```

- [ ] **Step 3: 编译验证**

```bash
mvn compile -q
```

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/example/aitest/controller/IndexController.java
git add src/main/java/com/example/aitest/controller/DashboardController.java
git commit -m "feat: add IndexController and DashboardController"
```

---

### Task 11: 创建首页模板 index.html

**Files:**
- Create: `src/main/resources/templates/index.html`
- Create: `src/main/resources/static/css/common.css`
- Create: `src/main/resources/static/js/common.js`

- [ ] **Step 1: 创建公共 CSS 样式 common.css**

```css
/* 全局样式 */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: 'Microsoft YaHei', Arial, sans-serif;
    background: #f5f7fa;
    color: #333;
}

/* 顶部导航栏 */
.top-nav {
    height: 60px;
    background: #1a3a5c;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 20px;
    color: white;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.top-nav .logo {
    font-size: 20px;
    font-weight: bold;
}

.top-nav .user-info {
    display: flex;
    align-items: center;
    gap: 15px;
}

.top-nav .logout-btn {
    background: #2c5282;
    color: white;
    border: none;
    padding: 8px 16px;
    border-radius: 4px;
    cursor: pointer;
}

.top-nav .logout-btn:hover {
    background: #3a67a8;
}

/* 布局容器 */
.layout-container {
    display: flex;
    min-height: calc(100vh - 60px);
}

/* 左侧菜单 */
.left-menu {
    width: 220px;
    background: #1a3a5c;
    color: white;
    padding-top: 10px;
}

.left-menu .menu-item {
    padding: 15px 20px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 10px;
    transition: background 0.2s;
    text-decoration: none;
    color: white;
}

.left-menu .menu-item:hover {
    background: #2c5282;
}

.left-menu .menu-item.active {
    background: #2c5282;
    border-left: 3px solid #fff;
}

/* 主内容区 */
.main-content {
    flex: 1;
    padding: 20px;
    background: #f5f7fa;
}

/* 卡片样式 */
.card {
    background: white;
    border-radius: 4px;
    padding: 20px;
    margin-bottom: 20px;
    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.card-title {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 15px;
    color: #1a3a5c;
    border-bottom: 2px solid #1a3a5c;
    padding-bottom: 10px;
}

/* 待办任务卡片网格 */
.todo-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
}

.todo-item {
    background: linear-gradient(135deg, #1a3a5c 0%, #2c5282 100%);
    color: white;
    padding: 20px;
    border-radius: 8px;
    text-align: center;
}

.todo-item .count {
    font-size: 36px;
    font-weight: bold;
}

.todo-item .label {
    font-size: 14px;
    margin-top: 8px;
    opacity: 0.9;
}

/* 公告列表 */
.announcement-list {
    list-style: none;
}

.announcement-list li {
    padding: 12px 0;
    border-bottom: 1px solid #d1d9e6;
    display: flex;
    justify-content: space-between;
}

.announcement-list li:last-child {
    border-bottom: none;
}

.announcement-list .title {
    color: #333;
    text-decoration: none;
}

.announcement-list .title:hover {
    color: #1a3a5c;
}

.announcement-list .date {
    color: #999;
    font-size: 14px;
}
```

- [ ] **Step 2: 创建首页模板 index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>工作台 - 银行信贷管理系统</title>
    <link rel="stylesheet" th:href="@{/css/common.css}">
</head>
<body>
    <!-- 顶部导航栏 -->
    <div class="top-nav">
        <div class="logo">银行信贷管理系统</div>
        <div class="user-info">
            <span>管理员</span>
            <button class="logout-btn" onclick="logout()">退出</button>
        </div>
    </div>

    <!-- 布局容器 -->
    <div class="layout-container">
        <!-- 左侧菜单 -->
        <div class="left-menu">
            <a href="/index" class="menu-item active">📊 工作台</a>
            <a href="/application" class="menu-item">📋 申请管理</a>
            <a href="/approval" class="menu-item">✓ 审批管理</a>
            <a href="/contract" class="menu-item">📄 合同管理</a>
            <a href="/loan" class="menu-item">💰 放款管理</a>
            <a href="/repayment" class="menu-item">🔄 还款管理</a>
            <a href="/warning" class="menu-item">⚠️ 预警管理</a>
            <a href="/postloan" class="menu-item">📈 贷后管理</a>
            <a href="/user" class="menu-item">👥 用户管理</a>
            <a href="/role" class="menu-item">👨‍💼 角色管理</a>
            <a href="/workflow" class="menu-item">⚙️ 流程管理</a>
            <a href="/report" class="menu-item">📊 报表管理</a>
        </div>

        <!-- 主内容区 -->
        <div class="main-content">
            <!-- 工作台 -->
            <div class="card">
                <div class="card-title">待办任务</div>
                <div class="todo-grid" id="todoGrid">
                    <div class="todo-item">
                        <div class="count" id="applicationCount">-</div>
                        <div class="label">待审批申请</div>
                    </div>
                    <div class="todo-item">
                        <div class="count" id="approvalCount">-</div>
                        <div class="label">待审批</div>
                    </div>
                    <div class="todo-item">
                        <div class="count" id="contractCount">-</div>
                        <div class="label">待签署合同</div>
                    </div>
                    <div class="todo-item">
                        <div class="count" id="loanCount">-</div>
                        <div class="label">待放款</div>
                    </div>
                    <div class="todo-item">
                        <div class="count" id="repaymentCount">-</div>
                        <div class="label">待处理还款</div>
                    </div>
                    <div class="todo-item">
                        <div class="count" id="warningCount">-</div>
                        <div class="label">预警</div>
                    </div>
                    <div class="todo-item">
                        <div class="count" id="postloanCount">-</div>
                        <div class="label">贷后任务</div>
                    </div>
                    <div class="todo-item">
                        <div class="count" id="otherCount">-</div>
                        <div class="label">其他</div>
                    </div>
                </div>
            </div>

            <!-- 公告栏 -->
            <div class="card">
                <div class="card-title">公告栏</div>
                <ul class="announcement-list" id="announcementList">
                    <li>加载中...</li>
                </ul>
            </div>
        </div>
    </div>

    <script th:src="@{/js/common.js}"></script>
    <script>
        // 加载待办数据
        fetch('/api/dashboard/todo')
            .then(res => res.json())
            .then(data => {
                if (data.code === 200) {
                    document.getElementById('applicationCount').textContent = data.data.application;
                    document.getElementById('approvalCount').textContent = data.data.approval;
                    document.getElementById('contractCount').textContent = data.data.contract;
                    document.getElementById('loanCount').textContent = data.data.loan;
                    document.getElementById('repaymentCount').textContent = data.data.repayment;
                    document.getElementById('warningCount').textContent = data.data.warning;
                    document.getElementById('postloanCount').textContent = data.data.postloan;
                    document.getElementById('otherCount').textContent = data.data.other;
                }
            });

        // 加载公告数据
        fetch('/api/announcements?size=5')
            .then(res => res.json())
            .then(data => {
                const list = document.getElementById('announcementList');
                if (data.code === 200 && data.data && data.data.length > 0) {
                    list.innerHTML = data.data.map(item =>
                        `<li>
                            <a href="#" class="title">${item.title}</a>
                            <span class="date">${formatDate(item.createdAt)}</span>
                        </li>`
                    ).join('');
                } else {
                    list.innerHTML = '<li>暂无公告</li>';
                }
            });

        function formatDate(dateStr) {
            if (!dateStr) return '';
            return dateStr.substring(0, 10);
        }

        function logout() {
            fetch('/api/auth/logout', { method: 'POST' })
                .then(() => window.location.href = '/login');
        }
    </script>
</body>
</html>
```

- [ ] **Step 3: 编译验证**

```bash
mvn compile -q
```

- [ ] **Step 4: Commit**

```bash
git add src/main/resources/templates/index.html
git add src/main/resources/static/css/common.css
git commit -m "feat: add index page with todo dashboard"
```

---

### Task 12: 创建公告 API Controller

**Files:**
- Create: `src/main/java/com/example/aitest/controller/AnnouncementController.java`

- [ ] **Step 1: 创建 AnnouncementController**

```java
package com.example.aitest.controller;

import com.example.aitest.common.Result;
import com.example.aitest.entity.Announcement;
import com.example.aitest.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public Result<List<Announcement>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Announcement> list = announcementService.findByPage(page, size);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Announcement> get(@PathVariable Long id) {
        Announcement announcement = announcementService.findById(id);
        return Result.success(announcement);
    }

    @PostMapping
    public Result<Announcement> create(@RequestBody Map<String, String> body) {
        Announcement announcement = announcementService.create(
                body.get("title"),
                body.get("content"),
                body.get("publishStatus") != null ? Integer.parseInt(body.get("publishStatus")) : 0,
                1L // 当前用户 ID，实际应从 session 获取
        );
        return Result.success(announcement);
    }

    @PutMapping("/{id}")
    public Result<Announcement> update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Announcement announcement = announcementService.update(
                id,
                body.get("title"),
                body.get("content"),
                body.get("publishStatus") != null ? Integer.parseInt(body.get("publishStatus")) : null
        );
        return Result.success(announcement);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        announcementService.publish(id);
        return Result.success();
    }

    @PutMapping("/{id}/unpublish")
    public Result<Void> unpublish(@PathVariable Long id) {
        announcementService.unpublish(id);
        return Result.success();
    }
}
```

- [ ] **Step 2: 编译验证**

```bash
mvn compile -q
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/example/aitest/controller/AnnouncementController.java
git commit -m "feat: add AnnouncementController API"
```

---

## 第五阶段：用户管理和角色管理

### Task 13: 创建用户管理 API Controller

**Files:**
- Create: `src/main/java/com/example/aitest/controller/UserController.java`

- [ ] **Step 1: 创建 UserController**

```java
package com.example.aitest.controller;

import com.example.aitest.common.Result;
import com.example.aitest.entity.Role;
import com.example.aitest.entity.User;
import com.example.aitest.service.RoleService;
import com.example.aitest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    @GetMapping
    public Result<List<User>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        // 简单实现，实际需要添加 search 方法
        List<User> list = userService.findAll();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> get(@PathVariable Long id) {
        User user = userService.findByUsername(""); // 需要扩展方法
        List<Long> roleIds = userService.getRoleIdsByUserId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("roleIds", roleIds);
        return Result.success(result);
    }

    @PostMapping
    public Result<User> create(@RequestBody Map<String, String> body) {
        // 需要实现完整的新增用户逻辑
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<User> update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        // 需要实现更新逻辑
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        // 需要实现删除逻辑
        return Result.success();
    }

    @PutMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        userService.assignRoles(id, body.get("roleIds"));
        return Result.success();
    }

    @PutMapping("/{id}/password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        userService.resetPassword(id, body.get("password"));
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        userService.updateStatus(id, body.get("status"));
        return Result.success();
    }

    @GetMapping("/roles")
    public Result<List<Role>> getRoles() {
        return Result.success(roleService.findAll());
    }
}
```

- [ ] **Step 2: 编译验证**

```bash
mvn compile -q
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/example/aitest/controller/UserController.java
git commit -m "feat: add UserController skeleton"
```

---

### Task 14: 创建角色管理 API Controller

**Files:**
- Create: `src/main/java/com/example/aitest/controller/RoleController.java`

- [ ] **Step 1: 创建 RoleController**

```java
package com.example.aitest.controller;

import com.example.aitest.common.Result;
import com.example.aitest.entity.Menu;
import com.example.aitest.entity.Role;
import com.example.aitest.service.MenuService;
import com.example.aitest.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final MenuService menuService;

    @GetMapping
    public Result<List<Role>> list() {
        return Result.success(roleService.findAll());
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> get(@PathVariable Long id) {
        Role role = roleService.findById(id);
        List<Long> menuIds = roleService.getMenuIdsByRoleId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("role", role);
        result.put("menuIds", menuIds);
        return Result.success(result);
    }

    @PostMapping
    public Result<Role> create(@RequestBody Map<String, String> body) {
        Role role = roleService.create(
                body.get("name"),
                body.get("code"),
                body.get("description")
        );
        return Result.success(role);
    }

    @PutMapping("/{id}")
    public Result<Role> update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Role role = roleService.update(
                id,
                body.get("name"),
                body.get("code"),
                body.get("description"),
                body.get("status") != null ? Integer.parseInt(body.get("status")) : null
        );
        return Result.success(role);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/menus")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        roleService.assignMenus(id, body.get("menuIds"));
        return Result.success();
    }

    @GetMapping("/menus")
    public Result<List<Menu>> getMenus() {
        return Result.success(menuService.findAll());
    }
}
```

- [ ] **Step 2: 创建 MenuService**

```java
package com.example.aitest.service;

import com.example.aitest.entity.Menu;
import java.util.List;

public interface MenuService {
    Menu findById(Long id);
    List<Menu> findAll();
    List<Menu> findByStatus(Integer status);
}
```

```java
package com.example.aitest.service.impl;

import com.example.aitest.entity.Menu;
import com.example.aitest.mapper.MenuMapper;
import com.example.aitest.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    @Override
    public Menu findById(Long id) {
        return menuMapper.findById(id);
    }

    @Override
    public List<Menu> findAll() {
        return menuMapper.findAll();
    }

    @Override
    public List<Menu> findByStatus(Integer status) {
        return menuMapper.findByStatus(status);
    }
}
```

- [ ] **Step 3: 编译验证**

```bash
mvn compile -q
```

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/example/aitest/controller/RoleController.java
git add src/main/java/com/example/aitest/service/MenuService.java
git add src/main/java/com/example/aitest/service/impl/MenuServiceImpl.java
git commit -m "feat: add RoleController and MenuService"
```

---

## 第六阶段：前端页面

### Task 15: 创建用户管理页面

**Files:**
- Create: `src/main/resources/templates/user/index.html`
- Create: `src/main/resources/static/js/user.js`

- [ ] **Step 1: 创建用户管理页面 user/index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>用户管理 - 银行信贷管理系统</title>
    <link rel="stylesheet" th:href="@{/css/common.css}">
    <style>
        .user-table {
            width: 100%;
            border-collapse: collapse;
        }
        .user-table th, .user-table td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #d1d9e6;
        }
        .user-table th {
            background: #f0f4f8;
            font-weight: bold;
        }
        .btn {
            padding: 6px 12px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            margin-right: 8px;
        }
        .btn-primary { background: #1a3a5c; color: white; }
        .btn-danger { background: #dc3545; color: white; }
        .btn-warning { background: #ffc107; color: #333; }
        .status-badge {
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
        }
        .status-active { background: #28a745; color: white; }
        .status-inactive { background: #6c757d; color: white; }
        .modal {
            display: none;
            position: fixed;
            top: 0; left: 0;
            width: 100%; height: 100%;
            background: rgba(0,0,0,0.5);
        }
        .modal-content {
            background: white;
            padding: 20px;
            margin: 50px auto;
            width: 500px;
            border-radius: 8px;
        }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; }
        .form-group input, .form-group select {
            width: 100%;
            padding: 8px;
            border: 1px solid #d1d9e6;
            border-radius: 4px;
        }
    </style>
</head>
<body>
    <div class="top-nav">
        <div class="logo">银行信贷管理系统</div>
        <div class="user-info">
            <span>管理员</span>
            <button class="logout-btn" onclick="logout()">退出</button>
        </div>
    </div>

    <div class="layout-container">
        <div class="left-menu">
            <a href="/index" class="menu-item">📊 工作台</a>
            <a href="/application" class="menu-item">📋 申请管理</a>
            <a href="/approval" class="menu-item">✓ 审批管理</a>
            <a href="/contract" class="menu-item">📄 合同管理</a>
            <a href="/loan" class="menu-item">💰 放款管理</a>
            <a href="/repayment" class="menu-item">🔄 还款管理</a>
            <a href="/warning" class="menu-item">⚠️ 预警管理</a>
            <a href="/postloan" class="menu-item">📈 贷后管理</a>
            <a href="/user" class="menu-item active">👥 用户管理</a>
            <a href="/role" class="menu-item">👨‍💼 角色管理</a>
            <a href="/workflow" class="menu-item">⚙️ 流程管理</a>
            <a href="/report" class="menu-item">📊 报表管理</a>
        </div>

        <div class="main-content">
            <div class="card">
                <div class="card-title">用户管理</div>
                <div style="margin-bottom: 15px;">
                    <button class="btn btn-primary" onclick="showAddModal()">+ 新增用户</button>
                </div>
                <table class="user-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>用户名</th>
                            <th>姓名</th>
                            <th>邮箱</th>
                            <th>手机</th>
                            <th>状态</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody id="userTableBody">
                        <tr><td colspan="7">加载中...</td></tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- 新增/编辑用户弹窗 -->
    <div id="userModal" class="modal">
        <div class="modal-content">
            <h3 id="modalTitle">新增用户</h3>
            <form id="userForm">
                <input type="hidden" id="userId">
                <div class="form-group">
                    <label>用户名</label>
                    <input type="text" id="username" required>
                </div>
                <div class="form-group">
                    <label>密码</label>
                    <input type="password" id="password">
                </div>
                <div class="form-group">
                    <label>角色</label>
                    <select id="roles" multiple></select>
                </div>
                <div class="form-group">
                    <label>状态</label>
                    <select id="status">
                        <option value="1">启用</option>
                        <option value="0">禁用</option>
                    </select>
                </div>
                <button type="submit" class="btn btn-primary">保存</button>
                <button type="button" class="btn" onclick="closeModal()">取消</button>
            </form>
        </div>
    </div>

    <script th:src="@{/js/common.js}"></script>
    <script th:src="@{/js/user.js}"></script>
</body>
</html>
```

- [ ] **Step 2: 创建 user.js**

```javascript
// 加载用户列表
function loadUsers() {
    fetch('/api/users')
        .then(res => res.json())
        .then(data => {
            const tbody = document.getElementById('userTableBody');
            if (data.code === 200 && data.data) {
                tbody.innerHTML = data.data.map(user => `
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.username}</td>
                        <td>${user.username}</td>
                        <td>${user.email || '-'}</td>
                        <td>${user.phone || '-'}</td>
                        <td><span class="status-badge ${user.status === 1 ? 'status-active' : 'status-inactive'}">
                            ${user.status === 1 ? '启用' : '禁用'}
                        </span></td>
                        <td>
                            <button class="btn btn-primary" onclick="editUser(${user.id})">编辑</button>
                            <button class="btn btn-warning" onclick="resetPassword(${user.id})">重置密码</button>
                            <button class="btn btn-danger" onclick="deleteUser(${user.id})">删除</button>
                        </td>
                    </tr>
                `).join('');
            }
        });
}

// 加载角色选项
function loadRoles() {
    fetch('/api/users/roles')
        .then(res => res.json())
        .then(data => {
            const select = document.getElementById('roles');
            if (data.code === 200 && data.data) {
                select.innerHTML = data.data.map(role =>
                    `<option value="${role.id}">${role.name}</option>`
                ).join('');
            }
        });
}

// 显示新增弹窗
function showAddModal() {
    document.getElementById('modalTitle').textContent = '新增用户';
    document.getElementById('userForm').reset();
    document.getElementById('userId').value = '';
    document.getElementById('userModal').style.display = 'block';
}

// 关闭弹窗
function closeModal() {
    document.getElementById('userModal').style.display = 'none';
}

// 退出登录
function logout() {
    fetch('/api/auth/logout', { method: 'POST' })
        .then(() => window.location.href = '/login');
}

// 初始化
loadUsers();
loadRoles();
```

- [ ] **Step 3: 编译验证**

```bash
mvn compile -q
```

- [ ] **Step 4: Commit**

```bash
git add src/main/resources/templates/user/index.html
git add src/main/resources/static/js/user.js
git commit -m "feat: add user management page"
```

---

### Task 16: 创建角色管理页面

**Files:**
- Create: `src/main/resources/templates/role/index.html`
- Create: `src/main/resources/static/js/role.js`

- [ ] **Step 1: 创建角色管理页面 role/index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>角色管理 - 银行信贷管理系统</title>
    <link rel="stylesheet" th:href="@{/css/common.css}">
    <style>
        .role-table { width: 100%; border-collapse: collapse; }
        .role-table th, .role-table td { padding: 12px; text-align: left; border-bottom: 1px solid #d1d9e6; }
        .role-table th { background: #f0f4f8; font-weight: bold; }
        .btn { padding: 6px 12px; border: none; border-radius: 4px; cursor: pointer; margin-right: 8px; }
        .btn-primary { background: #1a3a5c; color: white; }
        .btn-danger { background: #dc3545; color: white; }
        .btn-warning { background: #ffc107; color: #333; }
        .status-badge { padding: 4px 8px; border-radius: 4px; font-size: 12px; }
        .status-active { background: #28a745; color: white; }
        .status-inactive { background: #6c757d; color: white; }
        .modal { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); }
        .modal-content { background: white; padding: 20px; margin: 50px auto; width: 500px; border-radius: 8px; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; }
        .form-group input, .form-group select { width: 100%; padding: 8px; border: 1px solid #d1d9e6; border-radius: 4px; }
        .menu-tree { max-height: 200px; overflow-y: auto; border: 1px solid #d1d9e6; padding: 10px; }
        .menu-item { margin: 5px 0; }
    </style>
</head>
<body>
    <div class="top-nav">
        <div class="logo">银行信贷管理系统</div>
        <div class="user-info">
            <span>管理员</span>
            <button class="logout-btn" onclick="logout()">退出</button>
        </div>
    </div>

    <div class="layout-container">
        <div class="left-menu">
            <a href="/index" class="menu-item">📊 工作台</a>
            <a href="/user" class="menu-item">👥 用户管理</a>
            <a href="/role" class="menu-item active">👨‍💼 角色管理</a>
            <a href="/workflow" class="menu-item">⚙️ 流程管理</a>
            <a href="/report" class="menu-item">📊 报表管理</a>
        </div>

        <div class="main-content">
            <div class="card">
                <div class="card-title">角色管理</div>
                <button class="btn btn-primary" onclick="showAddModal()">+ 新增角色</button>
                <table class="role-table" style="margin-top: 15px;">
                    <thead>
                        <tr><th>ID</th><th>角色名称</th><th>角色编码</th><th>描述</th><th>状态</th><th>操作</th></tr>
                    </thead>
                    <tbody id="roleTableBody"><tr><td colspan="6">加载中...</td></tr></tbody>
                </table>
            </div>
        </div>
    </div>

    <div id="roleModal" class="modal">
        <div class="modal-content">
            <h3 id="modalTitle">新增角色</h3>
            <form id="roleForm">
                <input type="hidden" id="roleId">
                <div class="form-group">
                    <label>角色名称</label>
                    <input type="text" id="roleName" required>
                </div>
                <div class="form-group">
                    <label>角色编码</label>
                    <input type="text" id="roleCode" required>
                </div>
                <div class="form-group">
                    <label>描述</label>
                    <input type="text" id="roleDesc">
                </div>
                <div class="form-group">
                    <label>菜单权限</label>
                    <div class="menu-tree" id="menuTree"></div>
                </div>
                <button type="submit" class="btn btn-primary">保存</button>
                <button type="button" class="btn" onclick="closeModal()">取消</button>
            </form>
        </div>
    </div>

    <script th:src="@{/js/common.js}"></script>
    <script th:src="@{/js/role.js}"></script>
</body>
</html>
```

- [ ] **Step 2: 创建 role.js**

```javascript
function loadRoles() {
    fetch('/api/roles')
        .then(res => res.json())
        .then(data => {
            const tbody = document.getElementById('roleTableBody');
            if (data.code === 200 && data.data) {
                tbody.innerHTML = data.data.map(role => `
                    <tr>
                        <td>${role.id}</td>
                        <td>${role.name}</td>
                        <td>${role.code}</td>
                        <td>${role.description || '-'}</td>
                        <td><span class="status-badge ${role.status === 1 ? 'status-active' : 'status-inactive'}">
                            ${role.status === 1 ? '启用' : '禁用'}
                        </span></td>
                        <td>
                            <button class="btn btn-primary" onclick="editRole(${role.id})">编辑</button>
                            <button class="btn btn-warning" onclick="assignMenus(${role.id})">分配菜单</button>
                            <button class="btn btn-danger" onclick="deleteRole(${role.id})">删除</button>
                        </td>
                    </tr>
                `).join('');
            }
        });
}

function loadMenus() {
    fetch('/api/roles/menus')
        .then(res => res.json())
        .then(data => {
            const tree = document.getElementById('menuTree');
            if (data.code === 200 && data.data) {
                tree.innerHTML = data.data.map(menu => `
                    <div class="menu-item">
                        <input type="checkbox" name="menuIds" value="${menu.id}" id="menu_${menu.id}">
                        <label for="menu_${menu.id}">${menu.name}</label>
                    </div>
                `).join('');
            }
        });
}

function showAddModal() {
    document.getElementById('modalTitle').textContent = '新增角色';
    document.getElementById('roleForm').reset();
    document.getElementById('roleId').value = '';
    document.getElementById('roleModal').style.display = 'block';
    loadMenus();
}

function closeModal() {
    document.getElementById('roleModal').style.display = 'none';
}

function logout() {
    fetch('/api/auth/logout', { method: 'POST' })
        .then(() => window.location.href = '/login');
}

loadRoles();
loadMenus();
```

- [ ] **Step 3: 编译验证**

```bash
mvn compile -q
```

- [ ] **Step 4: Commit**

```bash
git add src/main/resources/templates/role/index.html
git add src/main/resources/static/js/role.js
git commit -m "feat: add role management page"
```

---

### Task 17: 创建占位页面

**Files:**
- Create: `src/main/resources/templates/placeholder.html`

- [ ] **Step 1: 创建占位页面**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>敬请期待 - 银行信贷管理系统</title>
    <link rel="stylesheet" th:href="@{/css/common.css}">
    <style>
        .placeholder {
            text-align: center;
            padding: 100px 20px;
        }
        .placeholder h1 {
            color: #1a3a5c;
            font-size: 36px;
            margin-bottom: 20px;
        }
        .placeholder p {
            color: #666;
            font-size: 18px;
        }
    </style>
</head>
<body>
    <div class="top-nav">
        <div class="logo">银行信贷管理系统</div>
        <div class="user-info">
            <span>管理员</span>
            <button class="logout-btn" onclick="logout()">退出</button>
        </div>
    </div>
    <div class="layout-container">
        <div class="left-menu">
            <a href="/index" class="menu-item">📊 工作台</a>
            <a href="/application" class="menu-item">📋 申请管理</a>
            <a href="/approval" class="menu-item">✓ 审批管理</a>
            <a href="/contract" class="menu-item">📄 合同管理</a>
            <a href="/loan" class="menu-item">💰 放款管理</a>
            <a href="/repayment" class="menu-item">🔄 还款管理</a>
            <a href="/warning" class="menu-item">⚠️ 预警管理</a>
            <a href="/postloan" class="menu-item">📈 贷后管理</a>
            <a href="/user" class="menu-item">👥 用户管理</a>
            <a href="/role" class="menu-item">👨‍💼 角色管理</a>
            <a href="/workflow" class="menu-item">⚙️ 流程管理</a>
            <a href="/report" class="menu-item">📊 报表管理</a>
        </div>
        <div class="main-content">
            <div class="placeholder">
                <h1>🚧 功能开发中</h1>
                <p>该模块正在紧张开发中，敬请期待！</p>
            </div>
        </div>
    </div>
    <script>
        function logout() {
            fetch('/api/auth/logout', { method: 'POST' })
                .then(() => window.location.href = '/login');
        }
    </script>
</body>
</html>
```

- [ ] **Step 2: 编译验证**

```bash
mvn compile -q
```

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/templates/placeholder.html
git commit -m "feat: add placeholder page for unfinished modules"
```

---

## 第七阶段：测试和验收

### Task 18: 功能测试

- [ ] **Step 1: 启动应用**

```bash
java -jar target/aitest-0.0.1-SNAPSHOT.jar
```

- [ ] **Step 2: 测试登录**

访问 http://localhost:8080/login
- 输入 admin / 123456 和正确验证码
- 验证登录成功跳转到首页

- [ ] **Step 3: 测试首页**

访问 http://localhost:8080/index
- 验证待办任务卡片显示数字
- 验证公告栏加载（可能有数据或显示"暂无公告"）
- 验证左侧菜单显示 11 个模块

- [ ] **Step 4: 测试用户管理**

访问 http://localhost:8080/user
- 验证用户列表加载
- 测试新增用户
- 测试角色分配
- 测试密码重置

- [ ] **Step 5: 测试角色管理**

访问 http://localhost:8080/role
- 验证角色列表加载（应显示 4 个初始化角色）
- 测试新增角色
- 测试菜单权限分配

- [ ] **Step 6: 测试占位菜单**

依次点击其他 9 个菜单
- 验证都跳转到占位页面
- 验证显示"功能开发中"提示

- [ ] **Step 7: 提交**

```bash
git add .
git commit -m "chore: complete implementation and testing"
```

---

## 交付清单检查

- [ ] 数据库 5 张表创建完成
- [ ] 初始化数据插入成功
- [ ] Entity/DAO/Service/Controller层完成
- [ ] 首页（工作台 + 公告栏）可用
- [ ] 用户管理 CRUD 完成
- [ ] 角色管理 CRUD 完成
- [ ] 菜单权限分配完成
- [ ] 公告管理 API 完成
- [ ] 占位页面完成
- [ ] 所有页面 UI 风格统一（传统银行风）

---

**Plan complete and saved to `docs/superpowers/plans/2026-03-20-credit-system-index-plan.md`.**

Two execution options:

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**
