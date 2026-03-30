package com.example.aitest.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 贷款申请实体
 */
@Data
public class LoanApplication {
    /**
     * 申请 ID
     */
    private Long id;

    /**
     * 申请编号
     */
    private String applicationNo;

    /**
     * 客户 ID
     */
    private Long customerId;

    /**
     * 客户姓名
     */
    private String customerName;

    /**
     * 身份证号
     */
    private String customerIdNo;

    /**
     * 手机号
     */
    private String customerPhone;

    /**
     * 申请金额
     */
    private BigDecimal loanAmount;

    /**
     * 贷款期限 (月)
     */
    private Integer loanTerm;

    /**
     * 贷款用途
     */
    private String loanPurpose;

    /**
     * 担保方式
     */
    private GuaranteeType guaranteeType;

    /**
     * 当前阶段
     */
    private String currentStage;

    /**
     * 状态
     */
    private ApplicationStatus status;

    /**
     * 当前处理人 ID
     */
    private Long assignedTo;

    /**
     * 创建人 ID
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
