package com.example.aitest.mapper;

import com.example.aitest.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 角色 Mapper 接口
 */
@Mapper
public interface RoleMapper {
    Role findById(@Param("id") Long id);
    List<Role> findAll();
    List<Role> findByPage(@Param("offset") int offset, @Param("limit") int limit);
    Role findByCode(@Param("code") String code);
    int insert(Role role);
    int update(Role role);
    int delete(@Param("id") Long id);
}
