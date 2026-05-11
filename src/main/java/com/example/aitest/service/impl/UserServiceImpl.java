package com.example.aitest.service.impl;

import com.example.aitest.common.ResultCode;
import com.example.aitest.config.BusinessException;
import com.example.aitest.dto.LoginRequest;
import com.example.aitest.dto.LoginResponse;
import com.example.aitest.entity.User;
import com.example.aitest.mapper.UserMapper;
import com.example.aitest.mapper.UserRoleMapper;
import com.example.aitest.service.CaptchaService;
import com.example.aitest.service.LoginAttemptService;
import com.example.aitest.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final CaptchaService captchaService;
    private final LoginAttemptService loginAttemptService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("用户登录，username: {}", request.getUsername());

        // 1. 验证验证码
        if (!captchaService.validateCaptcha(request.getSessionId(), request.getCaptcha())) {
            throw new BusinessException(ResultCode.CAPTCHA_ERROR);
        }

        // 2. 查询用户
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 3. 检查账户是否被锁定
        if (loginAttemptService.isAccountLocked(user)) {
            long remainingMinutes = loginAttemptService.getRemainingLockoutMinutes(user);
            log.warn("账户已锁定: username={}, 剩余{}分钟", user.getUsername(), remainingMinutes);
            throw new BusinessException(ResultCode.ACCOUNT_LOCKED);
        }

        // 4. 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 5. 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // 记录登录失败
            loginAttemptService.recordLoginFailure(user);
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 6. 登录成功 - 重置失败计数
        loginAttemptService.recordLoginSuccess(user);

        // 7. 生成 token（简单实现，实际项目应使用 JWT）
        String token = generateToken(user);

        log.info("用户登录成功，username: {}", user.getUsername());

        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .token(token)
                .success(true)
                .build();
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        return userRoleMapper.findRoleIdsByUserId(userId);
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.deleteByUserId(userId);
        if (roleIds != null) {
            for (Long roleId : roleIds) {
                userRoleMapper.insert(userId, roleId);
            }
        }
        log.info("分配角色成功，userId: {}, roleCount: {}", userId, roleIds != null ? roleIds.size() : 0);
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        String hashed = passwordEncoder.encode(newPassword);
        userMapper.updatePassword(userId, hashed);
        log.info("重置密码成功，userId: {}", userId);
    }

    @Override
    @Transactional
    public void updateStatus(Long userId, Integer status) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        userMapper.updateStatus(userId, status);
        log.info("更新用户状态成功，userId: {}, status: {}", userId, status);
    }

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    /**
     * 生成 Token（简单实现）
     */
    private String generateToken(User user) {
        // 实际项目应使用 JWT 或其他安全的 Token 生成方式
        return "token_" + user.getUsername() + "_" + System.currentTimeMillis();
    }
}
