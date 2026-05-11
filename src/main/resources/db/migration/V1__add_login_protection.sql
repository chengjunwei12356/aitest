-- Add login protection fields to user table
ALTER TABLE `user`
ADD COLUMN `failed_login_attempts` INT DEFAULT 0 COMMENT '连续登录失败次数',
ADD COLUMN `locked_until` DATETIME DEFAULT NULL COMMENT '账户锁定截止时间',
ADD COLUMN `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间';

-- Create index for locked accounts query
CREATE INDEX `idx_locked_until` ON `user` (`locked_until`);
