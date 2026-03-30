package com.example.aitest.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审批记录响应 VO
 */
@Data
@Builder
public class ApprovalRecordVO {
    private Long id;
    private String stage;
    private String approverName;
    private String action;
    private String comment;
    private LocalDateTime createdAt;
}
