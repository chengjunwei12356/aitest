package com.example.aitest.service.impl;

import com.example.aitest.config.BusinessException;
import com.example.aitest.entity.Announcement;
import com.example.aitest.mapper.AnnouncementMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 公告服务实现测试
 */
class AnnouncementServiceImplTest {

    @Mock
    private AnnouncementMapper announcementMapper;

    @InjectMocks
    private AnnouncementServiceImpl announcementService;

    private Announcement testAnnouncement;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testAnnouncement = Announcement.builder()
                .id(1L)
                .title("测试公告")
                .content("测试内容")
                .publishStatus(1)
                .createdBy(1L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("根据 ID 查询公告 - 成功")
    void findById_Success() {
        when(announcementMapper.findById(1L)).thenReturn(testAnnouncement);

        Announcement result = announcementService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试公告", result.getTitle());
        verify(announcementMapper, times(1)).findById(1L);
    }

    @Test
    @DisplayName("根据 ID 查询公告 - 不存在")
    void findById_NotFound() {
        when(announcementMapper.findById(999L)).thenReturn(null);

        Announcement result = announcementService.findById(999L);

        assertNull(result);
        verify(announcementMapper, times(1)).findById(999L);
    }

    @Test
    @DisplayName("分页查询公告 - 成功")
    void findByPage_Success() {
        List<Announcement> mockList = Arrays.asList(testAnnouncement);
        when(announcementMapper.findByPage(0, 5)).thenReturn(mockList);

        List<Announcement> result = announcementService.findByPage(1, 5);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(announcementMapper, times(1)).findByPage(0, 5);
    }

    @Test
    @DisplayName("查询已发布公告 - 成功")
    void findPublished_Success() {
        List<Announcement> mockList = Arrays.asList(testAnnouncement);
        when(announcementMapper.findPublished(5)).thenReturn(mockList);

        List<Announcement> result = announcementService.findPublished(5);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(announcementMapper, times(1)).findPublished(5);
    }

    @Test
    @DisplayName("统计公告数量 - 成功")
    void count_Success() {
        when(announcementMapper.count()).thenReturn(10);

        int result = announcementService.count();

        assertEquals(10, result);
        verify(announcementMapper, times(1)).count();
    }

    @Test
    @DisplayName("创建公告 - 成功")
    void create_Success() {
        when(announcementMapper.insert(any(Announcement.class))).thenReturn(1);

        Announcement result = announcementService.create(
                "新公告", "新内容", 0, 1L);

        assertNotNull(result);
        assertEquals("新公告", result.getTitle());
        assertEquals("新内容", result.getContent());
        assertEquals(0, result.getPublishStatus());
        verify(announcementMapper, times(1)).insert(any(Announcement.class));
    }

    @Test
    @DisplayName("创建公告 - 发布状态")
    void create_WithPublishStatus() {
        ArgumentCaptor<Announcement> captor = ArgumentCaptor.forClass(Announcement.class);
        when(announcementMapper.insert(captor.capture())).thenReturn(1);

        announcementService.create("新公告", "新内容", 1, 1L);

        Announcement captured = captor.getValue();
        assertEquals(1, captured.getPublishStatus());
        assertNotNull(captured.getPublishTime());
    }

    @Test
    @DisplayName("更新公告 - 成功")
    void update_Success() {
        when(announcementMapper.findById(1L)).thenReturn(testAnnouncement);
        when(announcementMapper.update(any(Announcement.class))).thenReturn(1);

        Announcement result = announcementService.update(1L, "新标题", "新内容", 1);

        assertNotNull(result);
        verify(announcementMapper, times(1)).findById(1L);
        verify(announcementMapper, times(1)).update(any(Announcement.class));
    }

    @Test
    @DisplayName("更新公告 - 不存在")
    void update_NotFound() {
        when(announcementMapper.findById(999L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            announcementService.update(999L, "标题", "内容", 1);
        });

        assertEquals(4001, exception.getCode());
        assertEquals("公告不存在", exception.getMessage());
    }

    @Test
    @DisplayName("删除公告 - 成功")
    void delete_Success() {
        when(announcementMapper.findById(1L)).thenReturn(testAnnouncement);
        when(announcementMapper.delete(1L)).thenReturn(1);

        assertDoesNotThrow(() -> announcementService.delete(1L));

        verify(announcementMapper, times(1)).delete(1L);
    }

    @Test
    @DisplayName("删除公告 - 不存在")
    void delete_NotFound() {
        when(announcementMapper.findById(999L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            announcementService.delete(999L);
        });

        assertEquals(4001, exception.getCode());
        assertEquals("公告不存在", exception.getMessage());
    }

    @Test
    @DisplayName("发布公告 - 成功")
    void publish_Success() {
        when(announcementMapper.findById(1L)).thenReturn(testAnnouncement);
        when(announcementMapper.updatePublishStatus(eq(1L), eq(1), any(LocalDateTime.class))).thenReturn(1);

        assertDoesNotThrow(() -> announcementService.publish(1L));

        verify(announcementMapper, times(1)).updatePublishStatus(eq(1L), eq(1), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("发布公告 - 不存在")
    void publish_NotFound() {
        when(announcementMapper.findById(999L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            announcementService.publish(999L);
        });

        assertEquals(4001, exception.getCode());
        assertEquals("公告不存在", exception.getMessage());
    }

    @Test
    @DisplayName("下架公告 - 成功")
    void unpublish_Success() {
        when(announcementMapper.findById(1L)).thenReturn(testAnnouncement);
        when(announcementMapper.updatePublishStatus(1L, 2, null)).thenReturn(1);

        assertDoesNotThrow(() -> announcementService.unpublish(1L));

        verify(announcementMapper, times(1)).updatePublishStatus(1L, 2, null);
    }

    @Test
    @DisplayName("下架公告 - 不存在")
    void unpublish_NotFound() {
        when(announcementMapper.findById(999L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            announcementService.unpublish(999L);
        });

        assertEquals(4001, exception.getCode());
        assertEquals("公告不存在", exception.getMessage());
    }
}
