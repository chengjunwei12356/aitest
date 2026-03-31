package com.example.aitest.controller;

import com.example.aitest.common.Result;
import com.example.aitest.entity.ApprovalRecord;
import com.example.aitest.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 审批记录管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    /**
     * 获取审批记录列表
     */
    @GetMapping("/{applicationId}/records")
    public Result<List<ApprovalRecord>> list(@PathVariable Long applicationId) {
        List<ApprovalRecord> list = approvalService.findByApplicationId(applicationId);
        return Result.success(list);
    }

    /**
     * 获取审批记录详情
     */
    @GetMapping("/{applicationId}/records/{id}")
    public Result<ApprovalRecord> get(@PathVariable Long applicationId,
                                      @PathVariable Long id) {
        ApprovalRecord record = approvalService.findById(id);
        return Result.success(record);
    }

    /**
     * 创建审批记录（通过审批接口调用）
     */
    @PostMapping("/{applicationId}/records")
    public Result<ApprovalRecord> create(@PathVariable Long applicationId,
                                         @RequestBody Map<String, Object> body) {
        ApprovalRecord record = new ApprovalRecord();
        record.setApplicationId(applicationId);
        record.setStage((String) body.get("stage"));
        record.setApproverId(1L); // 当前用户 ID，实际应从 session 获取
        record.setAction((String) body.get("action"));
        record.setComment((String) body.get("comment"));

        ApprovalRecord saved = approvalService.create(record);
        return Result.success(saved);
    }
}
