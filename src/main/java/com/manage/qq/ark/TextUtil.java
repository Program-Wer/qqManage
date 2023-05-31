package com.manage.qq.ark;

import org.apache.commons.lang3.StringUtils;

public class TextUtil {

    public static String handleLogLine(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        return "方舟服务器发来捷豹：【" + removeBrackets(removeTime(removeBracketsAndContent(text))) + "】";
    }

    public static String removeBracketsAndContent(String text) {
        String regex = "\\[.+?\\]|<.+?>";
        return text.replaceAll(regex, "");
    }

    public static String removeTime(String text) {
        String regex = "\\d{4}\\.\\d{2}\\.\\d{2}_\\d{2}\\.\\d{2}\\.\\d{2}:";
        return text.replaceAll(regex, "");
    }

    public static String removeBrackets(String text) {
        String regex = "\\(|\\)|（|）";
        return text.replaceAll(regex, "");
    }

    public static void main(String[] args) {
        String text = "（[2023.05.27-17.00.07:387][789]2023.05.27_17.00.07: 部落天堂制造, ID 1375372275: Day 36, 06:23:37: <RichColor Color=\"1, 1, 0, 1\">巨龙 拆除了 '开始'!</>)";
        System.out.println(removeBrackets(removeTime(removeBracketsAndContent(text))));
    }
}
