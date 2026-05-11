package com.example.aitest.service.impl;

import com.example.aitest.config.BusinessException;
import com.example.aitest.dto.CustomerReminderDTO;
import com.example.aitest.dto.KnowledgeArticleDTO;
import com.example.aitest.dto.ReminderResolveRequest;
import com.example.aitest.entity.CustomerReminder;
import com.example.aitest.entity.KnowledgeArticle;
import com.example.aitest.entity.KnowledgeCategory;
import com.example.aitest.entity.ReminderType;
import com.example.aitest.mapper.CustomerAssistantMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        testReminder.setCustomerName("张三");
        testReminder.setCustomerPhone("13800138001");
        testReminder.setReminderType(ReminderType.APPLICATION_EXPIRE);
        testReminder.setTitle("申请即将过期提醒");
        testReminder.setPriority(2);
        testReminder.setStatus("PENDING");

        testArticle = new KnowledgeArticleDTO();
        testArticle.setId(1L);
        testArticle.setCategory(KnowledgeCategory.FAQ);
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
    @DisplayName("获取提醒列表 - 按状态过滤")
    void getReminders_Success_WithStatus() {
        List<CustomerReminderDTO> mockList = Arrays.asList(testReminder);
        when(customerAssistantMapper.getReminders(isNull(), anyString(), any())).thenReturn(mockList);

        List<CustomerReminderDTO> result = customerAssistantService.getReminders(null, "PENDING");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(customerAssistantMapper, times(1)).getReminders(isNull(), anyString(), any());
    }

    @Test
    @DisplayName("获取提醒列表 - 空结果")
    void getReminders_Empty() {
        when(customerAssistantMapper.getReminders(anyString(), anyString(), any())).thenReturn(Collections.emptyList());

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

    // ==================== getReminderById 测试 ====================

    @Test
    @DisplayName("获取提醒详情 - 成功")
    void getReminderById_Success() {
        when(customerAssistantMapper.getReminderById(1L)).thenReturn(testReminder);

        CustomerReminderDTO result = customerAssistantService.getReminderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("申请即将过期", result.getReminderTypeDescription());
    }

    @Test
    @DisplayName("获取提醒详情 - 不存在")
    void getReminderById_NotFound() {
        when(customerAssistantMapper.getReminderById(999L)).thenReturn(null);

        CustomerReminderDTO result = customerAssistantService.getReminderById(999L);

        assertNull(result);
    }

    // ==================== createReminder 测试 ====================

    @Test
    @DisplayName("创建提醒 - 成功")
    void createReminder_Success() {
        CustomerReminder reminder = new CustomerReminder();
        reminder.setCustomerId(1L);
        reminder.setReminderType(ReminderType.APPLICATION_EXPIRE);
        reminder.setTitle("测试提醒");
        reminder.setPriority(2);
        reminder.setDueDate(LocalDateTime.now().plusDays(7));

        when(customerAssistantMapper.createReminder(any(CustomerReminder.class))).thenAnswer(invocation -> {
            CustomerReminder r = invocation.getArgument(0);
            r.setId(1L);
            return 1;
        });

        Long result = customerAssistantService.createReminder(reminder);

        assertNotNull(result);
        assertEquals(1L, result);
        verify(customerAssistantMapper, times(1)).createReminder(any(CustomerReminder.class));
    }

    // ==================== updateReminder 测试 ====================

    @Test
    @DisplayName("更新提醒 - 成功")
    void updateReminder_Success() {
        CustomerReminder reminder = new CustomerReminder();
        reminder.setId(1L);
        reminder.setTitle("更新后的标题");

        when(customerAssistantMapper.updateReminder(any(CustomerReminder.class))).thenReturn(1);

        boolean result = customerAssistantService.updateReminder(reminder);

        assertTrue(result);
    }

    @Test
    @DisplayName("更新提醒 - 失败")
    void updateReminder_Fail() {
        CustomerReminder reminder = new CustomerReminder();
        reminder.setId(999L);
        reminder.setTitle("更新后的标题");

        when(customerAssistantMapper.updateReminder(any(CustomerReminder.class))).thenReturn(0);

        boolean result = customerAssistantService.updateReminder(reminder);

        assertFalse(result);
    }

    // ==================== deleteReminder 测试 ====================

    @Test
    @DisplayName("删除提醒 - 成功")
    void deleteReminder_Success() {
        when(customerAssistantMapper.deleteReminder(eq(1L), any())).thenReturn(1);

        boolean result = customerAssistantService.deleteReminder(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("删除提醒 - 失败")
    void deleteReminder_Fail() {
        when(customerAssistantMapper.deleteReminder(eq(999L), any())).thenReturn(0);

        boolean result = customerAssistantService.deleteReminder(999L);

        assertFalse(result);
    }

    // ==================== getReminderStats 测试 ====================

    @Test
    @DisplayName("获取提醒统计 - 成功")
    void getReminderStats_Success() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", 10);
        stats.put("pending", 5);
        stats.put("notified", 2);
        stats.put("resolved", 3);
        stats.put("highPriority", 1);

        when(customerAssistantMapper.getReminderStats(any())).thenReturn(stats);

        Map<String, Integer> result = customerAssistantService.getReminderStats();

        assertNotNull(result);
        assertEquals(10, result.get("total"));
        assertEquals(5, result.get("pending"));
    }

    // ==================== resolveReminder 测试 ====================

    @Test
    @DisplayName("解决提醒 - 成功")
    void resolveReminder_Success() {
        ReminderResolveRequest request = new ReminderResolveRequest();
        request.setReminderId(1L);
        request.setNote("已处理");

        when(customerAssistantMapper.resolveReminder(eq(1L), eq("已处理"), any())).thenReturn(1);

        boolean result = customerAssistantService.resolveReminder(request);

        assertTrue(result);
        verify(customerAssistantMapper, times(1)).resolveReminder(eq(1L), eq("已处理"), any());
    }

    @Test
    @DisplayName("解决提醒 - 不存在或无权限")
    void resolveReminder_NotFound() {
        ReminderResolveRequest request = new ReminderResolveRequest();
        request.setReminderId(999L);
        request.setNote("已处理");

        when(customerAssistantMapper.resolveReminder(eq(999L), any(), any())).thenReturn(0);

        assertThrows(BusinessException.class, () -> {
            customerAssistantService.resolveReminder(request);
        });
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

        List<KnowledgeArticleDTO> result = customerAssistantService.searchKnowledge(KnowledgeCategory.FAQ, null);

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

    // ==================== createKnowledgeArticle 测试 ====================

    @Test
    @DisplayName("创建知识库文章 - 成功")
    void createKnowledgeArticle_Success() {
        KnowledgeArticle article = new KnowledgeArticle();
        article.setCategory(KnowledgeCategory.FAQ);
        article.setTitle("新文章");
        article.setContent("文章内容");

        when(customerAssistantMapper.createKnowledgeArticle(any(KnowledgeArticle.class))).thenAnswer(invocation -> {
            KnowledgeArticle a = invocation.getArgument(0);
            a.setId(1L);
            return 1;
        });

        Long result = customerAssistantService.createKnowledgeArticle(article);

        assertNotNull(result);
        assertEquals(1L, result);
    }

    @Test
    @DisplayName("创建知识库文章 - 默认激活状态")
    void createKnowledgeArticle_DefaultActive() {
        KnowledgeArticle article = new KnowledgeArticle();
        article.setCategory(KnowledgeCategory.FAQ);
        article.setTitle("新文章");

        when(customerAssistantMapper.createKnowledgeArticle(any(KnowledgeArticle.class))).thenReturn(1);

        customerAssistantService.createKnowledgeArticle(article);

        assertTrue(article.getIsActive());
    }

    // ==================== updateKnowledgeArticle 测试 ====================

    @Test
    @DisplayName("更新知识库文章 - 成功")
    void updateKnowledgeArticle_Success() {
        KnowledgeArticle article = new KnowledgeArticle();
        article.setId(1L);
        article.setTitle("更新后的标题");

        when(customerAssistantMapper.updateKnowledgeArticle(any(KnowledgeArticle.class))).thenReturn(1);

        boolean result = customerAssistantService.updateKnowledgeArticle(article);

        assertTrue(result);
    }

    @Test
    @DisplayName("更新知识库文章 - 失败")
    void updateKnowledgeArticle_Fail() {
        KnowledgeArticle article = new KnowledgeArticle();
        article.setId(999L);
        article.setTitle("更新后的标题");

        when(customerAssistantMapper.updateKnowledgeArticle(any(KnowledgeArticle.class))).thenReturn(0);

        boolean result = customerAssistantService.updateKnowledgeArticle(article);

        assertFalse(result);
    }

    // ==================== deleteKnowledgeArticle 测试 ====================

    @Test
    @DisplayName("删除知识库文章 - 成功")
    void deleteKnowledgeArticle_Success() {
        when(customerAssistantMapper.deleteKnowledgeArticle(1L)).thenReturn(1);

        boolean result = customerAssistantService.deleteKnowledgeArticle(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("删除知识库文章 - 失败")
    void deleteKnowledgeArticle_Fail() {
        when(customerAssistantMapper.deleteKnowledgeArticle(999L)).thenReturn(0);

        boolean result = customerAssistantService.deleteKnowledgeArticle(999L);

        assertFalse(result);
    }

    // ==================== getHotArticles 测试 ====================

    @Test
    @DisplayName("获取热门文章 - 成功")
    void getHotArticles_Success() {
        List<KnowledgeArticleDTO> mockList = Arrays.asList(testArticle);
        when(customerAssistantMapper.getHotArticles(10)).thenReturn(mockList);

        List<KnowledgeArticleDTO> result = customerAssistantService.getHotArticles(10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(customerAssistantMapper, times(1)).getHotArticles(10);
    }

    @Test
    @DisplayName("获取热门文章 - 默认数量")
    void getHotArticles_DefaultLimit() {
        List<KnowledgeArticleDTO> mockList = Arrays.asList(testArticle);
        when(customerAssistantMapper.getHotArticles(10)).thenReturn(mockList);

        List<KnowledgeArticleDTO> result = customerAssistantService.getHotArticles(null);

        assertNotNull(result);
        verify(customerAssistantMapper, times(1)).getHotArticles(10);
    }

    @Test
    @DisplayName("获取热门文章 - 无效数量使用默认值")
    void getHotArticles_InvalidLimit() {
        List<KnowledgeArticleDTO> mockList = Arrays.asList(testArticle);
        when(customerAssistantMapper.getHotArticles(10)).thenReturn(mockList);

        List<KnowledgeArticleDTO> result = customerAssistantService.getHotArticles(0);

        assertNotNull(result);
        verify(customerAssistantMapper, times(1)).getHotArticles(10);
    }
}
