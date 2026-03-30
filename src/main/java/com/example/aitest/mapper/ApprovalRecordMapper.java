package com.example.aitest.mapper;

import com.example.aitest.entity.ApprovalRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 审批记录 Mapper 接口
 */
@Mapper
public interface ApprovalRecordMapper {

    /**
     * 根据 ID 查询记录
     */
    ApprovalRecord findById(@Param("id") Long id);

    /**
     * 查询申请的所有审批记录
     */
    List<ApprovalRecord> findByApplicationId(@Param("applicationId") Long applicationId);

    /**
     * 插入记录
     */
    int insert(ApprovalRecord record);
}
