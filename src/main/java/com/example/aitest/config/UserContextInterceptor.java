package com.example.aitest.config;

import com.example.aitest.util.CurrentUserUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户上下文拦截器
 *
 * 从请求头 X-User-Id 中提取用户 ID，存储到 CurrentUserUtils 中。
 * 请求完成后自动清理 ThreadLocal，防止内存泄漏。
 *
 * 注意：当前使用简单的 Token 认证，未来可替换为 Spring Security 的 SecurityContextHolder。
 */
@Component
public class UserContextInterceptor implements HandlerInterceptor {

    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userIdHeader = request.getHeader(USER_ID_HEADER);
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                Long userId = Long.parseLong(userIdHeader);
                CurrentUserUtils.setUserId(userId);
            } catch (NumberFormatException e) {
                // 用户 ID 格式无效，跳过设置
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                               Object handler, @Nullable Exception ex) {
        CurrentUserUtils.clear();
    }
}
