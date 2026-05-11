package com.example.aitest.event;

import com.example.aitest.entity.CustomerReminder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 提醒创建事件
 * 在 CustomerAssistantService.createReminder() 中发布
 */
@Getter
public class ReminderCreatedEvent extends ApplicationEvent {

    private final CustomerReminder reminder;

    public ReminderCreatedEvent(Object source, CustomerReminder reminder) {
        super(source);
        this.reminder = reminder;
    }
}
