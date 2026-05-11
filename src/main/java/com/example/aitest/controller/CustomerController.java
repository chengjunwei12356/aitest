package com.example.aitest.controller;

import com.example.aitest.common.Result;
import com.example.aitest.dto.CustomerDTO;
import com.example.aitest.entity.Customer;
import com.example.aitest.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户管理控制器
 */
@Tag(name = "客户管理", description = "客户信息的增删改查接口")
@Slf4j
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 获取客户列表
     */
    @Operation(summary = "获取客户列表", description = "查询所有客户信息")
    @GetMapping
    public Result<List<Customer>> list() {
        List<Customer> customers = customerService.findAll();
        return Result.success(customers);
    }

    /**
     * 搜索客户
     */
    @Operation(summary = "搜索客户", description = "根据关键词搜索客户（姓名/手机号/身份证号）")
    @GetMapping("/search")
    public Result<List<Customer>> search(
            @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword) {
        List<Customer> customers = customerService.search(keyword);
        return Result.success(customers);
    }

    /**
     * 获取客户详情
     */
    @Operation(summary = "获取客户详情", description = "根据ID查询客户详细信息")
    @GetMapping("/{id}")
    public Result<Customer> get(@Parameter(description = "客户ID") @PathVariable Long id) {
        Customer customer = customerService.findById(id);
        if (customer == null) {
            return Result.error("客户不存在");
        }
        return Result.success(customer);
    }

    /**
     * 根据身份证号查询客户
     */
    @Operation(summary = "根据身份证号查询", description = "通过身份证号码查询客户信息")
    @GetMapping("/idno/{idNo}")
    public Result<Customer> getByIdNo(@Parameter(description = "身份证号") @PathVariable String idNo) {
        Customer customer = customerService.findByIdNo(idNo);
        if (customer == null) {
            return Result.error("客户不存在");
        }
        return Result.success(customer);
    }

    /**
     * 创建客户
     */
    @Operation(summary = "创建客户", description = "新增客户信息")
    @PostMapping
    public Result<Customer> create(@Valid @RequestBody CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setIdNo(dto.getIdNo());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());

        Customer result = customerService.create(customer);
        return Result.success(result);
    }

    /**
     * 更新客户
     */
    @Operation(summary = "更新客户", description = "修改客户信息")
    @PutMapping("/{id}")
    public Result<Customer> update(
            @Parameter(description = "客户ID") @PathVariable Long id,
            @Valid @RequestBody CustomerDTO dto) {
        Customer customer = customerService.findById(id);
        if (customer == null) {
            return Result.error("客户不存在");
        }

        customer.setName(dto.getName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());

        Customer result = customerService.update(customer);
        return Result.success(result);
    }

    /**
     * 删除客户
     */
    @Operation(summary = "删除客户", description = "根据ID删除客户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "客户ID") @PathVariable Long id) {
        Customer customer = customerService.findById(id);
        if (customer == null) {
            return Result.error("客户不存在");
        }

        customerService.delete(id);
        return Result.success();
    }
}
