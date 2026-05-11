package com.example.aitest.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 担保方式枚举
 */
@Getter
@AllArgsConstructor
public enum GuaranteeType {
    CREDIT("信用贷款"),
    MORTGAGE("抵押贷款"),
    PLEDGE("质押贷款"),
    GUARANTEE("保证贷款");

    private final String description;

    @JsonCreator
    public static GuaranteeType fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        // 支持大小写不敏感的匹配
        for (GuaranteeType type : GuaranteeType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid GuaranteeType: " + value);
    }

    @JsonValue
    @Override
    public String toString() {
        return this.name();
    }
}
