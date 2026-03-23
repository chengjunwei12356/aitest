package com.example.aitest.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 验证码实体类
 */
@Data
public class Captcha {
    /**
     * ID
     */
    private Long id;

    /**
     * 会话 ID
     */
    private String sessionId;

    /**
     * 验证码
     */
    private String code;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 是否已使用：0-未使用，1-已使用
     */
    private Integer used;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
