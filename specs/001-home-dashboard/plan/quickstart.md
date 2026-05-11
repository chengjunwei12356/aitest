# Quickstart: 首页工作台功能开发

**Feature**: 首页工作台 (001-home-dashboard)

**Date**: 2026-03-24

---

## 快速开始指南

### 前置条件

1. Java 17 环境
2. MySQL 8.0 数据库运行中
3. Maven 依赖已安装

### 数据库准备

确认公告表存在：

```sql
-- 查看表结构
DESCRIBE announcement;

-- 如果表不存在，创建它
CREATE TABLE IF NOT EXISTS announcement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '公告标题',
    content TEXT COMMENT '公告内容',
    status TINYINT DEFAULT 1 COMMENT '状态 0-草稿 1-发布',
    created_by BIGINT COMMENT '创建人 ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status_created_at (status, created_at)
);

-- 插入测试数据
INSERT INTO announcement (title, content, status, created_at) VALUES
('系统上线通知', '银行信贷管理系统正式上线运行，欢迎使用！', 1, '2026-03-24 10:00:00'),
('维护公告', '系统将于本周六凌晨进行例行维护，请提前保存数据。', 1, '2026-03-23 15:30:00'),
('版本更新', 'v1.1.0 版本新增还款计划导出功能。', 1, '2026-03-22 09:15:00');
```

---

## 开发任务清单

### Phase 1: 基础功能

1. **创建 Announcement 实体类**
   - 路径：`src/main/java/com/example/aitest/entity/Announcement.java`

2. **创建 AnnouncementMapper 接口**
   - 路径：`src/main/java/com/example/aitest/mapper/AnnouncementMapper.java`

3. **创建 AnnouncementMapper.xml**
   - 路径：`src/main/resources/mapper/AnnouncementMapper.xml`

4. **创建 AnnouncementService**
   - 路径：`src/main/java/com/example/aitest/service/AnnouncementService.java`
   - 实现：`src/main/java/com/example/aitest/service/impl/AnnouncementServiceImpl.java`

5. **修改 AnnouncementController**
   - 添加公告列表 API `/api/announcements`
   - 添加公告详情 API `/api/announcements/{id}`

6. **创建公告详情页面**
   - 路径：`src/main/resources/templates/announcement-detail.html`

7. **修改首页 index.html**
   - 添加 30 秒定时刷新功能
   - 修改公告链接跳转到详情页

---

## 运行项目

```bash
# 设置 JAVA 环境
set JAVA_HOME=C:/Users/Admin/AppData/Local/Temp/jdk17/jdk-17.0.2

# 编译项目
mvn clean package -DskipTests

# 运行项目
java -jar target/aitest-0.0.1-SNAPSHOT.jar

# 或使用批处理
run.bat
```

---

## 验证功能

### 1. 访问首页

浏览器打开：`http://localhost:8080/index`

**验证点**:
- [ ] 待办任务 8 类数据显示正确
- [ ] 公告栏显示最新 5 条公告
- [ ] 左侧菜单高亮当前页面

### 2. 测试 API

```bash
# 待办统计 API
curl http://localhost:8080/api/dashboard/todo

# 公告列表 API
curl http://localhost:8080/api/announcements?size=5

# 公告详情 API (替换 ID)
curl http://localhost:8080/api/announcements/1
```

### 3. 测试公告详情页面

浏览器打开：`http://localhost:8080/announcement/1`

**验证点**:
- [ ] 显示公告标题
- [ ] 显示公告内容
- [ ] 显示发布日期
- [ ] 返回按钮可返回首页

### 4. 测试自动刷新

打开浏览器开发者工具 → Network 面板

**验证点**:
- [ ] 每 30 秒发送一次 `/api/dashboard/todo` 请求
- [ ] 数据正确更新到页面

---

## 常见问题

### 公告表不存在

**错误**: `Table 'aitest.announcement' doesn't exist`

**解决**: 执行上述建表 SQL

### 404 错误

**错误**: 访问公告详情页 404

**解决**: 检查 `AnnouncementController` 的 `@GetMapping` 映射是否正确

### 数据不显示

**错误**: 公告栏显示"暂无公告"

**解决**:
1. 检查数据库是否有已发布公告 (`status=1`)
2. 检查 API 返回数据格式

---

## 下一步

完成开发后：

1. 运行单元测试
2. 手动测试所有功能
3. 提交代码到分支 `001-home-dashboard`
4. 创建 Pull Request
