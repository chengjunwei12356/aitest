package com.example.aitest.controller;

import com.example.aitest.common.Result;
import com.example.aitest.mapper.LoanApplicationMapper;
import com.example.aitest.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 工作台数据控制器
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final LoanApplicationMapper loanApplicationMapper;
    private final CustomerMapper customerMapper;

    /**
     * 获取待办任务统计
     */
    @GetMapping("/todo")
    public Result<Map<String, Object>> getTodoStats() {
        Map<String, Object> stats = new HashMap<>();

        // 从数据库查询真实统计数据
        int totalApplications = loanApplicationMapper.countAll();
        int draftCount = loanApplicationMapper.countByStatus("DRAFT");
        int pendingApproval = loanApplicationMapper.countByStatus("PENDING_APPROVAL");
        int approvedCount = loanApplicationMapper.countByStatus("APPROVED");
        int rejectedCount = loanApplicationMapper.countByStatus("REJECTED");
        int disbursedCount = loanApplicationMapper.countByStatus("DISBURSED");

        int totalCustomers = customerMapper.countAll();

        // 待办任务
        stats.put("application", pendingApproval);  // 待审批申请
        stats.put("approval", pendingApproval);      // 待审批
        stats.put("contract", approvedCount);        // 待签署合同（已批准）
        stats.put("loan", approvedCount);            // 待放款
        stats.put("repayment", disbursedCount);      // 待处理还款
        stats.put("warning", 0);                     // 预警（待实现）
        stats.put("postloan", disbursedCount);       // 贷后任务
        stats.put("other", draftCount);              // 草稿

        return Result.success(stats);
    }

    /**
     * 获取关键指标统计
     */
    @GetMapping("/metrics")
    public Result<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        int totalApplications = loanApplicationMapper.countAll();
        int totalCustomers = customerMapper.countAll();
        int pendingApproval = loanApplicationMapper.countByStatus("PENDING_APPROVAL");
        int approvedCount = loanApplicationMapper.countByStatus("APPROVED");

        // 计算审批通过率
        double approvalRate = 0.0;
        if (totalApplications > 0) {
            approvalRate = (double) approvedCount / totalApplications * 100;
        }

        metrics.put("totalApplications", totalApplications);
        metrics.put("totalCustomers", totalCustomers);
        metrics.put("pendingApproval", pendingApproval);
        metrics.put("approvedCount", approvedCount);
        metrics.put("approvalRate", String.format("%.1f%%", approvalRate));

        return Result.success(metrics);
    }

    /**
     * 获取贷款状态分布
     */
    @GetMapping("/status-distribution")
    public Result<Map<String, Integer>> getStatusDistribution() {
        Map<String, Integer> distribution = new HashMap<>();

        distribution.put("DRAFT", loanApplicationMapper.countByStatus("DRAFT"));
        distribution.put("PENDING_APPROVAL", loanApplicationMapper.countByStatus("PENDING_APPROVAL"));
        distribution.put("APPROVED", loanApplicationMapper.countByStatus("APPROVED"));
        distribution.put("REJECTED", loanApplicationMapper.countByStatus("REJECTED"));
        distribution.put("DISBURSED", loanApplicationMapper.countByStatus("DISBURSED"));

        return Result.success(distribution);
    }
}
