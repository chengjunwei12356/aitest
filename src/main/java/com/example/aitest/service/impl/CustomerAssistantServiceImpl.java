package com.example.aitest.service.impl;

import com.example.aitest.config.BusinessException;
import com.example.aitest.dto.CustomerReminderDTO;
import com.example.aitest.dto.KnowledgeArticleDTO;
import com.example.aitest.dto.ReminderResolveRequest;
import com.example.aitest.entity.CustomerReminder;
import com.example.aitest.entity.KnowledgeArticle;
import com.example.aitest.entity.KnowledgeCategory;
import com.example.aitest.entity.ReminderType;
import com.example.aitest.event.ReminderCreatedEvent;
import com.example.aitest.mapper.CustomerAssistantMapper;
import com.example.aitest.service.CustomerAssistantService;
import com.example.aitest.util.CurrentUserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 客户经理助手服务实现类
 *
 * 数据权限控制：通过 CurrentUserUtils 获取当前登录用户 ID，
 * 过滤只返回该用户负责的客户提醒。
 */
@Slf4j
@Service
public class CustomerAssistantServiceImpl implements CustomerAssistantService {

    @Autowired
    private CustomerAssistantMapper customerAssistantMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

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
    public CustomerReminderDTO getReminderById(Long id) {
        CustomerReminderDTO reminder = customerAssistantMapper.getReminderById(id);
        if (reminder != null && reminder.getReminderType() != null) {
            reminder.setReminderTypeDescription(reminder.getReminderType().getDescription());
        }
        return reminder;
    }

    @Override
    public Long createReminder(CustomerReminder reminder) {
        Long currentUserId = CurrentUserUtils.getUserId();
        if (currentUserId != null) {
            reminder.setUserId(currentUserId);
        }
        if (reminder.getStatus() == null) {
            reminder.setStatus("PENDING");
        }
        customerAssistantMapper.createReminder(reminder);
        
        // 发布提醒创建事件，触发站内消息推送
        log.info("发布提醒创建事件，reminderId: {}", reminder.getId());
        eventPublisher.publishEvent(new ReminderCreatedEvent(this, reminder));
        
        return reminder.getId();
    }

    @Override
    public Long createReminderFromDTO(CustomerReminderDTO reminderDTO) {
        CustomerReminder reminder = new CustomerReminder();
        BeanUtils.copyProperties(reminderDTO, reminder, "reminderType", "id");
        if (reminderDTO.getReminderType() != null) {
            reminder.setReminderType(reminderDTO.getReminderType());
        }
        return createReminder(reminder);
    }

    @Override
    public boolean updateReminder(CustomerReminder reminder) {
        int rows = customerAssistantMapper.updateReminder(reminder);
        return rows > 0;
    }

    @Override
    public boolean deleteReminder(Long id) {
        Long currentUserId = CurrentUserUtils.getUserId();
        int rows = customerAssistantMapper.deleteReminder(id, currentUserId);
        return rows > 0;
    }

    @Override
    public Map<String, Integer> getReminderStats() {
        Long currentUserId = CurrentUserUtils.getUserId();
        return customerAssistantMapper.getReminderStats(currentUserId);
    }

    @Override
    public boolean resolveReminder(ReminderResolveRequest request) {
        Long currentUserId = CurrentUserUtils.getUserId();
        int rows = customerAssistantMapper.resolveReminder(request.getReminderId(), request.getNote(), currentUserId);
        if (rows == 0) {
            throw new BusinessException("提醒不存在或无权限操作");
        }
        return true;
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
            customerAssistantMapper.updateViewCount(id);
            article = customerAssistantMapper.getKnowledgeArticle(id);
        }
        return article;
    }

    @Override
    public Long createKnowledgeArticle(KnowledgeArticle article) {
        if (article.getIsActive() == null) {
            article.setIsActive(true);
        }
        customerAssistantMapper.createKnowledgeArticle(article);
        return article.getId();
    }

    @Override
    public boolean updateKnowledgeArticle(KnowledgeArticle article) {
        int rows = customerAssistantMapper.updateKnowledgeArticle(article);
        return rows > 0;
    }

    @Override
    public boolean deleteKnowledgeArticle(Long id) {
        int rows = customerAssistantMapper.deleteKnowledgeArticle(id);
        return rows > 0;
    }

    @Override
    public List<KnowledgeArticleDTO> getHotArticles(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        List<KnowledgeArticleDTO> articles = customerAssistantMapper.getHotArticles(limit);
        populateCategoryDescription(articles);
        return articles;
    }

    private void populateReminderTypeDescription(List<CustomerReminderDTO> reminders) {
        if (reminders == null) return;
        for (CustomerReminderDTO reminder : reminders) {
            if (reminder.getReminderType() != null) {
                reminder.setReminderTypeDescription(reminder.getReminderType().getDescription());
            }
        }
    }

    private void populateCategoryDescription(List<KnowledgeArticleDTO> articles) {
        if (articles == null) return;
        for (KnowledgeArticleDTO article : articles) {
            if (article.getCategory() != null) {
                article.setCategoryDescription(article.getCategory().getDescription());
            }
        }
    }
}
