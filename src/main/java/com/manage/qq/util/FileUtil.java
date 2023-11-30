package com.manage.qq.util;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

import static com.manage.qq.constant.SystemConstant.TMP_DIR;

public class FileUtil {

    private final static Random random = new Random();

    public static String genUniqueFileNameContainsPath(String tag, String suffix) {
        return TMP_DIR + genUniqueFileName(tag, suffix);
    }

    public static String genUniqueFileName(String tag, String suffix) {
        return tag + "-" + TimeUtil.formatFileTime(System.currentTimeMillis()) + "-"+ RandomUtils.nextInt(0, Integer.MAX_VALUE) + suffix;
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

    public static String getDir(String path) {
        try {
            return new File(path).getParent();
        } catch (Exception e) {
            System.out.println("获取目录出错：");
            e.printStackTrace();
        }
        return null;
    }

    public static String getFileName(String path) {
        try {
            return new File(path).getName();
        } catch (Exception e) {
            System.out.println("获取目录出错：");
            e.printStackTrace();
        }
        return null;
    }

    public static String readFileAsString(String filePath) {
        String content = "";
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                System.out.println("文件不存在：" + filePath);
                return "";
            }
            byte[] bytes = Files.readAllBytes(path);
            content = new String(bytes);
        } catch (IOException e) {
            System.out.println("读取文件失败");
            e.printStackTrace();
        }
        return content;
    }

    public static boolean writeFile(String content, String filePath) {
        if (StringUtils.isEmpty(content)) {
            return true;
        }
        try {
            Path path = Paths.get(filePath);
            Path parentDir = path.getParent();
            if (!Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }
            Files.write(path, content.getBytes());
            return true;
        } catch (IOException e) {
            System.out.println("写入文件失败");
            e.printStackTrace();
            return false;
        }
    }
}
