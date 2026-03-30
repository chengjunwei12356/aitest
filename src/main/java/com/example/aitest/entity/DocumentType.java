package com.example.aitest.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 材料类型枚举
 */
@Getter
@AllArgsConstructor
public enum DocumentType {
    ID_CARD_FRONT("身份证正面", true),
    ID_CARD_BACK("身份证反面", true),
    INCOME_PROOF("收入证明", true),
    BANK_STATEMENT("银行流水", true),
    PROPERTY_CERT("房产证", false),
    VEHICLE_CERT("车辆证明", false),
    OTHER("其他材料", false);

    private final String description;
    private final boolean required;
}
