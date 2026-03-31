package com.example.aitest.controller;

import com.example.aitest.common.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 工作台数据控制器测试
 */
@SpringBootTest
class DashboardControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("获取待办任务统计 - 成功")
    void getTodoStats_Success() throws Exception {
        mockMvc.perform(get("/api/dashboard/todo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.application").value(12))
                .andExpect(jsonPath("$.data.approval").value(5))
                .andExpect(jsonPath("$.data.contract").value(3))
                .andExpect(jsonPath("$.data.loan").value(8))
                .andExpect(jsonPath("$.data.repayment").value(20))
                .andExpect(jsonPath("$.data.warning").value(3))
                .andExpect(jsonPath("$.data.postloan").value(7))
                .andExpect(jsonPath("$.data.other").value(15));
    }

    @Test
    @DisplayName("待办任务统计 - 验证数据结构")
    void getTodoStats_ValidateStructure() throws Exception {
        String response = mockMvc.perform(get("/api/dashboard/todo"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotNull(response);
        assertTrue(response.contains("\"code\":200"));
        assertTrue(response.contains("\"data\":"));
    }
}
