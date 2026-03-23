package com.example.aitest.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 验证码生成工具类
 */
public class CaptchaUtil {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 45;
    private static final int CODE_LENGTH = 4;
    // 只使用数字，更容易识别
    private static final String CODE_CHARS = "23456789";

    private static final Random random = new Random();

    /**
     * 生成验证码图片
     * @return BufferedImage 和 验证码文本
     */
    public static CaptchaImage generateCaptcha() {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 启用抗锯齿，让文字更清晰
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 设置字体 - 使用更大的字号
        Font font = new Font("Arial", Font.BOLD, 28);
        g2d.setFont(font);

        // 填充背景色（浅灰色，减少眼睛疲劳）
        g2d.setColor(new Color(245, 245, 245));
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // 绘制少量干扰线（使用非常浅的颜色，不影响识别）
        for (int i = 0; i < 2; i++) {
            g2d.setColor(new Color(220, 220, 220));
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g2d.drawLine(x1, y1, x2, y2);
        }

        // 生成验证码文本
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            String ch = String.valueOf(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
            code.append(ch);

            // 绘制字符 - 使用深色，保证对比度
            g2d.setColor(new Color(50, 50, 50));
            // 计算字符位置，保证均匀分布
            int charWidth = WIDTH / CODE_LENGTH;
            int x = charWidth * i + 15;
            int y = HEIGHT / 2 + 10;

            // 绘制字符，不旋转
            g2d.drawString(ch, x, y);
        }

        // 添加边框
        g2d.setColor(new Color(200, 200, 200));
        g2d.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);

        g2d.dispose();

        return new CaptchaImage(image, code.toString());
    }

    /**
     * 验证码图片封装类
     */
    public static class CaptchaImage {
        private final BufferedImage image;
        private final String code;

        public CaptchaImage(BufferedImage image, String code) {
            this.image = image;
            this.code = code;
        }

        public BufferedImage getImage() {
            return image;
        }

        public String getCode() {
            return code;
        }
    }
}
