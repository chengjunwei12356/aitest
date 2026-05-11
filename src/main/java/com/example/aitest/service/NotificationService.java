package com.example.aitest.service;

import com.example.aitest.entity.Notification;

import java.util.List;

/**
 * 站内消息服务接口
 */
public interface NotificationService {

    /**
     * 获取用户消息列表
     *
     * @param userId 用户 ID
     * @param isRead 是否已读（null 表示全部）
     * @return 消息列表
     */
    List<Notification> getNotifications(Long userId, Boolean isRead);

    /**
     * 获取未读消息数量
     *
     * @param userId 用户 ID
     * @return 未读消息数
     */
    int getUnreadCount(Long userId);

    /**
     * 创建消息
     *
     * @param notification 消息对象
     * @return 消息 ID
     */
    Long createNotification(Notification notification);

    /**
     * 标记为已读
     *
     * @param id     消息 ID
     * @param userId 用户 ID
     * @return 是否成功
     */
    boolean markAsRead(Long id, Long userId);

    /**
     * 全部标记为已读
     *
     * @param userId 用户 ID
     * @return 影响行数
     */
    int markAllAsRead(Long userId);

    /**
     * 删除消息
     *
     * @param id     消息 ID
     * @param userId 用户 ID
     * @return 是否成功
     */
    boolean deleteNotification(Long id, Long userId);
}
