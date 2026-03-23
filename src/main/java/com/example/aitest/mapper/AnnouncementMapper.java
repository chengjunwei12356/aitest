package com.example.aitest.mapper;

import com.example.aitest.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告 Mapper 接口
 */
@Mapper
public interface AnnouncementMapper {
    Announcement findById(@Param("id") Long id);
    List<Announcement> findByPage(@Param("offset") int offset, @Param("limit") int limit);
    List<Announcement> findPublished(@Param("limit") int limit);
    int count();
    int insert(Announcement announcement);
    int update(Announcement announcement);
    int delete(@Param("id") Long id);
    int updatePublishStatus(@Param("id") Long id, @Param("status") Integer status, @Param("publishTime") LocalDateTime publishTime);
}
