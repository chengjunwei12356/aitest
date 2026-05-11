# Research Report: 首页工作台功能

**Feature**: 首页工作台 (001-home-dashboard)

**Date**: 2026-03-24

---

## Research Tasks & Findings

### Task 1: 现有代码模式分析

**Status**: ✅ 完成

**Findings**:

#### Controller 模式
```java
@Controller
public class IndexController {
    @GetMapping("/index")
    public String index() {
        return "index";
    }
}

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    @GetMapping("/todo")
    public Result<Map<String, Object>> getTodoStats() {
        Map<String, Object> stats = new HashMap<>();
        // ...
        return Result.success(stats);
    }
}
```

#### Service 模式 (待创建)
- 遵循接口 + 实现模式
- 使用 `@Service` 注解
- 事务管理使用 `@Transactional`

#### Mapper 模式
- MyBatis 接口方式
- XML 映射文件位于 `src/main/resources/mapper/`

---

### Task 2: 公告数据表结构分析

**Status**: ✅ 完成

**现有表结构**:
```sql
CREATE TABLE announcement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '公告标题',
    content TEXT COMMENT '公告内容',
    status TINYINT DEFAULT 1 COMMENT '状态 0-草稿 1-发布',
    created_by BIGINT COMMENT '创建人 ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status_created_at (status, created_at)
);
```

**查询已发布最新 5 条公告 SQL**:
```sql
SELECT id, title, created_at
FROM announcement
WHERE status = 1
ORDER BY created_at DESC
LIMIT 5;
```

**查询公告详情 SQL**:
```sql
SELECT id, title, content, created_by, created_at
FROM announcement
WHERE id = ? AND status = 1;
```

---

### Task 3: 待办数据来源分析

**Status**: ✅ 完成 - 发现需要实现

**现状**: 当前 DashboardController 使用静态模拟数据

**需要的待办统计**:
| 类型 | 数据来源 | 状态 |
|------|----------|------|
| 待审批申请 | application 表 (status=待审批) | 需创建查询 |
| 待审批 | approval 表 (status=待处理) | 需创建查询 |
| 待签署合同 | contract 表 (status=待签署) | 需创建查询 |
| 待放款 | loan 表 (status=待放款) | 需创建查询 |
| 待处理还款 | repayment 表 (status=待处理) | 需创建查询 |
| 预警 | warning 表 (status=未处理) | 需创建查询 |
| 贷后任务 | postloan_task 表 | 需创建查询 |
| 其他 | 其他待办事项 | 需定义 |

**建议实现方案**:
1. 第一阶段：维持静态数据，验证前端展示
2. 第二阶段：逐个业务模块集成真实数据

---

## Technical Decisions

### Decision 1: 公告详情页实现方式

**Decision**: 使用独立页面路由 `/announcement/{id}`

**Rationale**:
- 符合传统 Web 页面模式
- URL 可直接分享和书签
- SEO 友好 (如果需要)
- 浏览器前进后退正常工作

**Alternatives Considered**:
| 方案 | 优点 | 缺点 |
|------|------|------|
| 弹窗显示 | 无需跳转 | URL 不可分享、 bookmark 困难 |
| 独立页面 | 可分享、SEO | 需要新页面 |

---

### Decision 2: 待办数据刷新策略

**Decision**: 前端 JavaScript 定时轮询 (30 秒间隔)

**Rationale**:
- 实现简单，仅需前端修改
- 无需 WebSocket 复杂基础设施
- 服务器压力可控

**Alternatives Considered**:
| 方案 | 优点 | 缺点 |
|------|------|------|
| 手动刷新 | 最简单 | 用户体验差 |
| 30 秒轮询 | 平衡方案 | 有一定服务器压力 |
| 60 秒轮询 | 压力更小 | 实时性稍差 |
| WebSocket 推送 | 实时性最好 | 基础设施复杂 |

---

### Decision 3: 自动刷新前端实现

**Decision**: 使用 `setInterval` 定时调用 API

**Implementation**:
```javascript
// 每 30 秒刷新待办数据
function refreshTodoData() {
    fetch('/api/dashboard/todo')
        .then(res => res.json())
        .then(data => {
            if (data.code === 200) {
                // 更新显示
            }
        });
}
setInterval(refreshTodoData, 30000);
```

---

## Unresolved Questions

无。所有技术问题已解决。

---

## References

- [Spring Boot 官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [MyBatis 官方文档](https://mybatis.org/mybatis-3/)
- [Thymeleaf 官方文档](https://www.thymeleaf.org/documentation.html)
