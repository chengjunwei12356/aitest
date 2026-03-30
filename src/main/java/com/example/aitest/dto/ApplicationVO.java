package com.example.aitest.dto;

import com.example.aitest.entity.ApplicationStatus;
import com.example.aitest.entity.GuaranteeType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 贷款申请响应 VO
 */
@Data
@Builder
public class ApplicationVO {
    private Long id;
    private String applicationNo;
    private String customerName;
    private String customerIdNo;
    private String customerPhone;
    private BigDecimal loanAmount;
    private Integer loanTerm;
    private String loanPurpose;
    private GuaranteeType guaranteeType;
    private ApplicationStatus status;
    private String currentStage;
    private String currentHandler;
    private String creatorName;
    private LocalDateTime createdAt;
    private List<DocumentVO> documents;
    private List<ApprovalRecordVO> records;
}
