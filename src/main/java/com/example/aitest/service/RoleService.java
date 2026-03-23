package com.example.aitest.service;

import com.example.aitest.entity.Role;
import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {
    Role findById(Long id);
    List<Role> findAll();
    List<Role> findByPage(int page, int size);
    int count();
    Role create(String name, String code, String description);
    Role update(Long id, String name, String code, String description, Integer status);
    void delete(Long id);
    void assignMenus(Long roleId, List<Long> menuIds);
    List<Long> getMenuIdsByRoleId(Long roleId);
}
