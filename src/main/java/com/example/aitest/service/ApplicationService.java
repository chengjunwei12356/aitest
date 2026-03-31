package com.example.aitest.service;

import com.example.aitest.entity.ApplicationStatus;
import com.example.aitest.entity.GuaranteeType;
import com.example.aitest.entity.LoanApplication;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 贷款申请服务接口
 */
public interface ApplicationService {

    /**
     * 根据 ID 查询申请
     * @param id 申请 ID
     * @return 申请信息
     */
    LoanApplication findById(Long id);

    /**
     * 根据申请编号查询申请
     * @param applicationNo 申请编号
     * @return 申请信息
     */
    LoanApplication findByApplicationNo(String applicationNo);

    /**
     * 查询申请列表（带筛选）
     * @param status 状态
     * @param guaranteeType 担保方式
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @param keyword 关键词
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 申请列表
     */
    List<LoanApplication> findAll(String status, String guaranteeType,
                                   BigDecimal minAmount, BigDecimal maxAmount,
                                   String keyword, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 创建贷款申请
     * @param application 申请信息
     * @param currentUserId 当前用户 ID
     * @return 创建后的申请
     */
    LoanApplication create(LoanApplication application, Long currentUserId);

    /**
     * 更新贷款申请
     * @param application 申请信息
     * @param currentUserId 当前用户 ID
     * @return 更新后的申请
     */
    LoanApplication update(LoanApplication application, Long currentUserId);

    /**
     * 删除贷款申请
     * @param id 申请 ID
     */
    void delete(Long id);

    /**
     * 提交申请（从草稿到待初审）
     * @param id 申请 ID
     * @param currentUserId 当前用户 ID
     */
    void submit(Long id, Long currentUserId);

    /**
     * 审批申请
     * @param id 申请 ID
     * @param action 操作（APPROVE/REJECT）
     * @param comment 审批意见
     * @param currentUserId 当前用户 ID
     */
    void approve(Long id, String action, String comment, Long currentUserId);

    /**
     * 分配申请给处理人
     * @param id 申请 ID
     * @param assigneeId 处理人 ID
     * @param currentUserId 当前用户 ID
     */
    void assign(Long id, Long assigneeId, Long currentUserId);
}
