package com.example.aitest.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 数据库数据修复 - 修复中文乱码问题
 * 只在 ID 为 1 的记录不存在时执行一次
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class DatabaseFixRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // 强制修复数据库（每次启动都执行）
        log.info("开始执行数据库字符集修复...");

        try {
            // 禁用外键检查
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

            // 删除数据
            log.info("删除 loan_application 表数据...");
            jdbcTemplate.execute("DELETE FROM loan_application");

            log.info("删除 customer 表数据...");
            jdbcTemplate.execute("DELETE FROM customer");

            log.info("删除 role_menu 表数据...");
            jdbcTemplate.execute("DELETE FROM role_menu");

            log.info("删除 user_role 表数据...");
            jdbcTemplate.execute("DELETE FROM user_role");

            log.info("删除 menu 表数据...");
            jdbcTemplate.execute("DELETE FROM menu");

            log.info("删除 role 表数据...");
            jdbcTemplate.execute("DELETE FROM role");

            // 重新插入角色数据
            log.info("插入角色数据...");
            jdbcTemplate.update("INSERT INTO role (id, name, code, description, status) VALUES (?, ?, ?, ?, ?)",
                    1, "系统管理员", "ADMIN", "拥有所有权限", 1);
            jdbcTemplate.update("INSERT INTO role (id, name, code, description, status) VALUES (?, ?, ?, ?, ?)",
                    2, "信贷审批员", "APPROVER", "负责贷款审批", 1);
            jdbcTemplate.update("INSERT INTO role (id, name, code, description, status) VALUES (?, ?, ?, ?, ?)",
                    3, "客户经理", "MANAGER", "负责客户管理", 1);
            jdbcTemplate.update("INSERT INTO role (id, name, code, description, status) VALUES (?, ?, ?, ?, ?)",
                    4, "普通用户", "USER", "基础权限", 1);

            // 重新插入菜单数据
            log.info("插入菜单数据...");
            jdbcTemplate.update("INSERT INTO menu (id, name, parent_id, path, icon, sort_order, permission, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    1, "工作台", 0, "/index", "dashboard", 1, "index:view", 1);
            jdbcTemplate.update("INSERT INTO menu (id, name, parent_id, path, icon, sort_order, permission, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    2, "申请管理", 0, "/application", "file-text", 2, "application:view", 1);
            jdbcTemplate.update("INSERT INTO menu (id, name, parent_id, path, icon, sort_order, permission, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    3, "审批管理", 0, "/approval", "check-circle", 3, "approval:view", 1);
            jdbcTemplate.update("INSERT INTO menu (id, name, parent_id, path, icon, sort_order, permission, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    4, "合同管理", 0, "/contract", "file", 4, "contract:view", 1);
            jdbcTemplate.update("INSERT INTO menu (id, name, parent_id, path, icon, sort_order, permission, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    5, "放款管理", 0, "/loan", "dollar", 5, "loan:view", 1);
            jdbcTemplate.update("INSERT INTO menu (id, name, parent_id, path, icon, sort_order, permission, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    6, "还款管理", 0, "/repayment", "refresh-cw", 6, "repayment:view", 1);
            jdbcTemplate.update("INSERT INTO menu (id, name, parent_id, path, icon, sort_order, permission, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    7, "预警管理", 0, "/warning", "alert-triangle", 7, "warning:view", 1);
            jdbcTemplate.update("INSERT INTO menu (id, name, parent_id, path, icon, sort_order, permission, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    8, "贷后管理", 0, "/postloan", "clipboard", 8, "postloan:view", 1);
            jdbcTemplate.update("INSERT INTO menu (id, name, parent_id, path, icon, sort_order, permission, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    9, "用户管理", 0, "/user", "users", 9, "user:view", 1);
            jdbcTemplate.update("INSERT INTO menu (id, name, parent_id, path, icon, sort_order, permission, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    10, "角色管理", 0, "/role", "user-check", 10, "role:view", 1);
            jdbcTemplate.update("INSERT INTO menu (id, name, parent_id, path, icon, sort_order, permission, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    11, "流程管理", 0, "/workflow", "settings", 11, "workflow:view", 1);
            jdbcTemplate.update("INSERT INTO menu (id, name, parent_id, path, icon, sort_order, permission, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    12, "报表管理", 0, "/report", "bar-chart", 12, "report:view", 1);

            // 重新插入客户数据
            log.info("插入客户数据...");
            jdbcTemplate.update("INSERT INTO customer (name, id_no, phone, email) VALUES (?, ?, ?, ?)",
                    "张三", "110101199001011234", "13800138001", "zhangsan@example.com");
            jdbcTemplate.update("INSERT INTO customer (name, id_no, phone, email) VALUES (?, ?, ?, ?)",
                    "李四", "110101199002022345", "13800138002", "lisi@example.com");
            jdbcTemplate.update("INSERT INTO customer (name, id_no, phone, email) VALUES (?, ?, ?, ?)",
                    "王五", "110101199003033456", "13800138003", "wangwu@example.com");

            // 重新插入贷款申请数据
            log.info("插入贷款申请数据...");
            jdbcTemplate.update(
                    "INSERT INTO loan_application (application_no, customer_id, customer_name, customer_id_no, customer_phone, loan_amount, loan_term, loan_purpose, guarantee_type, status, current_stage, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    "A202604020001", 1, "张三", "110101199001011234", "13800138001", 50000.00, 12, "消费装修", "CREDIT", "INITIAL", "INITIAL", 1);
            jdbcTemplate.update(
                    "INSERT INTO loan_application (application_no, customer_id, customer_name, customer_id_no, customer_phone, loan_amount, loan_term, loan_purpose, guarantee_type, status, current_stage, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    "A202604020002", 2, "李四", "110101199002022345", "13800138002", 200000.00, 36, "企业经营", "MORTGAGE", "RISK", "RISK", 1);
            jdbcTemplate.update(
                    "INSERT INTO loan_application (application_no, customer_id, customer_name, customer_id_no, customer_phone, loan_amount, loan_term, loan_purpose, guarantee_type, status, current_stage, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    "A202604020003", 3, "王五", "110101199003033456", "13800138003", 80000.00, 24, "购车", "GUARANTEE", "APPROVED", "APPROVED", 1);

            // 重新插入角色 - 菜单关联
            log.info("插入角色菜单关联数据...");
            for (int i = 1; i <= 12; i++) {
                jdbcTemplate.update("INSERT INTO role_menu (role_id, menu_id) VALUES (?, ?)", 1, i);
            }

            // 重新插入用户 - 角色关联
            log.info("插入用户角色关联数据...");
            jdbcTemplate.update("INSERT INTO user_role (user_id, role_id) VALUES (?, ?)", 1, 1);

            // 恢复外键检查
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

            log.info("数据库修复完成！");

        } catch (Exception e) {
            log.error("数据库修复失败：{}", e.getMessage(), e);
        }
    }
}
