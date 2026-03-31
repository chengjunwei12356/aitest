package com.example.aitest.service.impl;

import com.example.aitest.config.BusinessException;
import com.example.aitest.common.ResultCode;
import com.example.aitest.entity.Announcement;
import com.example.aitest.mapper.AnnouncementMapper;
import com.example.aitest.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    @Override
    public Announcement findById(Long id) {
        return announcementMapper.findById(id);
    }

    @Override
    public List<Announcement> findByPage(int page, int size) {
        return announcementMapper.findByPage((page - 1) * size, size);
    }

    @Override
    public List<Announcement> findPublished(int limit) {
        return announcementMapper.findPublished(limit);
    }

    @Override
    public int count() {
        return announcementMapper.count();
    }

    @Override
    @Transactional
    public Announcement create(String title, String content, Integer publishStatus, Long createdBy) {
        Announcement announcement = Announcement.builder()
                .title(title)
                .content(content)
                .publishStatus(publishStatus != null ? publishStatus : 0)
                .createdBy(createdBy)
                .build();
        if (publishStatus != null && publishStatus == 1) {
            announcement.setPublishTime(LocalDateTime.now());
        }
        announcementMapper.insert(announcement);
        log.info("创建公告成功：{}", title);
        return announcement;
    }

    @Override
    @Transactional
    public Announcement update(Long id, String title, String content, Integer publishStatus) {
        Announcement existing = announcementMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.ANNOUNCEMENT_NOT_FOUND);
        }
        Announcement announcement = Announcement.builder()
                .id(id)
                .title(title)
                .content(content)
                .publishStatus(publishStatus)
                .build();
        announcementMapper.update(announcement);
        log.info("更新公告成功：{}", id);
        return announcement;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Announcement existing = announcementMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.ANNOUNCEMENT_NOT_FOUND);
        }
        announcementMapper.delete(id);
        log.info("删除公告成功：{}", id);
    }

    @Override
    @Transactional
    public void publish(Long id) {
        Announcement existing = announcementMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.ANNOUNCEMENT_NOT_FOUND);
        }
        announcementMapper.updatePublishStatus(id, 1, LocalDateTime.now());
        log.info("发布公告成功：{}", id);
    }

    @Override
    @Transactional
    public void unpublish(Long id) {
        Announcement existing = announcementMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.ANNOUNCEMENT_NOT_FOUND);
        }
        announcementMapper.updatePublishStatus(id, 2, null);
        log.info("下架公告成功：{}", id);
    }
}
