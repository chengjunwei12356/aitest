package com.example.aitest.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 知识库分类枚举
 */
@Getter
@AllArgsConstructor
public enum KnowledgeCategory {
    BUSINESS_PROCESS("业务流程"),
    FAQ("常见问题"),
    PRODUCT_INFO("产品信息"),
    APPROVAL_STANDARD("审批标准");

    private final String description;

    @JsonCreator
    public static KnowledgeCategory fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        // 支持大小写不敏感的匹配
        for (KnowledgeCategory category : KnowledgeCategory.values()) {
            if (category.name().equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid KnowledgeCategory: " + value);
    }

    @JsonValue
    @Override
    public String toString() {
        return this.name();
    }
}