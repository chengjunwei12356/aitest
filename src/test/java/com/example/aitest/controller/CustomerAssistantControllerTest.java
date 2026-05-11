package com.example.aitest.controller;

import com.example.aitest.config.GlobalExceptionHandler;
import com.example.aitest.dto.CustomerReminderDTO;
import com.example.aitest.dto.KnowledgeArticleDTO;
import com.example.aitest.dto.ReminderResolveRequest;
import com.example.aitest.entity.CustomerReminder;
import com.example.aitest.entity.KnowledgeArticle;
import com.example.aitest.entity.KnowledgeCategory;
import com.example.aitest.entity.ReminderType;
import com.example.aitest.service.CustomerAssistantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 客户经理助手控制器测试
 * 测试创建提醒、查询列表、筛选、标记解决、删除、知识库搜索等功能
 */
class CustomerAssistantControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomerAssistantService customerAssistantService;

    @InjectMocks
    private CustomerAssistantController customerAssistantController;

    private ObjectMapper objectMapper;
    private CustomerReminderDTO testReminder;
    private KnowledgeArticleDTO testArticle;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(customerAssistantController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        testReminder = new CustomerReminderDTO();
        testReminder.setId(1L);
        testReminder.setCustomerId(1L);
        testReminder.setCustomerName("张三");
        testReminder.setCustomerPhone("13800138001");
        testReminder.setReminderType(ReminderType.APPLICATION_EXPIRE);
        testReminder.setTitle("申请即将过期提醒");
        testReminder.setContent("客户贷款申请即将过期，请及时处理");
        testReminder.setPriority(2);
        testReminder.setStatus("PENDING");
        testReminder.setDueDate(LocalDateTime.now().plusDays(7));

        testArticle = new KnowledgeArticleDTO();
        testArticle.setId(1L);
        testArticle.setCategory(KnowledgeCategory.FAQ);
        testArticle.setTitle("常见问题解答");
        testArticle.setContent("这里是常见问题内容");
        testArticle.setViewCount(10);
    }

    // ==================== 创建提醒测试 ====================

    @Test
    @DisplayName("测试创建提醒功能 - 成功（验证日期时间格式解析）")
    void testCreateReminder_Success() throws Exception {
        // 使用 Map 模拟前端发送的 JSON 数据，包含 "yyyy-MM-dd HH:mm:ss" 格式的日期
        Map<String, Object> reminderData = new HashMap<>();
        reminderData.put("customerId", 1L);
        reminderData.put("reminderType", "APPLICATION_EXPIRE");
        reminderData.put("title", "测试提醒-日期格式验证");
        reminderData.put("content", "验证日期时间格式解析");
        reminderData.put("priority", 2);
        reminderData.put("dueDate", "2026-05-12 15:30:00");

        when(customerAssistantService.createReminderFromDTO(any(CustomerReminderDTO.class))).thenReturn(1L);

        mockMvc.perform(post("/api/customer-assistant/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reminderData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    @DisplayName("测试创建提醒功能 - 高优先级")
    void testCreateReminder_HighPriority() throws Exception {
        Map<String, Object> reminderData = new HashMap<>();
        reminderData.put("customerId", 2L);
        reminderData.put("reminderType", "MATERIAL_EXPIRE");
        reminderData.put("title", "材料过期提醒");
        reminderData.put("content", "客户材料即将过期");
        reminderData.put("priority", 3);
        reminderData.put("dueDate", "2026-05-13 10:00:00");

        when(customerAssistantService.createReminderFromDTO(any(CustomerReminderDTO.class))).thenReturn(2L);

        mockMvc.perform(post("/api/customer-assistant/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reminderData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(2));
    }

    // ==================== 查询提醒列表测试 ====================

    @Test
    @DisplayName("测试查询提醒列表 - 无过滤条件")
    void testGetReminders_NoFilter() throws Exception {
        List<CustomerReminderDTO> reminders = Arrays.asList(testReminder);
        when(customerAssistantService.getReminders(any(), anyString())).thenReturn(reminders);

        mockMvc.perform(get("/api/customer-assistant/reminders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].title").value("申请即将过期提醒"));
    }

    @Test
    @DisplayName("测试查询提醒列表 - 空列表")
    void testGetReminders_EmptyList() throws Exception {
        when(customerAssistantService.getReminders(any(), anyString())).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/customer-assistant/reminders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // ==================== 筛选提醒功能测试 ====================

    @Test
    @DisplayName("测试筛选提醒功能 - 按类型过滤")
    void testFilterReminders_ByType() throws Exception {
        when(customerAssistantService.getReminders(any(ReminderType.class), anyString()))
                .thenReturn(Arrays.asList(testReminder));

        mockMvc.perform(get("/api/customer-assistant/reminders")
                        .param("type", "APPLICATION_EXPIRE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @DisplayName("测试筛选提醒功能 - 按状态过滤")
    void testFilterReminders_ByStatus() throws Exception {
        when(customerAssistantService.getReminders(any(), eq("PENDING")))
                .thenReturn(Arrays.asList(testReminder));

        mockMvc.perform(get("/api/customer-assistant/reminders")
                        .param("status", "PENDING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("测试筛选提醒功能 - 按类型和状态组合过滤")
    void testFilterReminders_ByTypeAndStatus() throws Exception {
        when(customerAssistantService.getReminders(any(ReminderType.class), eq("PENDING")))
                .thenReturn(Arrays.asList(testReminder));

        mockMvc.perform(get("/api/customer-assistant/reminders")
                        .param("type", "APPLICATION_EXPIRE")
                        .param("status", "PENDING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("测试筛选提醒功能 - 无匹配结果")
    void testFilterReminders_NoMatch() throws Exception {
        when(customerAssistantService.getReminders(any(), anyString())).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/customer-assistant/reminders")
                        .param("type", "LOAN_RENEW")
                        .param("status", "RESOLVED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // ==================== 标记提醒为已解决测试 ====================

    @Test
    @DisplayName("测试标记提醒为已解决 - 成功")
    void testResolveReminder_Success() throws Exception {
        ReminderResolveRequest request = new ReminderResolveRequest();
        request.setReminderId(1L);
        request.setNote("已处理完成");

        when(customerAssistantService.resolveReminder(any(ReminderResolveRequest.class))).thenReturn(true);

        mockMvc.perform(post("/api/customer-assistant/reminders/resolve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("测试标记提醒为已解决 - 带备注")
    void testResolveReminder_WithNote() throws Exception {
        ReminderResolveRequest request = new ReminderResolveRequest();
        request.setReminderId(1L);
        request.setNote("客户已补交材料，问题解决");

        when(customerAssistantService.resolveReminder(any(ReminderResolveRequest.class))).thenReturn(true);

        mockMvc.perform(post("/api/customer-assistant/reminders/resolve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    // ==================== 删除提醒功能测试 ====================

    @Test
    @DisplayName("测试删除提醒功能 - 成功")
    void testDeleteReminder_Success() throws Exception {
        when(customerAssistantService.deleteReminder(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/customer-assistant/reminders/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("测试删除提醒功能 - 不存在或无权限")
    void testDeleteReminder_Fail() throws Exception {
        when(customerAssistantService.deleteReminder(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/customer-assistant/reminders/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    // ==================== 知识库搜索功能测试 ====================

    @Test
    @DisplayName("测试知识库搜索功能 - 按关键词搜索")
    void testSearchKnowledge_ByKeyword() throws Exception {
        List<KnowledgeArticleDTO> articles = Arrays.asList(testArticle);
        when(customerAssistantService.searchKnowledge(any(), anyString())).thenReturn(articles);

        mockMvc.perform(get("/api/customer-assistant/knowledge")
                        .param("keyword", "贷款")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].title").value("常见问题解答"));
    }

    @Test
    @DisplayName("测试知识库搜索功能 - 按分类过滤")
    void testSearchKnowledge_ByCategory() throws Exception {
        when(customerAssistantService.searchKnowledge(any(KnowledgeCategory.class), anyString()))
                .thenReturn(Arrays.asList(testArticle));

        mockMvc.perform(get("/api/customer-assistant/knowledge")
                        .param("category", "FAQ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("测试知识库搜索功能 - 组合搜索")
    void testSearchKnowledge_Combined() throws Exception {
        when(customerAssistantService.searchKnowledge(any(KnowledgeCategory.class), eq("贷款")))
                .thenReturn(Arrays.asList(testArticle));

        mockMvc.perform(get("/api/customer-assistant/knowledge")
                        .param("category", "FAQ")
                        .param("keyword", "贷款")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("测试知识库搜索功能 - 无匹配结果")
    void testSearchKnowledge_NoMatch() throws Exception {
        when(customerAssistantService.searchKnowledge(any(), anyString())).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/customer-assistant/knowledge")
                        .param("keyword", "不存在的关键词")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("测试获取热门知识库文章")
    void testGetHotArticles() throws Exception {
        when(customerAssistantService.getHotArticles(5)).thenReturn(Arrays.asList(testArticle));

        mockMvc.perform(get("/api/customer-assistant/knowledge/hot")
                        .param("limit", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].viewCount").value(10));
    }

    // ==================== 站内消息功能测试 ====================

    @Test
    @DisplayName("测试站内消息功能 - 获取未读消息数")
    void testGetUnreadCount() throws Exception {
        mockMvc.perform(get("/api/notifications/unread-count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("测试站内消息功能 - 获取消息列表")
    void testGetNotifications() throws Exception {
        mockMvc.perform(get("/api/notifications")
                        .param("isRead", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("测试站内消息功能 - 标记消息为已读")
    void testMarkAsRead() throws Exception {
        mockMvc.perform(put("/api/notifications/1/read")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("测试站内消息功能 - 全部标记为已读")
    void testMarkAllAsRead() throws Exception {
        mockMvc.perform(post("/api/notifications/mark-all-read")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("测试站内消息功能 - 删除消息")
    void testDeleteNotification() throws Exception {
        mockMvc.perform(delete("/api/notifications/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // ==================== 提醒统计测试 ====================

    @Test
    @DisplayName("测试提醒统计接口")
    void testGetReminderStats() throws Exception {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", 50);
        stats.put("pending", 40);
        stats.put("notified", 10);
        stats.put("resolved", 0);
        stats.put("highPriority", 10);

        when(customerAssistantService.getReminderStats()).thenReturn(stats);

        mockMvc.perform(get("/api/customer-assistant/reminders/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(50))
                .andExpect(jsonPath("$.data.pending").value(40))
                .andExpect(jsonPath("$.data.highPriority").value(10));
    }

    // ==================== 获取单个提醒详情测试 ====================

    @Test
    @DisplayName("测试获取提醒详情 - 成功")
    void testGetReminderById_Success() throws Exception {
        when(customerAssistantService.getReminderById(1L)).thenReturn(testReminder);

        mockMvc.perform(get("/api/customer-assistant/reminders/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("申请即将过期提醒"));
    }

    @Test
    @DisplayName("测试获取提醒详情 - 不存在")
    void testGetReminderById_NotFound() throws Exception {
        when(customerAssistantService.getReminderById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/customer-assistant/reminders/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    // ==================== 获取客户提醒列表测试 ====================

    @Test
    @DisplayName("测试获取指定客户的提醒列表")
    void testGetCustomerReminders() throws Exception {
        when(customerAssistantService.getCustomerReminders(1L)).thenReturn(Arrays.asList(testReminder));

        mockMvc.perform(get("/api/customer-assistant/reminders/customer/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].customerId").value(1));
    }
}
