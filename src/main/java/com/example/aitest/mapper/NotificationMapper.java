package com.example.aitest.mapper;

import com.example.aitest.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 站内消息 Mapper 接口
 */
@Mapper
public interface NotificationMapper {

    /**
     * 获取用户的消息列表
     *
     * @param userId 用户 ID
     * @param isRead 是否已读（null 表示全部）
     * @return 消息列表
     */
    List<Notification> findByUserId(@Param("userId") Long userId,
                                     @Param("isRead") Boolean isRead);

    /**
     * 获取未读消息数量
     *
     * @param userId 用户 ID
     * @return 未读消息数
     */
    int countUnread(@Param("userId") Long userId);

    /**
     * 插入消息
     *
     * @param notification 消息对象
     */
    void insert(Notification notification);

    /**
     * 标记为已读
     *
     * @param id     消息 ID
     * @param userId 用户 ID（用于权限校验）
     * @return 影响行数
     */
    int markAsRead(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 批量标记为已读
     *
     * @param userId 用户 ID
     * @return 影响行数
     */
    int markAllAsRead(@Param("userId") Long userId);

    /**
     * 删除消息
     *
     * @param id     消息 ID
     * @param userId 用户 ID（用于权限校验）
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);
}
