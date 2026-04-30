package com.example.aitest.dto;

import com.example.aitest.entity.KnowledgeCategory;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 知识库文章 DTO
 */
@Data
public class KnowledgeArticleDTO {
    /**
     * 文章 ID
     */
    private Long id;

    /**
     * 分类
     */
    private KnowledgeCategory category;

    /**
     * 分类描述
     */
    private String categoryDescription;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签（逗号分隔）
     */
    private String tags;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 是否激活
     */
    private Boolean isActive;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}