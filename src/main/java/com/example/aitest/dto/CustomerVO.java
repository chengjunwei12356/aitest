package com.example.aitest.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户信息响应 VO
 */
@Data
@Builder
public class CustomerVO {
    private Long id;
    private String name;
    private String idNo;
    private String phone;
    private String email;
    private String address;
    private LocalDateTime createdAt;
}
