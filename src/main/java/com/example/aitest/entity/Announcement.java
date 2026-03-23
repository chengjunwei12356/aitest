package com.example.aitest.entity;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 公告实体类
 */
@Data
@Builder
public class Announcement {
    private Long id;
    private String title;
    private String content;
    private Integer publishStatus;
    private LocalDateTime publishTime;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
