-- Create database
CREATE DATABASE IF NOT EXISTS aitest DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE aitest;

-- User table
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `email` VARCHAR(100) DEFAULT NULL,
    `phone` VARCHAR(20) DEFAULT NULL,
    `status` TINYINT DEFAULT 1,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Captcha table
CREATE TABLE IF NOT EXISTS `captcha` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `session_id` VARCHAR(64) NOT NULL,
    `code` VARCHAR(10) NOT NULL,
    `expire_time` DATETIME NOT NULL,
    `used` TINYINT DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert test user (password: 123456, BCrypt hashed)
INSERT INTO `user` (`username`, `password`, `email`, `status`)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lqkkO9QS3TzCjU3rS', 'admin@example.com', 1)
ON DUPLICATE KEY UPDATE `password` = VALUES(`password`);

-- ===========================
-- 银行信贷管理系统新增表
-- ===========================

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

-- 用户 - 角色关联表
CREATE TABLE IF NOT EXISTS `user_role` (
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`role_id`) REFERENCES `role`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户 - 角色关联表';

-- 角色 - 菜单关联表
CREATE TABLE IF NOT EXISTS `role_menu` (
    `role_id` BIGINT NOT NULL,
    `menu_id` BIGINT NOT NULL,
    PRIMARY KEY (`role_id`, `menu_id`),
    FOREIGN KEY (`role_id`) REFERENCES `role`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`menu_id`) REFERENCES `menu`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色 - 菜单关联表';

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

-- ===========================
-- 客户经理助手模块表
-- ===========================

-- 客户提醒表
CREATE TABLE IF NOT EXISTS `customer_reminder` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '客户经理用户 ID',
    `customer_id` BIGINT NOT NULL COMMENT '客户 ID',
    `reminder_type` VARCHAR(50) NOT NULL COMMENT '提醒类型（枚举）',
    `title` VARCHAR(200) NOT NULL COMMENT '提醒标题',
    `content` TEXT COMMENT '提醒内容',
    `priority` TINYINT DEFAULT 1 COMMENT '优先级：1-低, 2-中, 3-高',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待处理, NOTIFIED-已通知, RESOLVED-已解决',
    `resolution_note` TEXT COMMENT '解决备注',
    `due_date` DATETIME NOT NULL COMMENT '截止时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_customer_id` (`customer_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_due_date` (`due_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户提醒表';

-- 知识库文章表
CREATE TABLE IF NOT EXISTS `knowledge_article` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `category` VARCHAR(50) NOT NULL COMMENT '分类（枚举）',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT COMMENT '内容',
    `tags` VARCHAR(500) COMMENT '标签（逗号分隔）',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `is_active` TINYINT DEFAULT 1 COMMENT '是否激活：1-是, 0-否',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_category` (`category`),
    INDEX `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库文章表';

-- ===========================
-- 站内消息表
-- ===========================
CREATE TABLE IF NOT EXISTS `notification` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '接收用户 ID',
    `title` VARCHAR(200) NOT NULL COMMENT '消息标题',
    `content` TEXT COMMENT '消息内容',
    `type` VARCHAR(50) NOT NULL COMMENT '消息类型: REMINDER-提醒, APPROVAL-审批, SYSTEM-系统',
    `related_id` BIGINT DEFAULT NULL COMMENT '关联业务 ID (如 reminder_id, application_id)',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读: 0-未读, 1-已读',
    `priority` TINYINT DEFAULT 1 COMMENT '优先级: 1-低, 2-中, 3-高',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `read_at` DATETIME DEFAULT NULL COMMENT '阅读时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_is_read` (`is_read`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内消息表';
