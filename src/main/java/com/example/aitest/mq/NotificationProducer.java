package com.example.aitest.mq;

import com.example.aitest.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 通知消息生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送通知推送消息
     */
    public void sendNotificationPush(Long notificationId, Long userId, String title) {
        Map<String, Object> message = Map.of(
                "notificationId", notificationId,
                "userId", userId,
                "title", title,
                "timestamp", System.currentTimeMillis()
        );
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                message
        );
        log.info("发送通知推送消息: notificationId={}, userId={}", notificationId, userId);
    }
}
