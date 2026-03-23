package com.example.aitest.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 角色 - 菜单关联 Mapper 接口
 */
@Mapper
public interface RoleMenuMapper {
    List<Long> findMenuIdsByRoleId(@Param("roleId") Long roleId);
    int insert(@Param("roleId") Long roleId, @Param("menuId") Long menuId);
    int deleteByRoleId(@Param("roleId") Long roleId);
    int delete(@Param("roleId") Long roleId, @Param("menuId") Long menuId);
}
