package com.example.aitest.controller;

import com.example.aitest.common.Result;
import com.example.aitest.dto.ApplicationDTO;
import com.example.aitest.entity.LoanApplication;
import com.example.aitest.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 贷款申请管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * 获取申请列表（带筛选）
     */
    @GetMapping
    public Result<List<LoanApplication>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String guaranteeType,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LocalDateTime startDateTime = startDate != null ? LocalDateTime.parse(startDate) : null;
        LocalDateTime endDateTime = endDate != null ? LocalDateTime.parse(endDate) : null;

        List<LoanApplication> list = applicationService.findAll(
                status, guaranteeType, minAmount, maxAmount, keyword, startDateTime, endDateTime);
        return Result.success(list);
    }

    /**
     * 获取申请详情
     */
    @GetMapping("/{id}")
    public Result<LoanApplication> get(@PathVariable Long id) {
        LoanApplication application = applicationService.findById(id);
        return Result.success(application);
    }

    /**
     * 创建申请
     */
    @PostMapping
    public Result<LoanApplication> create(@Valid @RequestBody ApplicationDTO dto) {
        // 当前用户 ID，实际应从 session 获取
        Long currentUserId = 1L;

        LoanApplication application = new LoanApplication();
        application.setCustomerId(dto.getCustomerId());
        application.setCustomerName(dto.getCustomerName());
        application.setCustomerIdNo(dto.getCustomerIdNo());
        application.setCustomerPhone(dto.getCustomerPhone());
        application.setLoanAmount(dto.getLoanAmount());
        application.setLoanTerm(dto.getLoanTerm());
        application.setLoanPurpose(dto.getLoanPurpose());
        application.setGuaranteeType(dto.getGuaranteeType());

        LoanApplication result = applicationService.create(application, currentUserId);
        return Result.success(result);
    }

    /**
     * 更新申请
     */
    @PutMapping("/{id}")
    public Result<LoanApplication> update(@PathVariable Long id,
                                          @Valid @RequestBody ApplicationDTO dto) {
        Long currentUserId = 1L;

        LoanApplication application = applicationService.findById(id);
        application.setCustomerId(dto.getCustomerId());
        application.setCustomerName(dto.getCustomerName());
        application.setCustomerIdNo(dto.getCustomerIdNo());
        application.setCustomerPhone(dto.getCustomerPhone());
        application.setLoanAmount(dto.getLoanAmount());
        application.setLoanTerm(dto.getLoanTerm());
        application.setLoanPurpose(dto.getLoanPurpose());
        application.setGuaranteeType(dto.getGuaranteeType());

        LoanApplication result = applicationService.update(application, currentUserId);
        return Result.success(result);
    }

    /**
     * 删除申请
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        applicationService.delete(id);
        return Result.success();
    }

    /**
     * 提交申请
     */
    @PostMapping("/{id}/submit")
    public Result<Void> submit(@PathVariable Long id) {
        Long currentUserId = 1L;
        applicationService.submit(id, currentUserId);
        return Result.success();
    }

    /**
     * 审批申请
     */
    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id,
                                @RequestBody Map<String, String> body) {
        Long currentUserId = 1L;
        String action = body.get("action");
        String comment = body.getOrDefault("comment", "");

        applicationService.approve(id, action, comment, currentUserId);
        return Result.success();
    }

    /**
     * 分配申请
     */
    @PostMapping("/{id}/assign")
    public Result<Void> assign(@PathVariable Long id,
                               @RequestBody Map<String, Long> body) {
        Long currentUserId = 1L;
        Long assigneeId = body.get("assigneeId");

        applicationService.assign(id, assigneeId, currentUserId);
        return Result.success();
    }
}
