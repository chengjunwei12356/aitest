package com.example.aitest.service;

import com.example.aitest.entity.Customer;
import java.util.List;

/**
 * 客户服务接口
 */
public interface CustomerService {

    /**
     * 根据 ID 查询客户
     * @param id 客户 ID
     * @return 客户信息
     */
    Customer findById(Long id);

    /**
     * 根据身份证号查询客户
     * @param idNo 身份证号
     * @return 客户信息
     */
    Customer findByIdNo(String idNo);

    /**
     * 搜索客户列表
     * @param keyword 关键词（姓名/手机号）
     * @return 客户列表
     */
    List<Customer> search(String keyword);

    /**
     * 创建客户
     * @param customer 客户信息
     * @return 创建后的客户
     */
    Customer create(Customer customer);

    /**
     * 更新客户信息
     * @param customer 客户信息
     * @return 更新后的客户
     */
    Customer update(Customer customer);

    /**
     * 删除客户
     * @param id 客户 ID
     */
    void delete(Long id);

    /**
     * 获取所有客户
     * @return 客户列表
     */
    List<Customer> findAll();
}
