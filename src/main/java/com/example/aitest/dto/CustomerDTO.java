package com.example.aitest.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

/**
 * 客户信息请求 DTO
 */
@Data
public class CustomerDTO {
    private Long id;

    @Length(max = 50, message = "姓名不能超过 50 个字符")
    private String name;

    @Pattern(regexp = "^\\d{17}[\\dXx]$", message = "身份证号格式不正确")
    private String idNo;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Length(max = 255, message = "地址不能超过 255 个字符")
    private String address;
}
