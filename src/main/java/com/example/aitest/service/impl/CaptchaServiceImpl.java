package com.example.aitest.service.impl;

import com.example.aitest.dto.CaptchaResponse;
import com.example.aitest.entity.Captcha;
import com.example.aitest.mapper.CaptchaMapper;
import com.example.aitest.service.CaptchaService;
import com.example.aitest.util.CaptchaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

/**
 * 验证码服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private final CaptchaMapper captchaMapper;

    /**
     * 验证码有效期（分钟）
     */
    private static final int CAPTCHA_EXPIRE_MINUTES = 5;

    @Override
    public CaptchaResponse generateCaptcha() {
        // 生成验证码图片
        CaptchaUtil.CaptchaImage captchaImage = CaptchaUtil.generateCaptcha();

        // 生成会话 ID
        String sessionId = UUID.randomUUID().toString().replace("-", "");

        // 保存到数据库
        Captcha captcha = new Captcha();
        captcha.setSessionId(sessionId);
        captcha.setCode(captchaImage.getCode().toLowerCase()); // 验证码转小写存储
        captcha.setExpireTime(LocalDateTime.now().plusMinutes(CAPTCHA_EXPIRE_MINUTES));
        captcha.setUsed(0);
        captchaMapper.insert(captcha);

        // 清理过期的验证码
        captchaMapper.deleteExpired(LocalDateTime.now());

        log.info("生成验证码，sessionId: {}, code: {}", sessionId, captchaImage.getCode());

        // 将图片转为 Base64
        String imageBase64 = imageToBase64(captchaImage.getImage());

        return CaptchaResponse.builder()
                .sessionId(sessionId)
                .image(imageBase64)
                .build();
    }

    @Override
    public boolean validateCaptcha(String sessionId, String code) {
        if (sessionId == null || code == null) {
            return false;
        }

        // 查询验证码（不区分大小写）
        Captcha captcha = captchaMapper.findValidBySessionId(sessionId, LocalDateTime.now());

        if (captcha == null) {
            log.warn("验证码不存在或已过期，sessionId: {}", sessionId);
            return false;
        }

        // 验证验证码（不区分大小写）
        if (!captcha.getCode().equalsIgnoreCase(code)) {
            log.warn("验证码错误，sessionId: {}, input: {}, expected: {}", sessionId, code, captcha.getCode());
            return false;
        }

        // 标记为已使用
        captchaMapper.markAsUsed(captcha.getId());

        log.info("验证码验证通过，sessionId: {}", sessionId);
        return true;
    }

    /**
     * 图片转 Base64
     */
    private String imageToBase64(java.awt.image.BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            log.error("图片转 Base64 失败", e);
            return "";
        }
    }
}
