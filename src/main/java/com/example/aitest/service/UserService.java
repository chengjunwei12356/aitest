package com.example.aitest.service;

import com.example.aitest.dto.LoginRequest;
import com.example.aitest.dto.LoginResponse;
import com.example.aitest.entity.User;
import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);

    /**
     * 根据用户 ID 查询用户
     * @param id 用户 ID
     * @return 用户信息
     */
    User findById(Long id);

    /**
     * 获取用户的角色 ID 列表
     * @param userId 用户 ID
     * @return 角色 ID 列表
     */
    List<Long> getRoleIdsByUserId(Long userId);

    /**
     * 为用户分配角色
     * @param userId 用户 ID
     * @param roleIds 角色 ID 列表
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 重置用户密码
     * @param userId 用户 ID
     * @param newPassword 新密码
     */
    void resetPassword(Long userId, String newPassword);

    /**
     * 更新用户状态
     * @param userId 用户 ID
     * @param status 状态
     */
    void updateStatus(Long userId, Integer status);

    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<User> findAll();
}
