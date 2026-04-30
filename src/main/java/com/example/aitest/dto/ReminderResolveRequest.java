package com.example.aitest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 提醒解决请求 DTO
 */
@Data
public class ReminderResolveRequest {
    /**
     * 提醒 ID（必填）
     */
    @NotNull(message = "提醒 ID 不能为空")
    private Long reminderId;

    /**
     * 解决备注
     */
    private String note;
}