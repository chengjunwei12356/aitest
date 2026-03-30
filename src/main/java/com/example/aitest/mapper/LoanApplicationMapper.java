package com.example.aitest.mapper;

import com.example.aitest.entity.LoanApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 贷款申请 Mapper 接口
 */
@Mapper
public interface LoanApplicationMapper {

    /**
     * 根据 ID 查询申请
     */
    LoanApplication findById(@Param("id") Long id);

    /**
     * 根据申请编号查询申请
     */
    LoanApplication findByApplicationNo(@Param("applicationNo") String applicationNo);

    /**
     * 查询申请列表（带筛选）
     */
    List<LoanApplication> findAll(@Param("status") String status,
                                   @Param("guaranteeType") String guaranteeType,
                                   @Param("minAmount") String minAmount,
                                   @Param("maxAmount") String maxAmount,
                                   @Param("keyword") String keyword,
                                   @Param("startDate") String startDate,
                                   @Param("endDate") String endDate);

    /**
     * 插入申请
     */
    int insert(LoanApplication application);

    /**
     * 更新申请
     */
    int update(LoanApplication application);

    /**
     * 删除申请
     */
    int deleteById(@Param("id") Long id);

    /**
     * 更新状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 更新当前阶段
     */
    int updateCurrentStage(@Param("id") Long id, @Param("stage") String stage);
}
