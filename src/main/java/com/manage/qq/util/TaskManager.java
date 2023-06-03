package com.manage.qq.util;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TaskManager {
    public static void main(String[] args) {
        try {
            Process process = Runtime.getRuntime().exec("tasklist");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void killProcess(String processName) {
        try {
            Process process = Runtime.getRuntime().exec("taskkill /F /IM " + processName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSystemInfo() {
        try {
            Process process = Runtime.getRuntime().exec("systeminfo");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
