package com.manage.qq.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Random;

public class FileUtil {

    private final static String TMP_DIR = System.getProperty("user.dir") + "/tmp/";
    private final static Random random = new Random();

    public static String genUniqueFileNameContainsPath(String tag, String suffix) {
        return TMP_DIR + genUniqueFileName(tag, suffix);
    }

    public static String genUniqueFileName(String tag, String suffix) {
        return tag + "-" + System.currentTimeMillis() + "-"+ Math.abs(random.nextInt()) + "-" + suffix;
    }

    /**
     * 转换成能识别的本地文件URI
     * @param path
     * @return
     */
    public static String convertUriPath(String path) {
        return new File(path).toURI().toString();
    }

    /**
     * 文字转图片文件
     * @param text
     * @param filePath
     */
    public static void textToImage(String text, String filePath) {
        String[] lines = text.split("\n");

        Font font = new Font("等线", Font.BOLD, 20);
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setFont(font);
        FontMetrics fontMetrics = g2d.getFontMetrics();
        g2d.dispose();

        int textHeight = fontMetrics.getHeight() + 20;
        int lineHeight = textHeight * lines.length;
        int width = Arrays.stream(lines).mapToInt(fontMetrics::stringWidth).max().orElse(100) + 20;
        int height = lineHeight + 20;

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g2d = image.createGraphics();
        g2d.setFont(font);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLACK);
        int y = textHeight;
        for (String line : lines) {
            g2d.drawString(line, 10, y);
            y += textHeight;
        }
        g2d.dispose();

        try {
            File output = new File(filePath);
            if (!output.getParentFile().exists()) {
                output.getParentFile().mkdirs();
            }
            ImageIO.write(image, "png", output);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
