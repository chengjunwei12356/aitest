package com.example.aitest.service;

import com.example.aitest.entity.ApplicationDocument;
import java.util.List;

/**
 * 申请材料服务接口
 */
public interface DocumentService {

    /**
     * 根据 ID 查询材料
     * @param id 材料 ID
     * @return 材料信息
     */
    ApplicationDocument findById(Long id);

    /**
     * 查询申请的所有材料
     * @param applicationId 申请 ID
     * @return 材料列表
     */
    List<ApplicationDocument> findByApplicationId(Long applicationId);

    /**
     * 上传材料
     * @param document 材料信息
     * @return 上传后的材料
     */
    ApplicationDocument upload(ApplicationDocument document);

    /**
     * 删除材料
     * @param id 材料 ID
     */
    void delete(Long id);

    /**
     * 根据申请 ID 删除所有材料
     * @param applicationId 申请 ID
     */
    void deleteByApplicationId(Long applicationId);
}
