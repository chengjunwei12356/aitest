package com.example.aitest.service.impl;

import com.example.aitest.dto.CustomerReminderDTO;
import com.example.aitest.dto.KnowledgeArticleDTO;
import com.example.aitest.dto.ReminderResolveRequest;
import com.example.aitest.entity.ReminderType;
import com.example.aitest.mapper.CustomerAssistantMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 客户经理助手服务测试
 */
class CustomerAssistantServiceImplTest {

    @Mock
    private CustomerAssistantMapper customerAssistantMapper;

    @InjectMocks
    private CustomerAssistantServiceImpl customerAssistantService;

    private CustomerReminderDTO testReminder;
    private KnowledgeArticleDTO testArticle;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testReminder = new CustomerReminderDTO();
        testReminder.setId(1L);
        testReminder.setCustomerId(1L);
        testReminder.setReminderType(ReminderType.APPLICATION_EXPIRE);
        testReminder.setTitle("申请即将过期提醒");
        testReminder.setPriority(2);
        testReminder.setStatus("PENDING");

        testArticle = new KnowledgeArticleDTO();
        testArticle.setId(1L);
        testArticle.setCategory(com.example.aitest.entity.KnowledgeCategory.FAQ);
        testArticle.setTitle("常见问题解答");
        testArticle.setTags("FAQ,贷款");
        testArticle.setViewCount(10);
    }

    // ==================== getReminders 测试 ====================

    @Test
    @DisplayName("获取提醒列表 - 成功（无过滤）")
    void getReminders_Success_NoFilter() {
        List<CustomerReminderDTO> mockList = Arrays.asList(testReminder);
        when(customerAssistantMapper.getReminders(null, null, null)).thenReturn(mockList);

        List<CustomerReminderDTO> result = customerAssistantService.getReminders(null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("申请即将过期", result.get(0).getReminderTypeDescription());
        verify(customerAssistantMapper, times(1)).getReminders(null, null, null);
    }

    @Test
    @DisplayName("获取提醒列表 - 按类型过滤")
    void getReminders_Success_WithType() {
        List<CustomerReminderDTO> mockList = Arrays.asList(testReminder);
        when(customerAssistantMapper.getReminders(anyString(), isNull(), any())).thenReturn(mockList);

        List<CustomerReminderDTO> result = customerAssistantService.getReminders(ReminderType.APPLICATION_EXPIRE, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(customerAssistantMapper, times(1)).getReminders(anyString(), isNull(), any());
    }

    @Test
    @DisplayName("获取提醒列表 - 空结果")
    void getReminders_Empty() {
        when(customerAssistantMapper.getReminders(anyString(), anyString(), any())).thenReturn(Arrays.asList());

        List<CustomerReminderDTO> result = customerAssistantService.getReminders(ReminderType.APPROVAL_TIMEOUT, "PENDING");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ==================== getCustomerReminders 测试 ====================

    @Test
    @DisplayName("获取客户提醒 - 成功")
    void getCustomerReminders_Success() {
        List<CustomerReminderDTO> mockList = Arrays.asList(testReminder);
        when(customerAssistantMapper.getCustomerReminders(eq(1L), any())).thenReturn(mockList);

        List<CustomerReminderDTO> result = customerAssistantService.getCustomerReminders(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("申请即将过期", result.get(0).getReminderTypeDescription());
        verify(customerAssistantMapper, times(1)).getCustomerReminders(eq(1L), any());
    }

    // ==================== resolveReminder 测试 ====================

    @Test
    @DisplayName("解决提醒 - 成功")
    void resolveReminder_Success() {
        ReminderResolveRequest request = new ReminderResolveRequest();
        request.setReminderId(1L);
        request.setNote("已处理");

        when(customerAssistantMapper.resolveReminder(eq(1L), eq("已处理"), any())).thenReturn(1);

        assertDoesNotThrow(() -> customerAssistantService.resolveReminder(request));

        verify(customerAssistantMapper, times(1)).resolveReminder(eq(1L), eq("已处理"), any());
    }

    // ==================== searchKnowledge 测试 ====================

    @Test
    @DisplayName("搜索知识库 - 成功（无过滤）")
    void searchKnowledge_Success_NoFilter() {
        List<KnowledgeArticleDTO> mockList = Arrays.asList(testArticle);
        when(customerAssistantMapper.searchKnowledge(null, null)).thenReturn(mockList);

        List<KnowledgeArticleDTO> result = customerAssistantService.searchKnowledge(null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("常见问题", result.get(0).getCategoryDescription());
        verify(customerAssistantMapper, times(1)).searchKnowledge(null, null);
    }

    @Test
    @DisplayName("搜索知识库 - 按分类过滤")
    void searchKnowledge_Success_WithCategory() {
        List<KnowledgeArticleDTO> mockList = Arrays.asList(testArticle);
        when(customerAssistantMapper.searchKnowledge(anyString(), isNull())).thenReturn(mockList);

        List<KnowledgeArticleDTO> result = customerAssistantService.searchKnowledge(
                com.example.aitest.entity.KnowledgeCategory.FAQ, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(customerAssistantMapper, times(1)).searchKnowledge(anyString(), isNull());
    }

    @Test
    @DisplayName("搜索知识库 - 按关键词搜索")
    void searchKnowledge_Success_WithKeyword() {
        List<KnowledgeArticleDTO> mockList = Arrays.asList(testArticle);
        when(customerAssistantMapper.searchKnowledge(isNull(), anyString())).thenReturn(mockList);

        List<KnowledgeArticleDTO> result = customerAssistantService.searchKnowledge(null, "贷款");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(customerAssistantMapper, times(1)).searchKnowledge(isNull(), anyString());
    }

    // ==================== getKnowledgeArticle 测试 ====================

    @Test
    @DisplayName("获取文章详情 - 成功")
    void getKnowledgeArticle_Success() {
        when(customerAssistantMapper.getKnowledgeArticle(anyLong())).thenReturn(testArticle);
        doNothing().when(customerAssistantMapper).updateViewCount(anyLong());

        KnowledgeArticleDTO result = customerAssistantService.getKnowledgeArticle(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("常见问题", result.getCategoryDescription());
        verify(customerAssistantMapper, times(2)).getKnowledgeArticle(anyLong());
        verify(customerAssistantMapper, times(1)).updateViewCount(1L);
    }

    @Test
    @DisplayName("获取文章详情 - 不存在")
    void getKnowledgeArticle_NotFound() {
        when(customerAssistantMapper.getKnowledgeArticle(999L)).thenReturn(null);

        KnowledgeArticleDTO result = customerAssistantService.getKnowledgeArticle(999L);

        assertNull(result);
        verify(customerAssistantMapper, times(1)).getKnowledgeArticle(999L);
        verify(customerAssistantMapper, never()).updateViewCount(anyLong());
    }
}
