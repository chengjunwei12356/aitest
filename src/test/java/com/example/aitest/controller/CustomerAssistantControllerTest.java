package com.example.aitest.controller;

import com.example.aitest.common.Result;
import com.example.aitest.config.GlobalExceptionHandler;
import com.example.aitest.dto.CustomerReminderDTO;
import com.example.aitest.dto.KnowledgeArticleDTO;
import com.example.aitest.dto.ReminderResolveRequest;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 客户经理助手控制器测试
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
        testReminder.setReminderType(ReminderType.APPLICATION_EXPIRE);
        testReminder.setTitle("申请即将过期提醒");

        testArticle = new KnowledgeArticleDTO();
        testArticle.setId(1L);
        testArticle.setCategory(KnowledgeCategory.FAQ);
        testArticle.setTitle("常见问题解答");
    }

    // ==================== getReminders 测试 ====================

    @Test
    @DisplayName("获取提醒列表 - 成功（无过滤）")
    void getReminders_Success_NoFilter() throws Exception {
        when(customerAssistantService.getReminders(any(), anyString())).thenReturn(Arrays.asList(testReminder));

        mockMvc.perform(get("/api/customer-assistant/reminders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取提醒列表 - 按类型过滤")
    void getReminders_Success_WithType() throws Exception {
        when(customerAssistantService.getReminders(any(), anyString())).thenReturn(Arrays.asList(testReminder));

        mockMvc.perform(get("/api/customer-assistant/reminders")
                        .param("type", "APPLICATION_EXPIRE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取提醒列表 - 按状态过滤")
    void getReminders_Success_WithStatus() throws Exception {
        when(customerAssistantService.getReminders(any(), anyString())).thenReturn(Arrays.asList(testReminder));

        mockMvc.perform(get("/api/customer-assistant/reminders")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== getCustomerReminders 测试 ====================

    @Test
    @DisplayName("获取客户提醒 - 成功")
    void getCustomerReminders_Success() throws Exception {
        when(customerAssistantService.getCustomerReminders(anyLong())).thenReturn(Arrays.asList(testReminder));

        mockMvc.perform(get("/api/customer-assistant/reminders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== resolveReminder 测试 ====================

    @Test
    @DisplayName("解决提醒 - 成功")
    void resolveReminder_Success() throws Exception {
        doNothing().when(customerAssistantService).resolveReminder(any(ReminderResolveRequest.class));

        ReminderResolveRequest request = new ReminderResolveRequest();
        request.setReminderId(1L);
        request.setNote("已处理");

        mockMvc.perform(post("/api/customer-assistant/reminders/resolve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== searchKnowledge 测试 ====================

    @Test
    @DisplayName("搜索知识库 - 成功（无过滤）")
    void searchKnowledge_Success_NoFilter() throws Exception {
        when(customerAssistantService.searchKnowledge(any(), anyString())).thenReturn(Arrays.asList(testArticle));

        mockMvc.perform(get("/api/customer-assistant/knowledge"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("搜索知识库 - 按分类过滤")
    void searchKnowledge_Success_WithCategory() throws Exception {
        when(customerAssistantService.searchKnowledge(any(), anyString())).thenReturn(Arrays.asList(testArticle));

        mockMvc.perform(get("/api/customer-assistant/knowledge")
                        .param("category", "FAQ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("搜索知识库 - 按关键词搜索")
    void searchKnowledge_Success_WithKeyword() throws Exception {
        when(customerAssistantService.searchKnowledge(any(), anyString())).thenReturn(Arrays.asList(testArticle));

        mockMvc.perform(get("/api/customer-assistant/knowledge")
                        .param("keyword", "贷款"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== getKnowledgeArticle 测试 ====================

    @Test
    @DisplayName("获取文章详情 - 成功")
    void getKnowledgeArticle_Success() throws Exception {
        when(customerAssistantService.getKnowledgeArticle(anyLong())).thenReturn(testArticle);

        mockMvc.perform(get("/api/customer-assistant/knowledge/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取文章详情 - 不存在")
    void getKnowledgeArticle_NotFound() throws Exception {
        when(customerAssistantService.getKnowledgeArticle(anyLong())).thenReturn(null);

        mockMvc.perform(get("/api/customer-assistant/knowledge/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }
}
