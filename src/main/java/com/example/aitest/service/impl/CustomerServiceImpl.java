package com.example.aitest.service.impl;

import com.example.aitest.config.BusinessException;
import com.example.aitest.common.ResultCode;
import com.example.aitest.entity.Customer;
import com.example.aitest.mapper.CustomerMapper;
import com.example.aitest.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;

    @Override
    public Customer findById(Long id) {
        log.info("查询客户信息，id: {}", id);
        Customer customer = customerMapper.findById(id);
        if (customer == null) {
            throw new BusinessException(ResultCode.CUSTOMER_NOT_FOUND);
        }
        return customer;
    }

    @Override
    public Customer findByIdNo(String idNo) {
        log.info("根据身份证号查询客户，idNo: {}", idNo);
        return customerMapper.findByIdNo(idNo);
    }

    @Override
    public List<Customer> search(String keyword) {
        log.info("搜索客户，keyword: {}", keyword);
        return customerMapper.search(keyword);
    }

    @Override
    @Transactional
    public Customer create(Customer customer) {
        log.info("创建客户，name: {}, idNo: {}", customer.getName(), customer.getIdNo());

        // 检查身份证号是否已存在
        Customer existing = customerMapper.findByIdNo(customer.getIdNo());
        if (existing != null) {
            throw new BusinessException("客户已存在：" + existing.getName());
        }

        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        customerMapper.insert(customer);

        log.info("客户创建成功，id: {}", customer.getId());
        return customer;
    }

    @Override
    @Transactional
    public Customer update(Customer customer) {
        log.info("更新客户信息，id: {}", customer.getId());

        Customer existing = customerMapper.findById(customer.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.CUSTOMER_NOT_FOUND);
        }

        customer.setUpdatedAt(LocalDateTime.now());
        customerMapper.update(customer);

        log.info("客户信息更新成功，id: {}", customer.getId());
        return customer;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("删除客户，id: {}", id);

        Customer existing = customerMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.CUSTOMER_NOT_FOUND);
        }

        // TODO: 检查是否有关联的贷款申请
        // int applicationCount = loanApplicationMapper.countByCustomerId(id);
        // if (applicationCount > 0) {
        //     throw new BusinessException("该客户有" + applicationCount + "笔贷款申请，无法删除");
        // }

        customerMapper.deleteById(id);
        log.info("客户删除成功，id: {}", id);
    }

    @Override
    public List<Customer> findAll() {
        log.info("查询所有客户列表");
        return customerMapper.search("");
    }
}
