package com.example.aitest.service;

import com.example.aitest.entity.ApprovalRecord;
import java.util.List;

/**
 * 审批记录服务接口
 */
public interface ApprovalService {

    /**
     * 根据 ID 查询审批记录
     * @param id 记录 ID
     * @return 审批记录
     */
    ApprovalRecord findById(Long id);

    /**
     * 查询申请的所有审批记录
     * @param applicationId 申请 ID
     * @return 审批记录列表
     */
    List<ApprovalRecord> findByApplicationId(Long applicationId);

    /**
     * 创建审批记录
     * @param record 审批记录
     * @return 创建后的记录
     */
    ApprovalRecord create(ApprovalRecord record);
}
