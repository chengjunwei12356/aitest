package com.example.aitest.dto;

import com.example.aitest.entity.GuaranteeType;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 贷款申请请求 DTO
 */
@Data
public class ApplicationDTO {
    private Long id;

    private Long customerId;

    @NotBlank(message = "客户姓名不能为空")
    @Length(max = 50, message = "客户姓名不能超过 50 个字符")
    private String customerName;

    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^\\d{17}[\\dXx]$", message = "身份证号格式不正确")
    private String customerIdNo;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String customerPhone;

    @NotNull(message = "贷款金额不能为空")
    @DecimalMin(value = "1000.00", message = "贷款金额至少为 1000 元")
    @DecimalMax(value = "10000000.00", message = "贷款金额不能超过 1000 万元")
    private BigDecimal loanAmount;

    @NotNull(message = "贷款期限不能为空")
    @Min(value = 1, message = "贷款期限至少 1 个月")
    @Max(value = 360, message = "贷款期限不能超过 360 个月")
    private Integer loanTerm;

    @Length(max = 100, message = "贷款用途不能超过 100 个字符")
    private String loanPurpose;

    @NotNull(message = "担保方式不能为空")
    private GuaranteeType guaranteeType;
}
