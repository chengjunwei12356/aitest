package com.example.aitest.mq;

import com.example.aitest.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 提醒消息生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送提醒生成消息
     */
    public void sendReminderGenerated(Long reminderId, Long userId, String type) {
        Map<String, Object> message = Map.of(
                "reminderId", reminderId,
                "userId", userId,
                "type", type,
                "timestamp", System.currentTimeMillis()
        );
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.REMINDER_EXCHANGE,
                RabbitMQConfig.REMINDER_ROUTING_KEY,
                message
        );
        log.info("发送提醒生成消息: reminderId={}, userId={}", reminderId, userId);
    }
}
