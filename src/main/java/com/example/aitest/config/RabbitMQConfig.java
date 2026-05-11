package com.example.aitest.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 队列配置
 */
@Configuration
public class RabbitMQConfig {

    // 提醒生成队列
    public static final String REMINDER_QUEUE = "reminder.generated.queue";
    public static final String REMINDER_EXCHANGE = "reminder.exchange";
    public static final String REMINDER_ROUTING_KEY = "reminder.generated";

    // 通知推送队列
    public static final String NOTIFICATION_QUEUE = "notification.push.queue";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.push";

    /**
     * 提醒生成队列
     */
    @Bean
    public Queue reminderQueue() {
        return QueueBuilder.durable(REMINDER_QUEUE).build();
    }

    @Bean
    public DirectExchange reminderExchange() {
        return new DirectExchange(REMINDER_EXCHANGE);
    }

    @Bean
    public Binding reminderBinding(Queue reminderQueue, DirectExchange reminderExchange) {
        return BindingBuilder.bind(reminderQueue).to(reminderExchange).with(REMINDER_ROUTING_KEY);
    }

    /**
     * 通知推送队列
     */
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue).to(notificationExchange).with(NOTIFICATION_ROUTING_KEY);
    }
}
