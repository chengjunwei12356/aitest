package com.example.aitest.util;

/**
 * 当前用户上下文工具类
 *
 * 使用 ThreadLocal 存储当前请求的用户 ID，用于在 Service 层获取当前登录用户。
 * 由 UserContextInterceptor 在请求开始时设置，请求结束后清理。
 */
public class CurrentUserUtils {

    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前用户 ID
     */
    public static void setUserId(Long userId) {
        USER_ID_HOLDER.set(userId);
    }

    /**
     * 获取当前用户 ID
     * @return 用户 ID，如果未设置则返回 null
     */
    public static Long getUserId() {
        return USER_ID_HOLDER.get();
    }

    /**
     * 清除当前线程的用户上下文
     */
    public static void clear() {
        USER_ID_HOLDER.remove();
    }
}
