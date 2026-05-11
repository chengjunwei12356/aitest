# Bank Credit System Constitution

## Core Principles

### I. 代码质量优先

所有代码必须遵循 Spring Boot 最佳实践：
- Controller-Service-Mapper 分层架构
- 统一响应格式 (Result)
- 业务异常使用 BusinessException
- 禁止静默捕获 Exception

### II. 测试覆盖

核心业务逻辑必须有单元测试：
- Service 层逻辑测试覆盖率 > 80%
- Controller 层接口测试
- 数据库操作集成测试

### III. 安全合规

银行系统安全要求：
- 密码必须使用 BCrypt 加密
- SQL 注入防护 (使用参数化查询)
- XSS 防护 (Thymeleaf 自动转义)
- 敏感操作日志记录

### IV. 数据一致性

数据库操作规范：
- 事务边界明确 (@Transactional)
- 乐观锁处理并发
- 外键约束维护引用完整性

## Technology Stack

- **Java**: 17 (OpenJDK 17.0.2)
- **Framework**: Spring Boot 3.2.5
- **ORM**: MyBatis 3.0.4
- **Database**: MySQL 8.0
- **Template**: Thymeleaf 3.1.2
- **Build**: Maven

## Development Workflow

1. 功能开发基于特性分支 (`###-feature-name`)
2. 代码提交前运行测试验证
3. 主要功能需要代码审查
4. 数据库变更需要 SQL 脚本

## Quality Gates

- 编译无警告
- 单元测试通过
- 代码遵循项目规范
- API 响应格式统一

**Version**: 1.0.0 | **Ratified**: 2026-03-24 | **Last Amended**: 2026-03-24
