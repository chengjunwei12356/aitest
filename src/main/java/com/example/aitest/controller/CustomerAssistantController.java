package com.example.aitest.controller;

import com.example.aitest.common.Result;
import com.example.aitest.dto.CustomerReminderDTO;
import com.example.aitest.dto.KnowledgeArticleDTO;
import com.example.aitest.dto.ReminderResolveRequest;
import com.example.aitest.entity.KnowledgeCategory;
import com.example.aitest.entity.ReminderType;
import com.example.aitest.service.CustomerAssistantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 获取指定客户的提醒列表
     * GET /api/customer-assistant/reminders/{customerId}
     */
    @GetMapping("/reminders/{customerId}")
    public Result<List<CustomerReminderDTO>> getCustomerReminders(
            @PathVariable Long customerId) {
        List<CustomerReminderDTO> reminders = customerAssistantService.getCustomerReminders(customerId);
        return Result.success(reminders);
    }

    /**
     * 解决提醒
     * POST /api/customer-assistant/reminders/resolve
     */
    @PostMapping("/reminders/resolve")
    public Result<Void> resolveReminder(@Valid @RequestBody ReminderResolveRequest request) {
        customerAssistantService.resolveReminder(request);
        return Result.success(null);
    }

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
}
