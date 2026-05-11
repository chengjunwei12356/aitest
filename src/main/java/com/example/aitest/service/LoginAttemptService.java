package com.example.aitest.service;

import com.example.aitest.entity.User;
import com.example.aitest.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 登录尝试管理服务 - 防止暴力破解
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final UserMapper userMapper;

    // 最大失败次数
    private static final int MAX_FAILED_ATTEMPTS = 5;
    // 锁定时间（分钟）
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    /**
     * 检查账户是否被锁定
     */
    public boolean isAccountLocked(User user) {
        if (user.getLockedUntil() == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(user.getLockedUntil());
    }

    /**
     * 记录登录失败
     */
    public void recordLoginFailure(User user) {
        int failedAttempts = user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0;
        failedAttempts++;

        LocalDateTime lockedUntil = null;
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            lockedUntil = LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES);
            log.warn("账户已锁定: username={}, failedAttempts={}, lockedUntil={}", 
                    user.getUsername(), failedAttempts, lockedUntil);
        } else {
            log.warn("登录失败: username={}, failedAttempts={}/{}", 
                    user.getUsername(), failedAttempts, MAX_FAILED_ATTEMPTS);
        }

        userMapper.updateLoginAttempts(user.getId(), failedAttempts, lockedUntil);
    }

    /**
     * 记录登录成功 - 重置失败计数
     */
    public void recordLoginSuccess(User user) {
        LocalDateTime now = LocalDateTime.now();
        userMapper.updateLastLoginAt(user.getId(), now);
        log.info("登录成功: username={}", user.getUsername());
    }

    /**
     * 获取剩余解锁时间（分钟）
     */
    public long getRemainingLockoutMinutes(User user) {
        if (user.getLockedUntil() == null) {
            return 0;
        }
        long minutes = java.time.Duration.between(LocalDateTime.now(), user.getLockedUntil()).toMinutes();
        return Math.max(0, minutes);
    }
}
