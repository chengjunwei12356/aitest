package com.example.aitest.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
public class User {
    /**
     * 用户 ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（BCrypt 加密）
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 连续登录失败次数
     */
    private Integer failedLoginAttempts;

    /**
     * 账户锁定截止时间
     */
    private LocalDateTime lockedUntil;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
