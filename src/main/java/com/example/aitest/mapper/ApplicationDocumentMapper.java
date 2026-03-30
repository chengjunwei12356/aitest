package com.example.aitest.mapper;

import com.example.aitest.entity.ApplicationDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 申请材料 Mapper 接口
 */
@Mapper
public interface ApplicationDocumentMapper {

    /**
     * 根据 ID 查询材料
     */
    ApplicationDocument findById(@Param("id") Long id);

    /**
     * 查询申请的所有材料
     */
    List<ApplicationDocument> findByApplicationId(@Param("applicationId") Long applicationId);

    /**
     * 插入材料
     */
    int insert(ApplicationDocument document);

    /**
     * 删除材料
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据申请 ID 删除所有材料
     */
    int deleteByApplicationId(@Param("applicationId") Long applicationId);
}
