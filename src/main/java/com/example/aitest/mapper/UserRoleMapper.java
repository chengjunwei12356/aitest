package com.example.aitest.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户 - 角色关联 Mapper 接口
 */
@Mapper
public interface UserRoleMapper {
    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);
    List<Long> findUserIdsByRoleId(@Param("roleId") Long roleId);
    int insert(@Param("userId") Long userId, @Param("roleId") Long roleId);
    int deleteByUserId(@Param("userId") Long userId);
    int deleteByRoleId(@Param("roleId") Long roleId);
    int delete(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
