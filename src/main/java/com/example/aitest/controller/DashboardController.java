package com.example.aitest.controller;

import com.example.aitest.common.Result;
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
public class DashboardController {

    /**
     * 获取待办任务统计
     */
    @GetMapping("/todo")
    public Result<Map<String, Object>> getTodoStats() {
        Map<String, Object> stats = new HashMap<>();
        // 静态模拟数据
        stats.put("application", 12);  // 待审批申请
        stats.put("approval", 5);      // 待审批
        stats.put("contract", 3);      // 待签署合同
        stats.put("loan", 8);          // 待放款
        stats.put("repayment", 20);    // 待处理还款
        stats.put("warning", 3);       // 预警
        stats.put("postloan", 7);      // 贷后任务
        stats.put("other", 15);        // 其他
        return Result.success(stats);
    }
}
