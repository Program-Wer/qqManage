package com.manage.qq.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static final long HOUR_MS = 60 * 60 * 1000;
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_FILE_FORMAT = "yyyyMMdd-HHmmss";

    public static String formatFileTime(long timestamp) {
        return formatTime(timestamp, DATETIME_FILE_FORMAT);
    }

    public static String formatTime(long timestamp, String format) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static void main(String[] args) {
        long timestamp = System.currentTimeMillis();
        String formattedTimestamp = formatFileTime(timestamp);
        System.out.println(formattedTimestamp);
    }
}