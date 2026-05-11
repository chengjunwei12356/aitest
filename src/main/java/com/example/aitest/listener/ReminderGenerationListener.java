package com.example.aitest.listener;

import com.example.aitest.entity.CustomerReminder;
import com.example.aitest.entity.LoanApplication;
import com.example.aitest.entity.ReminderType;
import com.example.aitest.event.ApplicationStatusChangedEvent;
import com.example.aitest.service.CustomerAssistantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 提醒生成监听器
 * 监听贷款申请状态变更事件，自动生成相关提醒
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ReminderGenerationListener {

    private final CustomerAssistantService customerAssistantService;

    @EventListener
    @Transactional
    public void handleStatusChange(ApplicationStatusChangedEvent event) {
        log.info("检测到申请状态变更: {} -> {}, applicationId: {}",
                event.getOldStatus(), event.getNewStatus(),
                event.getApplication().getId());

        switch (event.getNewStatus()) {
            case INITIAL:
                createApprovalReminder(event.getApplication(), "初审");
                break;
            case FINAL:
                createApprovalReminder(event.getApplication(), "终审");
                break;
            case RISK:
                createApprovalReminder(event.getApplication(), "风控审批");
                break;
            case REJECTED:
                createRejectionReminder(event.getApplication());
                break;
            case APPROVED:
                createApprovedReminder(event.getApplication());
                break;
            default:
                log.debug("状态 {} 无需生成提醒", event.getNewStatus());
        }
    }

    /**
     * 创建审批提醒（给审批人）
     */
    private void createApprovalReminder(LoanApplication app, String stageName) {
        if (app.getAssignedTo() == null) {
            log.warn("申请 {} 未分配处理人，跳过生成提醒", app.getId());
            return;
        }

        CustomerReminder reminder = new CustomerReminder();
        reminder.setUserId(app.getAssignedTo());
        reminder.setCustomerId(app.getCustomerId());
        reminder.setReminderType(ReminderType.APPROVAL_TIMEOUT);
        reminder.setTitle(String.format("贷款申请待%s", stageName));
        reminder.setContent(String.format("申请编号: %s, 客户: %s, 金额: %s元",
                app.getApplicationNo(), app.getCustomerName(), app.getLoanAmount()));
        reminder.setPriority(3);  // 高优先级
        reminder.setStatus("PENDING");
        reminder.setDueDate(LocalDateTime.now().plusDays(2));  // 2天内处理

        customerAssistantService.createReminder(reminder);
        log.info("已生成{}提醒，reminderId: {}", stageName, reminder.getId());
    }

    /**
     * 创建拒绝提醒（给客户经理）
     */
    private void createRejectionReminder(LoanApplication app) {
        if (app.getCreatedBy() == null) {
            log.warn("申请 {} 无创建人信息，跳过生成提醒", app.getId());
            return;
        }

        CustomerReminder reminder = new CustomerReminder();
        reminder.setUserId(app.getCreatedBy());
        reminder.setCustomerId(app.getCustomerId());
        reminder.setReminderType(ReminderType.FOLLOWUP_REQUIRED);
        reminder.setTitle("贷款申请已被拒绝");
        reminder.setContent(String.format("申请编号: %s 已被拒绝，请及时联系客户说明原因",
                app.getApplicationNo()));
        reminder.setPriority(2);
        reminder.setStatus("PENDING");
        reminder.setDueDate(LocalDateTime.now().plusDays(1));

        customerAssistantService.createReminder(reminder);
        log.info("已生成拒绝提醒，reminderId: {}", reminder.getId());
    }

    /**
     * 创建批准提醒（给客户经理，准备放款）
     */
    private void createApprovedReminder(LoanApplication app) {
        if (app.getCreatedBy() == null) {
            log.warn("申请 {} 无创建人信息，跳过生成提醒", app.getId());
            return;
        }

        CustomerReminder reminder = new CustomerReminder();
        reminder.setUserId(app.getCreatedBy());
        reminder.setCustomerId(app.getCustomerId());
        reminder.setReminderType(ReminderType.FOLLOWUP_REQUIRED);
        reminder.setTitle("贷款申请已批准");
        reminder.setContent(String.format("申请编号: %s 已批准，请准备放款手续",
                app.getApplicationNo()));
        reminder.setPriority(2);
        reminder.setStatus("PENDING");
        reminder.setDueDate(LocalDateTime.now().plusDays(3));

        customerAssistantService.createReminder(reminder);
        log.info("已生成批准提醒，reminderId: {}", reminder.getId());
    }
}
