package com.example.aitest.controller;

import com.example.aitest.common.Result;
import com.example.aitest.entity.Notification;
import com.example.aitest.service.NotificationService;
import com.example.aitest.util.CurrentUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 站内消息 Controller
 * 提供消息查询和管理 REST API
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 获取当前用户的消息列表
     * GET /api/notifications?isRead=false
     */
    @GetMapping
    public Result<List<Notification>> list(
            @RequestParam(required = false) Boolean isRead) {
        Long userId = CurrentUserUtils.getUserId();
        List<Notification> notifications = notificationService.getNotifications(userId, isRead);
        return Result.success(notifications);
    }

    /**
     * 获取未读消息数量
     * GET /api/notifications/unread-count
     */
    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount() {
        Long userId = CurrentUserUtils.getUserId();
        int count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 标记为已读
     * PUT /api/notifications/{id}/read
     */
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        Long userId = CurrentUserUtils.getUserId();
        notificationService.markAsRead(id, userId);
        return Result.success();
    }

    /**
     * 全部标记为已读
     * POST /api/notifications/mark-all-read
     */
    @PostMapping("/mark-all-read")
    public Result<Void> markAllAsRead() {
        Long userId = CurrentUserUtils.getUserId();
        notificationService.markAllAsRead(userId);
        return Result.success();
    }

    /**
     * 删除消息
     * DELETE /api/notifications/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = CurrentUserUtils.getUserId();
        notificationService.deleteNotification(id, userId);
        return Result.success();
    }
}
