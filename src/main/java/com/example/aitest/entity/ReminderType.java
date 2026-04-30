package com.example.aitest.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 提醒类型枚举
 */
@Getter
@AllArgsConstructor
public enum ReminderType {
    APPLICATION_EXPIRE("申请即将过期"),
    MATERIAL_EXPIRE("材料即将过期"),
    APPROVAL_TIMEOUT("审批超时"),
    FOLLOWUP_REQUIRED("需要回访"),
    LOAN_RENEW("续贷提醒");

    private final String description;

    @JsonCreator
    public static ReminderType fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        // 支持大小写不敏感的匹配
        for (ReminderType type : ReminderType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid ReminderType: " + value);
    }

    @JsonValue
    @Override
    public String toString() {
        return this.name();
    }
}