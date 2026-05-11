package com.example.aitest.controller;

import com.example.aitest.common.Result;
import com.example.aitest.dto.ApplicationDTO;
import com.example.aitest.entity.GuaranteeType;
import com.example.aitest.entity.LoanApplication;
import com.example.aitest.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ApplicationController 单元测试
 */
@WebMvcTest(ApplicationController.class)
@DisplayName("贷款申请控制器测试")
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicationService applicationService;

    private LoanApplication testApplication;

    @BeforeEach
    void setUp() {
        testApplication = new LoanApplication();
        testApplication.setId(1L);
        testApplication.setApplicationNo("A202605110001");
        testApplication.setCustomerName("张三");
        testApplication.setCustomerIdNo("110101199001011234");
        testApplication.setCustomerPhone("13800138000");
        testApplication.setLoanAmount(new BigDecimal("500000"));
        testApplication.setLoanTerm(12);
        testApplication.setLoanPurpose("消费");
        testApplication.setGuaranteeType(GuaranteeType.CREDIT);
        testApplication.setStatus(com.example.aitest.entity.ApplicationStatus.DRAFT);
    }

    @Test
    @DisplayName("获取申请列表 - 成功")
    void testList_Success() throws Exception {
        List<LoanApplication> applications = Arrays.asList(testApplication);
        when(applicationService.findAll(null, null, null, null, null, null, null))
                .thenReturn(applications);

        mockMvc.perform(get("/api/applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].customerName").value("张三"));
    }

    @Test
    @DisplayName("获取申请详情 - 成功")
    void testGet_Success() throws Exception {
        when(applicationService.findById(1L)).thenReturn(testApplication);

        mockMvc.perform(get("/api/applications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.customerName").value("张三"));
    }

    @Test
    @DisplayName("创建申请 - 成功")
    void testCreate_Success() throws Exception {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setCustomerId(1L);
        dto.setCustomerName("张三");
        dto.setCustomerIdNo("110101199001011234");
        dto.setCustomerPhone("13800138000");
        dto.setLoanAmount(new BigDecimal("500000"));
        dto.setLoanTerm(12);
        dto.setLoanPurpose("消费");
        dto.setGuaranteeType(GuaranteeType.CREDIT);

        when(applicationService.create(any(LoanApplication.class), eq(1L)))
                .thenReturn(testApplication);

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":1,\"customerName\":\"张三\",\"customerIdNo\":\"110101199001011234\",\"customerPhone\":\"13800138000\",\"loanAmount\":500000,\"loanTerm\":12,\"loanPurpose\":\"消费\",\"guaranteeType\":\"CREDIT\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除申请 - 成功")
    void testDelete_Success() throws Exception {
        mockMvc.perform(delete("/api/applications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("提交申请 - 成功")
    void testSubmit_Success() throws Exception {
        mockMvc.perform(post("/api/applications/1/submit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
