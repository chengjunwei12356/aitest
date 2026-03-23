-- 插入测试数据
-- 密码：123456，使用 BCrypt 加密
INSERT INTO `user` (`username`, `password`, `email`, `status`)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lqkkO9QS3TzCjU3rS', 'admin@example.com', 1)
ON DUPLICATE KEY UPDATE `password` = VALUES(`password`);

-- ===========================
-- 银行信贷管理系统初始化数据
-- ===========================

-- 初始化角色
INSERT INTO `role` (`name`, `code`, `description`, `status`) VALUES
('系统管理员', 'ADMIN', '拥有所有权限', 1),
('信贷审批员', 'APPROVER', '负责贷款审批', 1),
('客户经理', 'MANAGER', '负责客户管理', 1),
('普通用户', 'USER', '基础权限', 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

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

-- 管理员角色分配所有菜单权限
INSERT INTO `role_menu` (`role_id`, `menu_id`)
SELECT 1, id FROM `menu`
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- 将 admin 用户关联到管理员角色
INSERT INTO `user_role` (`user_id`, `role_id`)
SELECT 1, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_role WHERE user_id = 1 AND role_id = 1);
