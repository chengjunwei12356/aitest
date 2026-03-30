package com.example.aitest.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 申请材料响应 VO
 */
@Data
@Builder
public class DocumentVO {
    private Long id;
    private String docType;
    private String docName;
    private String filePath;
    private Long fileSize;
    private Boolean isRequired;
    private LocalDateTime uploadedAt;
}
