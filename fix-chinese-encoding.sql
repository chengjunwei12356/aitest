-- ====================================
-- 修复中文乱码问题 SQL 脚本
-- 执行前先备份数据库：mysqldump -uroot -proot aitest > backup.sql
-- ====================================

USE aitest;

-- 1. 修改表的字符集为 utf8mb4
ALTER TABLE `customer` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `loan_application` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `approval_record` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `role` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `menu` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 清空现有数据
TRUNCATE TABLE `loan_application`;
TRUNCATE TABLE `customer`;
TRUNCATE TABLE `role_menu`;
TRUNCATE TABLE `user_role`;
TRUNCATE TABLE `menu`;
TRUNCATE TABLE `role`;
TRUNCATE TABLE `approval_record`;

-- 3. 重新插入角色数据
INSERT INTO `role` (`name`, `code`, `description`, `status`) VALUES
('系统管理员', 'ADMIN', '拥有所有权限', 1),
('信贷审批员', 'APPROVER', '负责贷款审批', 1),
('客户经理', 'MANAGER', '负责客户管理', 1),
('普通用户', 'USER', '基础权限', 1);

-- 4. 重新插入菜单数据
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
('报表管理', 0, '/report', 'bar-chart', 12, 'report:view', 1);

-- 5. 重新插入客户数据（修复中文乱码）
INSERT INTO `customer` (`name`, `id_no`, `phone`, `email`) VALUES
('张三', '110101199001011234', '13800138001', 'zhangsan@example.com'),
('李四', '110101199002022345', '13800138002', 'lisi@example.com'),
('王五', '110101199003033456', '13800138003', 'wangwu@example.com');

-- 6. 重新插入贷款申请数据（修复中文乱码）
INSERT INTO `loan_application` (`application_no`, `customer_id`, `customer_name`, `customer_id_no`, `customer_phone`, `loan_amount`, `loan_term`, `loan_purpose`, `guarantee_type`, `status`, `current_stage`, `created_by`) VALUES
('A202604010001', 1, '张三', '110101199001011234', '13800138001', 50000.00, 12, '消费装修', 'CREDIT', 'INITIAL', 'INITIAL', 1),
('A202604010002', 2, '李四', '110101199002022345', '13800138002', 200000.00, 36, '企业经营', 'MORTGAGE', 'RISK', 'RISK', 1),
('A202604010003', 3, '王五', '110101199003033456', '13800138003', 80000.00, 24, '购车', 'GUARANTEE', 'APPROVED', 'APPROVED', 1);

-- 7. 分配权限
INSERT INTO `role_menu` (`role_id`, `menu_id`)
SELECT 1, id FROM `menu`
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- 8. 将 admin 用户关联到管理员角色
INSERT INTO `user_role` (`user_id`, `role_id`)
SELECT 1, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_role WHERE user_id = 1 AND role_id = 1);

-- ====================================
-- 修复完成！请验证数据：
-- SELECT * FROM customer;
-- SELECT * FROM loan_application;
-- ====================================
