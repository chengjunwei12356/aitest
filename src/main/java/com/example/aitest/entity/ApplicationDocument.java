package com.example.aitest.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 申请材料实体
 */
@Data
public class ApplicationDocument {
    /**
     * 材料 ID
     */
    private Long id;

    /**
     * 申请 ID
     */
    private Long applicationId;

    /**
     * 材料类型
     */
    private String docType;

    /**
     * 文件名称
     */
    private String docName;

    /**
     * 存储路径
     */
    private String filePath;

    /**
     * 文件大小 (字节)
     */
    private Long fileSize;

    /**
     * 是否必传
     */
    private Boolean isRequired;

    /**
     * 上传人 ID
     */
    private Long uploadedBy;

    /**
     * 上传时间
     */
    private LocalDateTime uploadedAt;
}
