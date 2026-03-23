package com.example.aitest.service;

import com.example.aitest.entity.Menu;
import java.util.List;

/**
 * 菜单服务接口
 */
public interface MenuService {
    Menu findById(Long id);
    List<Menu> findAll();
    List<Menu> findByStatus(Integer status);
    List<Menu> findByRoleIds(List<Long> roleIds);
}
