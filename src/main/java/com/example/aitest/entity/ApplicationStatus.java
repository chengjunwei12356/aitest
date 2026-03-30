package com.example.aitest.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 贷款申请状态枚举
 */
@Getter
@AllArgsConstructor
public enum ApplicationStatus {
    DRAFT("草稿"),
    INITIAL("待初审"),
    FINAL("待终审"),
    RISK("待风控审批"),
    APPROVED("已批准"),
    REJECTED("已拒绝"),
    PENDING_LOAN("待放款"),
    COMPLETED("已完成");

    private final String description;
}
