package com.example.aitest.mapper;

import com.example.aitest.entity.Captcha;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 验证码 Mapper 接口
 */
@Mapper
public interface CaptchaMapper {

    /**
     * 插入验证码
     * @param captcha 验证码信息
     * @return 影响行数
     */
    int insert(Captcha captcha);

    /**
     * 根据会话 ID 查询未过期的验证码
     * @param sessionId 会话 ID
     * @param now 当前时间
     * @return 验证码信息
     */
    Captcha findValidBySessionId(@Param("sessionId") String sessionId, @Param("now") LocalDateTime now);

    /**
     * 标记验证码为已使用
     * @param id 验证码 ID
     * @return 影响行数
     */
    int markAsUsed(@Param("id") Long id);

    /**
     * 删除过期的验证码
     * @param now 当前时间
     * @return 影响行数
     */
    int deleteExpired(@Param("now") LocalDateTime now);
}
