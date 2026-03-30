package com.example.aitest.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 审批记录实体
 */
@Data
public class ApprovalRecord {
    /**
     * 记录 ID
     */
    private Long id;

    /**
     * 申请 ID
     */
    private Long applicationId;

    /**
     * 审批阶段
     */
    private String stage;

    /**
     * 审批人 ID
     */
    private Long approverId;

    /**
     * 操作
     */
    private String action;

    /**
     * 审批意见
     */
    private String comment;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
