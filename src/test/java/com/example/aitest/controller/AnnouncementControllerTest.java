package com.example.aitest.controller;

import com.example.aitest.config.BusinessException;
import com.example.aitest.config.GlobalExceptionHandler;
import com.example.aitest.service.AnnouncementService;
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

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 公告管理控制器测试
 */
class AnnouncementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AnnouncementService announcementService;

    @InjectMocks
    private AnnouncementController announcementController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(announcementController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("获取公告列表 - 成功")
    void list_Success() throws Exception {
        mockMvc.perform(get("/api/announcements")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取公告列表 - 默认参数")
    void list_DefaultParams() throws Exception {
        mockMvc.perform(get("/api/announcements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取公告详情 - 成功")
    void get_Success() throws Exception {
        mockMvc.perform(get("/api/announcements/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取公告详情 - 不存在")
    void get_NotFound() throws Exception {
        when(announcementService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/announcements/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value((Object) null));
    }

    @Test
    @DisplayName("删除公告 - 成功")
    void delete_Success() throws Exception {
        doNothing().when(announcementService).delete(anyLong());

        mockMvc.perform(delete("/api/announcements/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(announcementService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("删除公告 - 不存在")
    void delete_NotFound() throws Exception {
        doThrow(new BusinessException(4001, "公告不存在"))
                .when(announcementService).delete(999L);

        mockMvc.perform(delete("/api/announcements/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4001))
                .andExpect(jsonPath("$.message").value("公告不存在"));
    }

    @Test
    @DisplayName("发布公告 - 成功")
    void publish_Success() throws Exception {
        doNothing().when(announcementService).publish(anyLong());

        mockMvc.perform(put("/api/announcements/1/publish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(announcementService, times(1)).publish(1L);
    }

    @Test
    @DisplayName("下架公告 - 成功")
    void unpublish_Success() throws Exception {
        doNothing().when(announcementService).unpublish(anyLong());

        mockMvc.perform(put("/api/announcements/1/unpublish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(announcementService, times(1)).unpublish(1L);
    }

    @Test
    @DisplayName("创建公告 - 成功")
    void create_Success() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("title", "测试公告");
        body.put("content", "测试内容");
        body.put("publishStatus", "0");

        mockMvc.perform(post("/api/announcements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("更新公告 - 成功")
    void update_Success() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("title", "更新后的标题");
        body.put("content", "更新后的内容");
        body.put("publishStatus", "1");

        mockMvc.perform(put("/api/announcements/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
