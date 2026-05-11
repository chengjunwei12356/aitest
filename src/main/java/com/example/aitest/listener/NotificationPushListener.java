package com.example.aitest.listener;

import com.example.aitest.entity.CustomerReminder;
import com.example.aitest.entity.Notification;
import com.example.aitest.event.ReminderCreatedEvent;
import com.example.aitest.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 通知推送监听器
 * 监听提醒创建事件，自动推送站内消息
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationPushListener {

    private final NotificationService notificationService;

    @EventListener
    @Transactional
    public void handleReminderCreated(ReminderCreatedEvent event) {
        CustomerReminder reminder = event.getReminder();

        log.info("检测到提醒创建，userId: {}, title: {}",
                reminder.getUserId(), reminder.getTitle());

        // 创建站内消息
        Notification notification = new Notification();
        notification.setUserId(reminder.getUserId());
        notification.setTitle(reminder.getTitle());
        notification.setContent(reminder.getContent());
        notification.setType("REMINDER");
        notification.setRelatedId(reminder.getId());
        notification.setIsRead(false);
        notification.setPriority(reminder.getPriority());

        Long notificationId = notificationService.createNotification(notification);
        log.info("已推送站内消息，notificationId: {}, userId: {}, title: {}",
                notificationId, reminder.getUserId(), reminder.getTitle());
    }
}
