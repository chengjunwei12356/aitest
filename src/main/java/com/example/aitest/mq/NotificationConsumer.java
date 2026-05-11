package com.example.aitest.mq;

import com.example.aitest.config.RabbitMQConfig;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 通知消息消费者 - 异步处理通知推送
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    /**
     * 监听通知推送队列
     * 异步处理：WebSocket推送、短信/邮件通知等
     */
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleNotificationPush(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            Object payload = message.getBody();
            log.info("收到通知推送消息: {}", payload);

            // TODO: 异步处理业务逻辑
            // 1. WebSocket实时推送
            // 2. 短信通知（重要提醒）
            // 3. 邮件通知

            channel.basicAck(deliveryTag, false);
            log.info("通知消息处理成功: deliveryTag={}", deliveryTag);
        } catch (Exception e) {
            log.error("通知消息处理失败", e);
            channel.basicNack(deliveryTag, false, true);
        }
    }
}
