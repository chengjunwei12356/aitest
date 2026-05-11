package com.example.aitest.controller;

import com.example.aitest.common.Result;
import com.example.aitest.dto.CaptchaResponse;
import com.example.aitest.dto.LoginRequest;
import com.example.aitest.dto.LoginResponse;
import com.example.aitest.service.CaptchaService;
import com.example.aitest.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "用户登录、验证码、登出等认证相关接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final CaptchaService captchaService;

    /**
     * 获取验证码
     */
    @Operation(summary = "获取验证码", description = "生成图形验证码用于登录验证")
    @GetMapping("/captcha")
    public Result<CaptchaResponse> getCaptcha() {
        return Result.success(captchaService.generateCaptcha());
    }

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录", description = "使用用户名、密码和验证码进行登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }

    /**
     * 用户登出
     */
    @Operation(summary = "用户登出", description = "退出当前登录会话")
    @PostMapping("/logout")
    public Result<Void> logout() {
        // TODO: 实现登出逻辑（如果使用 JWT，需要实现黑名单机制）
        return Result.success();
    }
}
