package com.example.aitest.controller;

import com.example.aitest.common.Result;
import com.example.aitest.entity.Menu;
import com.example.aitest.entity.Role;
import com.example.aitest.service.MenuService;
import com.example.aitest.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final MenuService menuService;

    /**
     * 获取角色列表
     */
    @GetMapping
    public Result<List<Role>> list() {
        return Result.success(roleService.findAll());
    }

    /**
     * 获取角色详情（含菜单权限）
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> get(@PathVariable Long id) {
        Role role = roleService.findById(id);
        List<Long> menuIds = roleService.getMenuIdsByRoleId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("role", role);
        result.put("menuIds", menuIds);
        return Result.success(result);
    }

    /**
     * 创建角色
     */
    @PostMapping
    public Result<Role> create(@RequestBody Map<String, String> body) {
        Role role = roleService.create(
                body.get("name"),
                body.get("code"),
                body.get("description")
        );
        return Result.success(role);
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public Result<Role> update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Integer status = null;
        if (body.get("status") != null) {
            status = Integer.parseInt(body.get("status"));
        }
        Role role = roleService.update(
                id,
                body.get("name"),
                body.get("code"),
                body.get("description"),
                status
        );
        return Result.success(role);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success();
    }

    /**
     * 分配菜单权限
     */
    @PutMapping("/{id}/menus")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        roleService.assignMenus(id, body.get("menuIds"));
        return Result.success();
    }

    /**
     * 获取所有菜单（用于权限分配）
     */
    @GetMapping("/menus")
    public Result<List<Menu>> getMenus() {
        return Result.success(menuService.findAll());
    }
}
