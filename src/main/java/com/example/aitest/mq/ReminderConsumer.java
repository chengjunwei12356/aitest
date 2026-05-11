package com.example.aitest.mq;

import com.example.aitest.config.RabbitMQConfig;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 提醒消息消费者 - 异步处理提醒生成后的业务逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderConsumer {

    /**
     * 监听提醒生成队列
     * 异步处理：发送站内消息、推送通知等
     */
    @RabbitListener(queues = RabbitMQConfig.REMINDER_QUEUE)
    public void handleReminderGenerated(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            // 解析消息
            Object payload = message.getBody();
            log.info("收到提醒生成消息: {}", payload);

            // TODO: 异步处理业务逻辑
            // 1. 发送站内消息给客户经理
            // 2. 推送移动端通知
            // 3. 记录操作日志

            // 手动ACK
            channel.basicAck(deliveryTag, false);
            log.info("提醒消息处理成功: deliveryTag={}", deliveryTag);
        } catch (Exception e) {
            log.error("提醒消息处理失败", e);
            // 拒绝消息，重新入队
            channel.basicNack(deliveryTag, false, true);
        }
    }
}
