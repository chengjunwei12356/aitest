package com.example.aitest.entity;

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
}
