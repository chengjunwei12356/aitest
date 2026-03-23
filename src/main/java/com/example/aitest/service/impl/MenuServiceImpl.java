package com.example.aitest.service.impl;

import com.example.aitest.entity.Menu;
import com.example.aitest.mapper.MenuMapper;
import com.example.aitest.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 菜单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    @Override
    public Menu findById(Long id) {
        return menuMapper.findById(id);
    }

    @Override
    public List<Menu> findAll() {
        return menuMapper.findAll();
    }

    @Override
    public List<Menu> findByStatus(Integer status) {
        return menuMapper.findByStatus(status);
    }

    @Override
    public List<Menu> findByRoleIds(List<Long> roleIds) {
        return menuMapper.findByRoleIds(roleIds);
    }
}
