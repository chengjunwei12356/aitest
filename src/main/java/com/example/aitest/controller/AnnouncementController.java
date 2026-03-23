package com.example.aitest.controller;

import com.example.aitest.common.Result;
import com.example.aitest.entity.Announcement;
import com.example.aitest.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 公告管理控制器
 */
@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    /**
     * 获取公告列表（分页）
     */
    @GetMapping
    public Result<List<Announcement>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Announcement> list = announcementService.findByPage(page, size);
        return Result.success(list);
    }

    /**
     * 获取公告详情
     */
    @GetMapping("/{id}")
    public Result<Announcement> get(@PathVariable Long id) {
        Announcement announcement = announcementService.findById(id);
        return Result.success(announcement);
    }

    /**
     * 创建公告
     */
    @PostMapping
    public Result<Announcement> create(@RequestBody Map<String, Object> body) {
        Announcement announcement = announcementService.create(
                (String) body.get("title"),
                (String) body.get("content"),
                body.get("publishStatus") != null ? Integer.parseInt(body.get("publishStatus").toString()) : 0,
                1L // 当前用户 ID，实际应从 session 获取
        );
        return Result.success(announcement);
    }

    /**
     * 更新公告
     */
    @PutMapping("/{id}")
    public Result<Announcement> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer publishStatus = null;
        if (body.get("publishStatus") != null) {
            publishStatus = Integer.parseInt(body.get("publishStatus").toString());
        }
        Announcement announcement = announcementService.update(
                id,
                (String) body.get("title"),
                (String) body.get("content"),
                publishStatus
        );
        return Result.success(announcement);
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return Result.success();
    }

    /**
     * 发布公告
     */
    @PutMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        announcementService.publish(id);
        return Result.success();
    }

    /**
     * 下架公告
     */
    @PutMapping("/{id}/unpublish")
    public Result<Void> unpublish(@PathVariable Long id) {
        announcementService.unpublish(id);
        return Result.success();
    }
}
