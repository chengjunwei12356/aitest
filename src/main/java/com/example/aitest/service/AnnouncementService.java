package com.example.aitest.service;

import com.example.aitest.entity.Announcement;
import java.util.List;

/**
 * 公告服务接口
 */
public interface AnnouncementService {
    Announcement findById(Long id);
    List<Announcement> findByPage(int page, int size);
    List<Announcement> findPublished(int limit);
    int count();
    Announcement create(String title, String content, Integer publishStatus, Long createdBy);
    Announcement update(Long id, String title, String content, Integer publishStatus);
    void delete(Long id);
    void publish(Long id);
    void unpublish(Long id);
}
