package com.example.aitest.event;

import com.example.aitest.entity.ApplicationStatus;
import com.example.aitest.entity.LoanApplication;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 贷款申请状态变更事件
 * 在 submit()、approve() 等方法中发布
 */
@Getter
public class ApplicationStatusChangedEvent extends ApplicationEvent {

    private final LoanApplication application;
    private final ApplicationStatus oldStatus;
    private final ApplicationStatus newStatus;
    private final String stage;
    private final Long operatorId;

    public ApplicationStatusChangedEvent(Object source, LoanApplication application,
                                         ApplicationStatus oldStatus,
                                         ApplicationStatus newStatus,
                                         String stage, Long operatorId) {
        super(source);
        this.application = application;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.stage = stage;
        this.operatorId = operatorId;
    }
}
