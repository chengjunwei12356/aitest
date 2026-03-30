package com.example.aitest.mapper;

import com.example.aitest.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户信息 Mapper 接口
 */
@Mapper
public interface CustomerMapper {

    /**
     * 根据 ID 查询客户
     */
    Customer findById(@Param("id") Long id);

    /**
     * 根据身份证号查询客户
     */
    Customer findByIdNo(@Param("idNo") String idNo);

    /**
     * 搜索客户列表
     */
    List<Customer> search(@Param("keyword") String keyword);

    /**
     * 插入客户
     */
    int insert(Customer customer);

    /**
     * 更新客户
     */
    int update(Customer customer);
}
