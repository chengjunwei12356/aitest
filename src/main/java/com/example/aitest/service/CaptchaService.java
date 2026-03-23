package com.example.aitest.service;

import com.example.aitest.dto.CaptchaResponse;

/**
 * 验证码服务接口
 */
public interface CaptchaService {

    /**
     * 生成验证码
     * @return 验证码响应（包含图片和会话 ID）
     */
    CaptchaResponse generateCaptcha();

    /**
     * 验证验证码
     * @param sessionId 会话 ID
     * @param code 验证码
     * @return 是否验证通过
     */
    boolean validateCaptcha(String sessionId, String code);
}
