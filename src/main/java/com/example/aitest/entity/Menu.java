package com.example.aitest.entity;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 菜单实体类
 */
@Data
@Builder
public class Menu {
    private Long id;
    private String name;
    private Long parentId;
    private String path;
    private String icon;
    private Integer sortOrder;
    private String permission;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
