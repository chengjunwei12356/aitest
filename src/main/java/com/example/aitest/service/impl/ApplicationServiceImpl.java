package com.example.aitest.service.impl;

import com.example.aitest.common.ResultCode;
import com.example.aitest.config.BusinessException;
import com.example.aitest.entity.*;
import com.example.aitest.event.ApplicationStatusChangedEvent;
import com.example.aitest.mapper.*;
import com.example.aitest.service.ApplicationService;
import com.example.aitest.service.CustomerAssistantService;
import com.example.aitest.util.CurrentUserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 贷款申请服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final LoanApplicationMapper loanApplicationMapper;
    private final CustomerMapper customerMapper;
    private final ApprovalRecordMapper approvalRecordMapper;
    private final CustomerAssistantService customerAssistantService;
    private final ApplicationEventPublisher eventPublisher;

    // 风控审批阈值：10 万
    private static final BigDecimal RISK_APPROVAL_THRESHOLD = new BigDecimal("100000");

    @Override
    public LoanApplication findById(Long id) {
        log.info("查询贷款申请，id: {}", id);
        LoanApplication application = loanApplicationMapper.findById(id);
        if (application == null) {
            throw new BusinessException(ResultCode.APPLICATION_NOT_FOUND);
        }
        
        // 数据隔离：普通用户只能查看自己创建的申请
        Long currentUserId = CurrentUserUtils.getUserId();
        if (currentUserId != null && !isAdmin(currentUserId)) {
            if (!application.getCreatedBy().equals(currentUserId)) {
                throw new BusinessException(ResultCode.APPLICATION_NO_PERMISSION);
            }
        }
        
        return application;
    }

    @Override
    public LoanApplication findByApplicationNo(String applicationNo) {
        log.info("查询贷款申请，applicationNo: {}", applicationNo);
        return loanApplicationMapper.findByApplicationNo(applicationNo);
    }

    @Override
    public List<LoanApplication> findAll(String status, String guaranteeType,
                                         BigDecimal minAmount, BigDecimal maxAmount,
                                         String keyword, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("查询贷款申请列表，status: {}, guaranteeType: {}, minAmount: {}, maxAmount: {}, keyword: {}",
                status, guaranteeType, minAmount, maxAmount, keyword);
        
        Long currentUserId = CurrentUserUtils.getUserId();
        
        // 数据隔离：普通用户只能查看自己创建的申请，管理员可以查看所有
        if (currentUserId != null && !isAdmin(currentUserId)) {
            log.info("非管理员用户，只查询 createdBy={} 的申请", currentUserId);
            return loanApplicationMapper.findByCreatedBy(currentUserId, status, guaranteeType,
                    minAmount != null ? minAmount.toString() : null,
                    maxAmount != null ? maxAmount.toString() : null,
                    keyword,
                    startDate != null ? startDate.toString() : null,
                    endDate != null ? endDate.toString() : null);
        }
        
        // 管理员查询所有
        return loanApplicationMapper.findAll(status, guaranteeType,
                minAmount != null ? minAmount.toString() : null,
                maxAmount != null ? maxAmount.toString() : null,
                keyword,
                startDate != null ? startDate.toString() : null,
                endDate != null ? endDate.toString() : null);
    }
    
    /**
     * 检查是否为管理员（简单实现，实际应查询角色表）
     */
    private boolean isAdmin(Long userId) {
        // 假设 userId=1 为管理员，实际项目中应查询 user_role 和 role 表
        return userId != null && userId == 1L;
    }

    @Override
    @Transactional
    public LoanApplication create(LoanApplication application, Long currentUserId) {
        log.info("创建贷款申请，customerName: {}, loanAmount: {}, currentUserId: {}",
                application.getCustomerName(), application.getLoanAmount(), currentUserId);

        // 1. 生成申请编号
        String applicationNo = generateApplicationNo();
        application.setApplicationNo(applicationNo);

        // 2. 检查客户是否存在，不存在则创建
        Customer existingCustomer = customerMapper.findByIdNo(application.getCustomerIdNo());
        if (existingCustomer == null) {
            Customer customer = new Customer();
            customer.setName(application.getCustomerName());
            customer.setIdNo(application.getCustomerIdNo());
            customer.setPhone(application.getCustomerPhone());
            customer.setCreatedAt(LocalDateTime.now());
            customer.setUpdatedAt(LocalDateTime.now());
            customerMapper.insert(customer);
            application.setCustomerId(customer.getId());
            log.info("新客户创建成功，id: {}", customer.getId());
        } else {
            application.setCustomerId(existingCustomer.getId());
            // 更新客户信息
            existingCustomer.setPhone(application.getCustomerPhone());
            existingCustomer.setUpdatedAt(LocalDateTime.now());
            customerMapper.update(existingCustomer);
            log.info("客户信息已更新，id: {}", existingCustomer.getId());
        }

        // 3. 设置默认状态
        application.setStatus(ApplicationStatus.DRAFT);
        application.setCurrentStage("DRAFT");
        application.setCreatedBy(currentUserId);
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        // 4. 插入申请
        loanApplicationMapper.insert(application);

        log.info("贷款申请创建成功，id: {}, applicationNo: {}", application.getId(), applicationNo);
        return application;
    }

    @Override
    @Transactional
    public LoanApplication update(LoanApplication application, Long currentUserId) {
        log.info("更新贷款申请，id: {}, currentUserId: {}", application.getId(), currentUserId);

        // 1. 查询原申请
        LoanApplication existing = loanApplicationMapper.findById(application.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.APPLICATION_NOT_FOUND);
        }

        // 2. 检查状态（只有草稿状态可以编辑）
        if (existing.getStatus() != ApplicationStatus.DRAFT) {
            throw new BusinessException(ResultCode.APPLICATION_STATUS_INVALID);
        }

        // 3. 检查权限（只有创建人可以编辑）
        if (!existing.getCreatedBy().equals(currentUserId)) {
            throw new BusinessException(ResultCode.APPLICATION_NO_PERMISSION);
        }

        // 4. 更新客户信息
        Customer existingCustomer = customerMapper.findByIdNo(application.getCustomerIdNo());
        if (existingCustomer == null) {
            Customer customer = new Customer();
            customer.setName(application.getCustomerName());
            customer.setIdNo(application.getCustomerIdNo());
            customer.setPhone(application.getCustomerPhone());
            customer.setCreatedAt(LocalDateTime.now());
            customer.setUpdatedAt(LocalDateTime.now());
            customerMapper.insert(customer);
            application.setCustomerId(customer.getId());
        } else {
            application.setCustomerId(existingCustomer.getId());
            existingCustomer.setPhone(application.getCustomerPhone());
            existingCustomer.setUpdatedAt(LocalDateTime.now());
            customerMapper.update(existingCustomer);
        }

        // 5. 更新申请
        application.setUpdatedAt(LocalDateTime.now());
        loanApplicationMapper.update(application);

        log.info("贷款申请更新成功，id: {}", application.getId());
        return application;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("删除贷款申请，id: {}", id);

        LoanApplication application = loanApplicationMapper.findById(id);
        if (application == null) {
            throw new BusinessException(ResultCode.APPLICATION_NOT_FOUND);
        }

        // 只有草稿状态可以删除
        if (application.getStatus() != ApplicationStatus.DRAFT) {
            throw new BusinessException(ResultCode.APPLICATION_STATUS_INVALID);
        }

        loanApplicationMapper.deleteById(id);

        log.info("贷款申请删除成功，id: {}", id);
    }

    @Override
    @Transactional
    public void submit(Long id, Long currentUserId) {
        log.info("提交贷款申请，id: {}, currentUserId: {}", id, currentUserId);

        LoanApplication application = loanApplicationMapper.findById(id);
        if (application == null) {
            throw new BusinessException(ResultCode.APPLICATION_NOT_FOUND);
        }

        // 只有草稿状态可以提交
        if (application.getStatus() != ApplicationStatus.DRAFT) {
            throw new BusinessException(ResultCode.APPLICATION_STATUS_INVALID);
        }

        // 检查权限
        if (!application.getCreatedBy().equals(currentUserId)) {
            throw new BusinessException(ResultCode.APPLICATION_NO_PERMISSION);
        }

        // 保存旧状态用于事件发布
        ApplicationStatus oldStatus = application.getStatus();

        // 根据金额判断审批流程
        if (application.getLoanAmount().compareTo(RISK_APPROVAL_THRESHOLD) >= 0) {
            // 金额 >= 10 万，需要风控审批
            application.setStatus(ApplicationStatus.INITIAL);
            application.setCurrentStage("INITIAL");
            log.info("申请提交成功，需要多级审批（金额 >= 10 万），id: {}", id);
        } else {
            // 金额 < 10 万，只需要初审和终审
            application.setStatus(ApplicationStatus.INITIAL);
            application.setCurrentStage("INITIAL");
            log.info("申请提交成功，需要初审和终审（金额 < 10 万），id: {}", id);
        }

        loanApplicationMapper.update(application);

        // 发布状态变更事件，触发自动提醒生成
        eventPublisher.publishEvent(new ApplicationStatusChangedEvent(
                this, application, oldStatus, application.getStatus(),
                application.getCurrentStage(), currentUserId));
    }

    @Override
    @Transactional
    public void approve(Long id, String action, String comment, Long currentUserId) {
        log.info("审批贷款申请，id: {}, action: {}, currentUserId: {}", id, action, currentUserId);

        LoanApplication application = loanApplicationMapper.findById(id);
        if (application == null) {
            throw new BusinessException(ResultCode.APPLICATION_NOT_FOUND);
        }

        ApplicationStatus oldStatus = application.getStatus();
        String currentStage = application.getCurrentStage();

        // 创建审批记录
        ApprovalRecord record = new ApprovalRecord();
        record.setApplicationId(id);
        record.setStage(currentStage);
        record.setApproverId(currentUserId);
        record.setAction(action);
        record.setComment(comment);
        record.setCreatedAt(LocalDateTime.now());
        approvalRecordMapper.insert(record);
        log.info("审批记录已创建，applicationId: {}, stage: {}", id, currentStage);

        if ("REJECT".equalsIgnoreCase(action)) {
            // 拒绝申请
            application.setStatus(ApplicationStatus.REJECTED);
            application.setCurrentStage("REJECTED");
            loanApplicationMapper.update(application);
            log.info("申请已拒绝，id: {}", id);

            // 发布事件
            eventPublisher.publishEvent(new ApplicationStatusChangedEvent(
                    this, application, oldStatus, ApplicationStatus.REJECTED,
                    "REJECTED", currentUserId));
            return;
        }

        // 审批通过，根据当前阶段流转状态
        if ("INITIAL".equals(currentStage)) {
            // 初审通过
            application.setStatus(ApplicationStatus.FINAL);
            application.setCurrentStage("FINAL");
            loanApplicationMapper.update(application);
            log.info("初审通过，进入终审，id: {}", id);

            // 发布事件
            eventPublisher.publishEvent(new ApplicationStatusChangedEvent(
                    this, application, oldStatus, ApplicationStatus.FINAL,
                    "FINAL", currentUserId));

        } else if ("FINAL".equals(currentStage)) {
            // 终审通过
            ApplicationStatus nextStatus;
            if (application.getLoanAmount().compareTo(RISK_APPROVAL_THRESHOLD) >= 0) {
                // 金额 >= 10 万，进入风控审批
                nextStatus = ApplicationStatus.RISK;
                application.setStatus(ApplicationStatus.RISK);
                application.setCurrentStage("RISK");
            } else {
                // 金额 < 10 万，审批通过
                nextStatus = ApplicationStatus.APPROVED;
                application.setStatus(ApplicationStatus.APPROVED);
                application.setCurrentStage("APPROVED");
            }
            loanApplicationMapper.update(application);
            log.info("终审通过，id: {}", id);

            // 发布事件
            eventPublisher.publishEvent(new ApplicationStatusChangedEvent(
                    this, application, oldStatus, nextStatus,
                    application.getCurrentStage(), currentUserId));

        } else if ("RISK".equals(currentStage)) {
            // 风控审批通过
            application.setStatus(ApplicationStatus.APPROVED);
            application.setCurrentStage("APPROVED");
            loanApplicationMapper.update(application);
            log.info("风控审批通过，申请已批准，id: {}", id);

            // 发布事件
            eventPublisher.publishEvent(new ApplicationStatusChangedEvent(
                    this, application, oldStatus, ApplicationStatus.APPROVED,
                    "APPROVED", currentUserId));
        }
    }

    @Override
    @Transactional
    public void assign(Long id, Long assigneeId, Long currentUserId) {
        log.info("分配贷款申请，id: {}, assigneeId: {}, currentUserId: {}", id, assigneeId, currentUserId);

        LoanApplication application = loanApplicationMapper.findById(id);
        if (application == null) {
            throw new BusinessException(ResultCode.APPLICATION_NOT_FOUND);
        }

        application.setAssignedTo(assigneeId);
        application.setUpdatedAt(LocalDateTime.now());
        loanApplicationMapper.update(application);

        // 生成分配提醒给新处理人
        CustomerReminder reminder = new CustomerReminder();
        reminder.setUserId(assigneeId);
        reminder.setCustomerId(application.getCustomerId());
        reminder.setReminderType(ReminderType.FOLLOWUP_REQUIRED);
        reminder.setTitle("新的贷款申请已分配给您");
        reminder.setContent(String.format("申请编号: %s, 客户: %s",
                application.getApplicationNo(), application.getCustomerName()));
        reminder.setPriority(2);
        reminder.setStatus("PENDING");
        reminder.setDueDate(LocalDateTime.now().plusDays(1));
        customerAssistantService.createReminder(reminder);

        log.info("申请分配成功，id: {}, assigneeId: {}", id, assigneeId);
    }

    /**
     * 生成申请编号
     * 格式：A + YYYYMMDD + 4 位序号
     */
    private String generateApplicationNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String prefix = "A" + dateStr;

        // 查询当天最后一个申请编号，生成下一个序号
        // 简单实现：使用时间戳后 4 位
        String seq = String.format("%04d", System.currentTimeMillis() % 10000);
        return prefix + seq;
    }
}
