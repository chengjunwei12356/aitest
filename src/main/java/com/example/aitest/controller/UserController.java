package com.example.aitest.controller;

import com.example.aitest.common.Result;
import com.example.aitest.entity.Role;
import com.example.aitest.entity.User;
import com.example.aitest.service.RoleService;
import com.example.aitest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    /**
     * 获取用户列表
     */
    @GetMapping
    public Result<List<User>> list() {
        List<User> list = userService.findAll();
        return Result.success(list);
    }

    /**
     * 获取用户详情（含角色）
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> get(@PathVariable Long id) {
        User user = userService.findById(id);
        List<Long> roleIds = userService.getRoleIdsByUserId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("roleIds", roleIds);
        return Result.success(result);
    }

    /**
     * 创建用户
     */
    @PostMapping
    public Result<User> create(@RequestBody Map<String, Object> body) {
        // TODO: 实现完整的新增用户逻辑
        return Result.success();
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result<User> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        // TODO: 实现更新逻辑
        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        // TODO: 实现删除逻辑
        return Result.success();
    }

    /**
     * 分配角色
     */
    @PutMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        userService.assignRoles(id, body.get("roleIds"));
        return Result.success();
    }

    /**
     * 重置密码
     */
    @PutMapping("/{id}/password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        userService.resetPassword(id, body.get("password"));
        return Result.success();
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        userService.updateStatus(id, body.get("status"));
        return Result.success();
    }

    /**
     * 获取所有角色（用于下拉框）
     */
    @GetMapping("/roles")
    public Result<List<Role>> getRoles() {
        return Result.success(roleService.findAll());
    }
}
