package com.example.aitest.mapper;

import com.example.aitest.dto.CustomerReminderDTO;
import com.example.aitest.dto.KnowledgeArticleDTO;
import com.example.aitest.entity.CustomerReminder;
import com.example.aitest.entity.KnowledgeArticle;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 客户助手 Mapper 接口
 */
public interface CustomerAssistantMapper {

    /**
     * 获取提醒列表
     * @param userId 当前用户 ID（用于数据权限过滤，可为 null）
     */
    List<CustomerReminderDTO> getReminders(
            @Param("type") String type,
            @Param("status") String status,
            @Param("userId") Long userId);

    /**
     * 获取客户提醒列表
     * @param userId 当前用户 ID（用于数据权限过滤，可为 null）
     */
    List<CustomerReminderDTO> getCustomerReminders(
            @Param("customerId") Long customerId,
            @Param("userId") Long userId);

    /**
     * 根据ID获取提醒
     */
    CustomerReminderDTO getReminderById(@Param("id") Long id);

    /**
     * 创建提醒
     */
    int createReminder(CustomerReminder reminder);

    /**
     * 更新提醒
     */
    int updateReminder(CustomerReminder reminder);

    /**
     * 删除提醒
     */
    int deleteReminder(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 获取提醒统计
     */
    Map<String, Integer> getReminderStats(@Param("userId") Long userId);

    /**
     * 搜索知识库文章
     */
    List<KnowledgeArticleDTO> searchKnowledge(
            @Param("category") String category,
            @Param("keyword") String keyword);

    /**
     * 获取知识库文章
     */
    KnowledgeArticleDTO getKnowledgeArticle(@Param("id") Long id);

    /**
     * 创建知识库文章
     */
    int createKnowledgeArticle(KnowledgeArticle article);

    /**
     * 更新知识库文章
     */
    int updateKnowledgeArticle(KnowledgeArticle article);

    /**
     * 删除知识库文章
     */
    int deleteKnowledgeArticle(@Param("id") Long id);

    /**
     * 获取热门文章
     */
    List<KnowledgeArticleDTO> getHotArticles(@Param("limit") Integer limit);

    /**
     * 标记提醒为已解决
     * @param userId 当前用户 ID（用于权限检查，可为 null）
     */
    int resolveReminder(
            @Param("reminderId") Long reminderId,
            @Param("note") String note,
            @Param("userId") Long userId);

    /**
     * 增加文章浏览次数
     */
    void updateViewCount(@Param("id") Long id);
}