-- ====================================
-- 数据库中文乱码修复脚本
-- 执行前请备份数据库！
-- ====================================

USE aitest;

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 清空所有表
DELETE FROM loan_application;
DELETE FROM customer;
DELETE FROM role_menu;
DELETE FROM user_role;
DELETE FROM menu;
DELETE FROM role;
DELETE FROM approval_record;

-- 重新插入角色数据
INSERT INTO `role` (`id`, `name`, `code`, `description`, `status`, `created_at`, `updated_at`) VALUES
(1, '系统管理员', 'ADMIN', '拥有所有权限', 1, NOW(), NOW()),
(2, '信贷审批员', 'APPROVER', '负责贷款审批', 1, NOW(), NOW()),
(3, '客户经理', 'MANAGER', '负责客户管理', 1, NOW(), NOW()),
(4, '普通用户', 'USER', '基础权限', 1, NOW(), NOW());

-- 重新插入菜单数据
INSERT INTO `menu` (`id`, `name`, `parent_id`, `path`, `icon`, `sort_order`, `permission`, `status`, `created_at`, `updated_at`) VALUES
(1, '工作台', 0, '/index', 'dashboard', 1, 'index:view', 1, NOW(), NOW()),
(2, '申请管理', 0, '/application', 'file-text', 2, 'application:view', 1, NOW(), NOW()),
(3, '审批管理', 0, '/approval', 'check-circle', 3, 'approval:view', 1, NOW(), NOW()),
(4, '合同管理', 0, '/contract', 'file', 4, 'contract:view', 1, NOW(), NOW()),
(5, '放款管理', 0, '/loan', 'dollar', 5, 'loan:view', 1, NOW(), NOW()),
(6, '还款管理', 0, '/repayment', 'refresh-cw', 6, 'repayment:view', 1, NOW(), NOW()),
(7, '预警管理', 0, '/warning', 'alert-triangle', 7, 'warning:view', 1, NOW(), NOW()),
(8, '贷后管理', 0, '/postloan', 'clipboard', 8, 'postloan:view', 1, NOW(), NOW()),
(9, '用户管理', 0, '/user', 'users', 9, 'user:view', 1, NOW(), NOW()),
(10, '角色管理', 0, '/role', 'user-check', 10, 'role:view', 1, NOW(), NOW()),
(11, '流程管理', 0, '/workflow', 'settings', 11, 'workflow:view', 1, NOW(), NOW()),
(12, '报表管理', 0, '/report', 'bar-chart', 12, 'report:view', 1, NOW(), NOW());

-- 重新插入客户数据
INSERT INTO `customer` (`id`, `name`, `id_no`, `phone`, `email`, `created_at`, `updated_at`) VALUES
(1, '张三', '110101199001011234', '13800138001', 'zhangsan@example.com', NOW(), NOW()),
(2, '李四', '110101199002022345', '13800138002', 'lisi@example.com', NOW(), NOW()),
(3, '王五', '110101199003033456', '13800138003', 'wangwu@example.com', NOW(), NOW());

-- 重新插入贷款申请数据
INSERT INTO `loan_application` (`id`, `application_no`, `customer_id`, `customer_name`, `customer_id_no`, `customer_phone`, `loan_amount`, `loan_term`, `loan_purpose`, `guarantee_type`, `status`, `current_stage`, `created_by`, `created_at`, `updated_at`) VALUES
(1, 'A202604020001', 1, '张三', '110101199001011234', '13800138001', 50000.00, 12, '消费装修', 'CREDIT', 'INITIAL', 'INITIAL', 1, NOW(), NOW()),
(2, 'A202604020002', 2, '李四', '110101199002022345', '13800138002', 200000.00, 36, '企业经营', 'MORTGAGE', 'RISK', 'RISK', 1, NOW(), NOW()),
(3, 'A202604020003', 3, '王五', '110101199003033456', '13800138003', 80000.00, 24, '购车', 'GUARANTEE', 'APPROVED', 'APPROVED', 1, NOW(), NOW());

-- 重新插入角色 - 菜单关联
INSERT INTO `role_menu` (`role_id`, `menu_id`)
SELECT 1, id FROM `menu`
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- 重新插入用户 - 角色关联
INSERT INTO `user_role` (`user_id`, `role_id`)
SELECT 1, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_role WHERE user_id = 1 AND role_id = 1);

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 验证修复结果
SELECT '角色表' as 表名，COUNT(*) as 记录数 FROM role;
SELECT '菜单表' as 表名，COUNT(*) as 记录数 FROM menu;
SELECT '客户表' as 表名，COUNT(*) as 记录数 FROM customer;
SELECT '申请表' as 表名，COUNT(*) as 记录数 FROM loan_application;

-- 显示修复后的数据
SELECT * FROM role;
SELECT * FROM customer;
SELECT * FROM loan_application;
