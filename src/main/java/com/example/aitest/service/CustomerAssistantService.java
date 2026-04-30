package com.example.aitest.service;

import com.example.aitest.dto.CustomerReminderDTO;
import com.example.aitest.dto.KnowledgeArticleDTO;
import com.example.aitest.dto.ReminderResolveRequest;
import com.example.aitest.entity.ReminderType;
import com.example.aitest.entity.KnowledgeCategory;

import java.util.List;

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
     * 解决提醒
     */
    void resolveReminder(ReminderResolveRequest request);

    /**
     * 搜索知识库文章
     */
    List<KnowledgeArticleDTO> searchKnowledge(KnowledgeCategory category, String keyword);

    /**
     * 获取知识库文章
     */
    KnowledgeArticleDTO getKnowledgeArticle(Long id);
}