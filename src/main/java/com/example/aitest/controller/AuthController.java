package com.example.aitest.controller;

import com.example.aitest.common.Result;
import com.example.aitest.dto.CaptchaResponse;
import com.example.aitest.dto.LoginRequest;
import com.example.aitest.dto.LoginResponse;
import com.example.aitest.service.CaptchaService;
import com.example.aitest.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final CaptchaService captchaService;

    /**
     * 获取验证码
     */
    @GetMapping("/captcha")
    public Result<CaptchaResponse> getCaptcha() {
        return Result.success(captchaService.generateCaptcha());
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        // TODO: 实现登出逻辑（如果使用 JWT，需要实现黑名单机制）
        return Result.success();
    }
}
