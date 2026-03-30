package com.example.aitest.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客户信息实体
 */
@Data
public class Customer {
    /**
     * 客户 ID
     */
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 身份证号
     */
    private String idNo;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 地址
     */
    private String address;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
