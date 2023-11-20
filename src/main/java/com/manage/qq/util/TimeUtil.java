package com.manage.qq.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static String formatTime(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        return sdf.format(date);
    }

    public static void main(String[] args) {
        long timestamp = System.currentTimeMillis();
        String formattedTimestamp = formatTime(timestamp);
        System.out.println(formattedTimestamp);
    }
}