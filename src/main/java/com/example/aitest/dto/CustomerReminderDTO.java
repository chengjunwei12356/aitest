package com.example.aitest.dto;

import com.example.aitest.entity.ReminderType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客户提醒 DTO
 */
@Data
public class CustomerReminderDTO {
    /**
     * 提醒 ID
     */
    private Long id;

    /**
     * 客户经理用户 ID
     */
    private Long userId;

    /**
     * 客户 ID
     */
    private Long customerId;

    /**
     * 提醒类型
     */
    private ReminderType reminderType;

    /**
     * 提醒类型描述
     */
    private String reminderTypeDescription;

    /**
     * 提醒标题
     */
    private String title;

    /**
     * 提醒内容
     */
    private String content;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 状态
     */
    private String status;

    /**
     * 截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime dueDate;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;

    /**
     * 客户姓名
     */
    private String customerName;

    /**
     * 客户手机号
     */
    private String customerPhone;

    /**
     * 解决备注
     */
    private String resolutionNote;
}