package com.example.aitest.service;

import com.example.aitest.dto.CustomerReminderDTO;
import com.example.aitest.dto.KnowledgeArticleDTO;
import com.example.aitest.dto.ReminderResolveRequest;
import com.example.aitest.entity.CustomerReminder;
import com.example.aitest.entity.KnowledgeArticle;
import com.example.aitest.entity.ReminderType;
import com.example.aitest.entity.KnowledgeCategory;

import java.util.List;
import java.util.Map;

/**
 * 客户助手服务接口
 */
public interface CustomerAssistantService {

    /**
     * 获取提醒列表
     */
    List<CustomerReminderDTO> getReminders(ReminderType type, String status);

    /**
     * 获取客户提醒列表
     */
    List<CustomerReminderDTO> getCustomerReminders(Long customerId);

    /**
     * 根据ID获取提醒
     */
    CustomerReminderDTO getReminderById(Long id);

    /**
     * 创建提醒
     */
    Long createReminder(CustomerReminder reminder);

    /**
     * 从 DTO 创建提醒（支持日期时间格式解析）
     */
    Long createReminderFromDTO(CustomerReminderDTO reminderDTO);

    /**
     * 更新提醒
     */
    boolean updateReminder(CustomerReminder reminder);

    /**
     * 删除提醒
     */
    boolean deleteReminder(Long id);

    /**
     * 获取提醒统计
     */
    Map<String, Integer> getReminderStats();

    /**
     * 解决提醒
     */
    boolean resolveReminder(ReminderResolveRequest request);

    /**
     * 搜索知识库文章
     */
    List<KnowledgeArticleDTO> searchKnowledge(KnowledgeCategory category, String keyword);

    /**
     * 获取知识库文章
     */
    KnowledgeArticleDTO getKnowledgeArticle(Long id);

    /**
     * 创建知识库文章
     */
    Long createKnowledgeArticle(KnowledgeArticle article);

    /**
     * 更新知识库文章
     */
    boolean updateKnowledgeArticle(KnowledgeArticle article);

    /**
     * 删除知识库文章
     */
    boolean deleteKnowledgeArticle(Long id);

    /**
     * 获取热门文章
     */
    List<KnowledgeArticleDTO> getHotArticles(Integer limit);
}