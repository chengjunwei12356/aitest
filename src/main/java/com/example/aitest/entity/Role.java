package com.example.aitest.entity;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 角色实体类
 */
@Data
@Builder
public class Role {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
