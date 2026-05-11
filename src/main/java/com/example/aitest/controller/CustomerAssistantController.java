package com.example.aitest.controller;

import com.example.aitest.common.Result;
import com.example.aitest.dto.CustomerReminderDTO;
import com.example.aitest.dto.KnowledgeArticleDTO;
import com.example.aitest.dto.ReminderResolveRequest;
import com.example.aitest.entity.CustomerReminder;
import com.example.aitest.entity.KnowledgeArticle;
import com.example.aitest.entity.KnowledgeCategory;
import com.example.aitest.entity.ReminderType;
import com.example.aitest.service.CustomerAssistantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 客户经理助手 Controller
 * 提供提醒管理和知识库查询 REST API
 *
 * 数据权限：通过 X-User-Id 请求头传递用户 ID，
 * 拦截器会将其存入 CurrentUserUtils，Service 层据此过滤数据。
 * 未提供用户 ID 时返回所有数据（向后兼容）。
 */
@RestController
@RequestMapping("/api/customer-assistant")
public class CustomerAssistantController {

    @Autowired
    private CustomerAssistantService customerAssistantService;

    // ==================== 提醒管理 API ====================

    /**
     * 获取提醒列表
     * GET /api/customer-assistant/reminders?type=APPLICATION_EXPIRE&status=PENDING
     */
    @GetMapping("/reminders")
    public Result<List<CustomerReminderDTO>> getReminders(
            @RequestParam(required = false) ReminderType type,
            @RequestParam(required = false) String status) {
        List<CustomerReminderDTO> reminders = customerAssistantService.getReminders(type, status);
        return Result.success(reminders);
    }

    /**
     * 获取提醒统计
     * GET /api/customer-assistant/reminders/stats
     */
    @GetMapping("/reminders/stats")
    public Result<Map<String, Integer>> getReminderStats() {
        Map<String, Integer> stats = customerAssistantService.getReminderStats();
        return Result.success(stats);
    }

    /**
     * 获取提醒详情
     * GET /api/customer-assistant/reminders/{id}
     */
    @GetMapping("/reminders/{id}")
    public Result<CustomerReminderDTO> getReminderById(@PathVariable Long id) {
        CustomerReminderDTO reminder = customerAssistantService.getReminderById(id);
        if (reminder == null) {
            return Result.error("提醒不存在");
        }
        return Result.success(reminder);
    }

    /**
     * 获取指定客户的提醒列表
     * GET /api/customer-assistant/reminders/customer/{customerId}
     */
    @GetMapping("/reminders/customer/{customerId}")
    public Result<List<CustomerReminderDTO>> getCustomerReminders(
            @PathVariable Long customerId) {
        List<CustomerReminderDTO> reminders = customerAssistantService.getCustomerReminders(customerId);
        return Result.success(reminders);
    }

    /**
     * 创建提醒
     * POST /api/customer-assistant/reminders
     */
    @PostMapping("/reminders")
    public Result<Long> createReminder(@Valid @RequestBody CustomerReminderDTO reminderDTO) {
        Long id = customerAssistantService.createReminderFromDTO(reminderDTO);
        return Result.success(id);
    }

    /**
     * 更新提醒
     * PUT /api/customer-assistant/reminders/{id}
     */
    @PutMapping("/reminders/{id}")
    public Result<Boolean> updateReminder(@PathVariable Long id, @Valid @RequestBody CustomerReminder reminder) {
        reminder.setId(id);
        boolean success = customerAssistantService.updateReminder(reminder);
        if (!success) {
            return Result.error("更新失败");
        }
        return Result.success(true);
    }

    /**
     * 删除提醒
     * DELETE /api/customer-assistant/reminders/{id}
     */
    @DeleteMapping("/reminders/{id}")
    public Result<Boolean> deleteReminder(@PathVariable Long id) {
        boolean success = customerAssistantService.deleteReminder(id);
        if (!success) {
            return Result.error("删除失败或无权限");
        }
        return Result.success(true);
    }

    /**
     * 解决提醒
     * POST /api/customer-assistant/reminders/resolve
     */
    @PostMapping("/reminders/resolve")
    public Result<Boolean> resolveReminder(@Valid @RequestBody ReminderResolveRequest request) {
        boolean success = customerAssistantService.resolveReminder(request);
        return Result.success(success);
    }

    // ==================== 知识库管理 API ====================

    /**
     * 搜索知识库文章
     * GET /api/customer-assistant/knowledge?category=FAQ&keyword=贷款
     */
    @GetMapping("/knowledge")
    public Result<List<KnowledgeArticleDTO>> searchKnowledge(
            @RequestParam(required = false) KnowledgeCategory category,
            @RequestParam(required = false) String keyword) {
        List<KnowledgeArticleDTO> articles = customerAssistantService.searchKnowledge(category, keyword);
        return Result.success(articles);
    }

    /**
     * 获取热门文章
     * GET /api/customer-assistant/knowledge/hot?limit=10
     */
    @GetMapping("/knowledge/hot")
    public Result<List<KnowledgeArticleDTO>> getHotArticles(
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<KnowledgeArticleDTO> articles = customerAssistantService.getHotArticles(limit);
        return Result.success(articles);
    }

    /**
     * 获取知识库文章详情
     * GET /api/customer-assistant/knowledge/{id}
     */
    @GetMapping("/knowledge/{id}")
    public Result<KnowledgeArticleDTO> getKnowledgeArticle(@PathVariable Long id) {
        KnowledgeArticleDTO article = customerAssistantService.getKnowledgeArticle(id);
        if (article == null) {
            return Result.error("文章不存在或已下架");
        }
        return Result.success(article);
    }

    /**
     * 创建知识库文章
     * POST /api/customer-assistant/knowledge
     */
    @PostMapping("/knowledge")
    public Result<Long> createKnowledgeArticle(@Valid @RequestBody KnowledgeArticle article) {
        Long id = customerAssistantService.createKnowledgeArticle(article);
        return Result.success(id);
    }

    /**
     * 更新知识库文章
     * PUT /api/customer-assistant/knowledge/{id}
     */
    @PutMapping("/knowledge/{id}")
    public Result<Boolean> updateKnowledgeArticle(@PathVariable Long id, @Valid @RequestBody KnowledgeArticle article) {
        article.setId(id);
        boolean success = customerAssistantService.updateKnowledgeArticle(article);
        if (!success) {
            return Result.error("更新失败");
        }
        return Result.success(true);
    }

    /**
     * 删除知识库文章
     * DELETE /api/customer-assistant/knowledge/{id}
     */
    @DeleteMapping("/knowledge/{id}")
    public Result<Boolean> deleteKnowledgeArticle(@PathVariable Long id) {
        boolean success = customerAssistantService.deleteKnowledgeArticle(id);
        if (!success) {
            return Result.error("删除失败");
        }
        return Result.success(true);
    }
}
