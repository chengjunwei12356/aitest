package com.example.aitest.service.impl;

import com.example.aitest.dto.CustomerReminderDTO;
import com.example.aitest.dto.KnowledgeArticleDTO;
import com.example.aitest.dto.ReminderResolveRequest;
import com.example.aitest.entity.KnowledgeCategory;
import com.example.aitest.entity.ReminderType;
import com.example.aitest.mapper.CustomerAssistantMapper;
import com.example.aitest.service.CustomerAssistantService;
import com.example.aitest.util.CurrentUserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 客户经理助手服务实现类
 *
 * 数据权限控制：通过 CurrentUserUtils 获取当前登录用户 ID，
 * 过滤只返回该用户负责的客户提醒。
 */
@Service
public class CustomerAssistantServiceImpl implements CustomerAssistantService {

    @Autowired
    private CustomerAssistantMapper customerAssistantMapper;

    @Override
    public List<CustomerReminderDTO> getReminders(ReminderType type, String status) {
        String typeStr = type != null ? type.name() : null;
        Long currentUserId = CurrentUserUtils.getUserId();
        List<CustomerReminderDTO> reminders = customerAssistantMapper.getReminders(typeStr, status, currentUserId);
        populateReminderTypeDescription(reminders);
        return reminders;
    }

    @Override
    public List<CustomerReminderDTO> getCustomerReminders(Long customerId) {
        Long currentUserId = CurrentUserUtils.getUserId();
        List<CustomerReminderDTO> reminders = customerAssistantMapper.getCustomerReminders(customerId, currentUserId);
        populateReminderTypeDescription(reminders);
        return reminders;
    }

    @Override
    public void resolveReminder(ReminderResolveRequest request) {
        Long currentUserId = CurrentUserUtils.getUserId();
        customerAssistantMapper.resolveReminder(request.getReminderId(), request.getNote(), currentUserId);
    }

    @Override
    public List<KnowledgeArticleDTO> searchKnowledge(KnowledgeCategory category, String keyword) {
        String categoryStr = category != null ? category.name() : null;
        List<KnowledgeArticleDTO> articles = customerAssistantMapper.searchKnowledge(categoryStr, keyword);
        populateCategoryDescription(articles);
        return articles;
    }

    @Override
    public KnowledgeArticleDTO getKnowledgeArticle(Long id) {
        KnowledgeArticleDTO article = customerAssistantMapper.getKnowledgeArticle(id);
        if (article != null) {
            if (article.getCategory() != null) {
                article.setCategoryDescription(article.getCategory().getDescription());
            }
            // 增加浏览次数
            customerAssistantMapper.updateViewCount(id);
            // 重新查询以获取更新后的浏览次数
            article = customerAssistantMapper.getKnowledgeArticle(id);
        }
        return article;
    }

    /**
     * 为提醒列表填充提醒类型描述
     */
    private void populateReminderTypeDescription(List<CustomerReminderDTO> reminders) {
        if (reminders == null) return;
        for (CustomerReminderDTO reminder : reminders) {
            if (reminder.getReminderType() != null) {
                reminder.setReminderTypeDescription(reminder.getReminderType().getDescription());
            }
        }
    }

    /**
     * 为文章列表填充分类描述
     */
    private void populateCategoryDescription(List<KnowledgeArticleDTO> articles) {
        if (articles == null) return;
        for (KnowledgeArticleDTO article : articles) {
            if (article.getCategory() != null) {
                article.setCategoryDescription(article.getCategory().getDescription());
            }
        }
    }
}
