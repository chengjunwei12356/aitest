package com.example.aitest.service.impl;

import com.example.aitest.entity.Role;
import com.example.aitest.mapper.RoleMapper;
import com.example.aitest.mapper.RoleMenuMapper;
import com.example.aitest.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RoleMenuMapper roleMenuMapper;

    @Override
    public Role findById(Long id) {
        return roleMapper.findById(id);
    }

    @Override
    public List<Role> findAll() {
        return roleMapper.findAll();
    }

    @Override
    public List<Role> findByPage(int page, int size) {
        return roleMapper.findByPage((page - 1) * size, size);
    }

    @Override
    public int count() {
        List<Role> all = roleMapper.findAll();
        return all != null ? all.size() : 0;
    }

    @Override
    @Transactional
    public Role create(String name, String code, String description) {
        Role existing = roleMapper.findByCode(code);
        if (existing != null) {
            throw new RuntimeException("角色编码已存在");
        }
        Role role = Role.builder()
                .name(name)
                .code(code)
                .description(description)
                .status(1)
                .build();
        roleMapper.insert(role);
        log.info("创建角色成功：{}", code);
        return role;
    }

    @Override
    @Transactional
    public Role update(Long id, String name, String code, String description, Integer status) {
        Role existing = roleMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("角色不存在");
        }
        Role role = Role.builder()
                .id(id)
                .name(name)
                .code(code)
                .description(description)
                .status(status)
                .build();
        roleMapper.update(role);
        log.info("更新角色成功：{}", id);
        return role;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        roleMapper.delete(id);
        log.info("删除角色成功：{}", id);
    }

    @Override
    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        roleMenuMapper.deleteByRoleId(roleId);
        if (menuIds != null) {
            for (Long menuId : menuIds) {
                roleMenuMapper.insert(roleId, menuId);
            }
        }
        log.info("分配菜单成功，roleId: {}, menuCount: {}", roleId, menuIds != null ? menuIds.size() : 0);
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        return roleMenuMapper.findMenuIdsByRoleId(roleId);
    }
}
