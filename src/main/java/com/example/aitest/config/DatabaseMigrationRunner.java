package com.example.aitest.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 数据库迁移运行器 - 自动执行必要的表结构变更
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseMigrationRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始执行数据库迁移检查...");
        
        try {
            // 检查并添加登录保护字段
            addLoginProtectionFields();
            
            log.info("数据库迁移完成");
        } catch (Exception e) {
            log.error("数据库迁移失败", e);
        }
    }

    /**
     * 添加登录保护相关字段
     */
    private void addLoginProtectionFields() {
        try {
            // 检查字段是否已存在
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' " +
                "AND COLUMN_NAME = 'failed_login_attempts'", 
                Integer.class
            );

            if (count != null && count == 0) {
                log.info("添加登录保护字段到 user 表...");
                
                jdbcTemplate.execute(
                    "ALTER TABLE `user` ADD COLUMN `failed_login_attempts` INT DEFAULT 0 COMMENT '连续登录失败次数'"
                );
                
                jdbcTemplate.execute(
                    "ALTER TABLE `user` ADD COLUMN `locked_until` DATETIME DEFAULT NULL COMMENT '账户锁定截止时间'"
                );
                
                jdbcTemplate.execute(
                    "ALTER TABLE `user` ADD COLUMN `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间'"
                );
                
                jdbcTemplate.execute(
                    "CREATE INDEX `idx_locked_until` ON `user` (`locked_until`)"
                );
                
                log.info("登录保护字段添加成功");
            } else {
                log.info("登录保护字段已存在，跳过");
            }
        } catch (Exception e) {
            log.error("添加登录保护字段失败", e);
        }
    }
}
