package com.example.aitest.mapper;

import com.example.aitest.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 菜单 Mapper 接口
 */
@Mapper
public interface MenuMapper {
    Menu findById(@Param("id") Long id);
    List<Menu> findAll();
    List<Menu> findByStatus(@Param("status") Integer status);
    List<Menu> findByRoleIds(@Param("roleIds") List<Long> roleIds);
    int insert(Menu menu);
    int update(Menu menu);
    int delete(@Param("id") Long id);
}
