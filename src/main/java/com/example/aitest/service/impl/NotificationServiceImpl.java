package com.example.aitest.service.impl;

import com.example.aitest.entity.Notification;
import com.example.aitest.mapper.NotificationMapper;
import com.example.aitest.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 站内消息服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    public List<Notification> getNotifications(Long userId, Boolean isRead) {
        log.info("获取用户消息列表，userId: {}, isRead: {}", userId, isRead);
        return notificationMapper.findByUserId(userId, isRead);
    }

    @Override
    public int getUnreadCount(Long userId) {
        log.info("获取未读消息数量，userId: {}", userId);
        return notificationMapper.countUnread(userId);
    }

    @Override
    @Transactional
    public Long createNotification(Notification notification) {
        log.info("创建站内消息，userId: {}, title: {}", notification.getUserId(), notification.getTitle());

        if (notification.getIsRead() == null) {
            notification.setIsRead(false);
        }
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(LocalDateTime.now());
        }

        notificationMapper.insert(notification);
        log.info("站内消息创建成功，id: {}", notification.getId());
        return notification.getId();
    }

    @Override
    @Transactional
    public boolean markAsRead(Long id, Long userId) {
        log.info("标记消息为已读，id: {}, userId: {}", id, userId);
        int rows = notificationMapper.markAsRead(id, userId);
        boolean success = rows > 0;
        if (success) {
            log.info("消息标记已读成功，id: {}", id);
        } else {
            log.warn("消息标记已读失败，可能不存在或无权限，id: {}", id);
        }
        return success;
    }

    @Override
    @Transactional
    public int markAllAsRead(Long userId) {
        log.info("全部标记为已读，userId: {}", userId);
        int rows = notificationMapper.markAllAsRead(userId);
        log.info("批量标记已读完成，userId: {}, 影响行数: {}", userId, rows);
        return rows;
    }

    @Override
    @Transactional
    public boolean deleteNotification(Long id, Long userId) {
        log.info("删除消息，id: {}, userId: {}", id, userId);
        int rows = notificationMapper.deleteById(id, userId);
        boolean success = rows > 0;
        if (success) {
            log.info("消息删除成功，id: {}", id);
        } else {
            log.warn("消息删除失败，可能不存在或无权限，id: {}", id);
        }
        return success;
    }
}
