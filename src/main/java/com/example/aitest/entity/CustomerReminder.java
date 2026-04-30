package com.example.aitest.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客户提醒实体类
 */
@Data
public class CustomerReminder {
    /**
     * 提醒 ID
     */
    private Long id;

    /**
     * 客户 ID
     */
    private Long customerId;

    /**
     * 提醒类型
     */
    private ReminderType reminderType;

    /**
     * 提醒标题
     */
    private String title;

    /**
     * 提醒内容
     */
    private String content;

    /**
     * 优先级：1-低, 2-中, 3-高
     */
    private Integer priority;

    /**
     * 状态：PENDING-待处理, NOTIFIED-已通知, RESOLVED-已解决
     */
    private String status;

    /**
     * 截止时间
     */
    private LocalDateTime dueDate;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}