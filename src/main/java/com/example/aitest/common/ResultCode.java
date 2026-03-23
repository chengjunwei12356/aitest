package com.example.aitest.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "成功"),
    ERROR(400, "失败"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    // 业务错误码
    USERNAME_OR_PASSWORD_ERROR(1001, "用户名或密码错误"),
    USER_DISABLED(1002, "用户已被禁用"),
    CAPTCHA_ERROR(1003, "验证码错误"),
    CAPTCHA_EXPIRED(1004, "验证码已过期"),
    USER_NOT_FOUND(1005, "用户不存在"),
    USERNAME_EXISTS(1006, "用户名已存在");

    private final Integer code;
    private final String message;
}
