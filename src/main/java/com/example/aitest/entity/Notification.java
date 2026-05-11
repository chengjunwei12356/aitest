package com.example.aitest.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 站内消息实体
 */
@Data
public class Notification {
    /**
     * 消息 ID
     */
    private Long id;

    /**
     * 接收用户 ID
     */
    private Long userId;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型: REMINDER-提醒, APPROVAL-审批, SYSTEM-系统
     */
    private String type;

    /**
     * 关联业务 ID (如 reminder_id, application_id)
     */
    private Long relatedId;

    /**
     * 是否已读: false-未读, true-已读
     */
    private Boolean isRead;

    /**
     * 优先级: 1-低, 2-中, 3-高
     */
    private Integer priority;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 阅读时间
     */
    private LocalDateTime readAt;
}
