package com.example.aitest.service;

import com.example.aitest.config.BusinessException;
import com.example.aitest.entity.Customer;
import com.example.aitest.mapper.CustomerMapper;
import com.example.aitest.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * CustomerService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("客户服务测试")
class CustomerServiceTest {

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("张三");
        testCustomer.setIdNo("110101199001011234");
        testCustomer.setPhone("13800138000");
        testCustomer.setEmail("zhangsan@example.com");
        testCustomer.setAddress("北京市朝阳区");
    }

    @Test
    @DisplayName("根据ID查询客户 - 成功")
    void testFindById_Success() {
        when(customerMapper.findById(1L)).thenReturn(testCustomer);

        Customer result = customerService.findById(1L);

        assertNotNull(result);
        assertEquals("张三", result.getName());
        assertEquals("110101199001011234", result.getIdNo());
        verify(customerMapper, times(1)).findById(1L);
    }

    @Test
    @DisplayName("根据ID查询客户 - 不存在")
    void testFindById_NotFound() {
        when(customerMapper.findById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> {
            customerService.findById(999L);
        });
    }

    @Test
    @DisplayName("根据身份证号查询客户 - 成功")
    void testFindByIdNo_Success() {
        when(customerMapper.findByIdNo("110101199001011234")).thenReturn(testCustomer);

        Customer result = customerService.findByIdNo("110101199001011234");

        assertNotNull(result);
        assertEquals("张三", result.getName());
    }

    @Test
    @DisplayName("根据身份证号查询客户 - 不存在")
    void testFindByIdNo_NotFound() {
        when(customerMapper.findByIdNo(anyString())).thenReturn(null);

        Customer result = customerService.findByIdNo("999999999999999999");

        assertNull(result);
    }

    @Test
    @DisplayName("搜索客户列表")
    void testSearch() {
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerMapper.search("张三")).thenReturn(customers);

        List<Customer> result = customerService.search("张三");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("张三", result.get(0).getName());
    }

    @Test
    @DisplayName("创建客户 - 成功")
    void testCreate_Success() {
        when(customerMapper.findByIdNo(testCustomer.getIdNo())).thenReturn(null);
        when(customerMapper.insert(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(1L);
            return 1;
        });

        Customer result = customerService.create(testCustomer);

        assertNotNull(result);
        assertEquals("张三", result.getName());
        verify(customerMapper, times(1)).insert(any(Customer.class));
    }

    @Test
    @DisplayName("创建客户 - 身份证号已存在")
    void testCreate_DuplicateIdNo() {
        when(customerMapper.findByIdNo(testCustomer.getIdNo())).thenReturn(testCustomer);

        assertThrows(BusinessException.class, () -> {
            customerService.create(testCustomer);
        });
    }

    @Test
    @DisplayName("更新客户 - 成功")
    void testUpdate_Success() {
        when(customerMapper.findById(1L)).thenReturn(testCustomer);
        when(customerMapper.update(any(Customer.class))).thenReturn(1);

        testCustomer.setPhone("13900139000");
        Customer result = customerService.update(testCustomer);

        assertNotNull(result);
        assertEquals("13900139000", result.getPhone());
        verify(customerMapper, times(1)).update(any(Customer.class));
    }

    @Test
    @DisplayName("更新客户 - 不存在")
    void testUpdate_NotFound() {
        Customer notFoundCustomer = new Customer();
        notFoundCustomer.setId(999L);
        
        when(customerMapper.findById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> {
            customerService.update(notFoundCustomer);
        });
    }

    @Test
    @DisplayName("删除客户 - 成功")
    void testDelete_Success() {
        when(customerMapper.findById(1L)).thenReturn(testCustomer);
        when(customerMapper.deleteById(1L)).thenReturn(1);

        customerService.delete(1L);

        verify(customerMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("删除客户 - 不存在")
    void testDelete_NotFound() {
        when(customerMapper.findById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> {
            customerService.delete(999L);
        });
    }

    @Test
    @DisplayName("查询所有客户")
    void testFindAll() {
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerMapper.search("")).thenReturn(customers);

        List<Customer> result = customerService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
