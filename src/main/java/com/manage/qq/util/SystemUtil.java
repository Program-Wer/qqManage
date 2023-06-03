package com.manage.qq.util;

import com.manage.qq.model.SystemTask;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SystemUtil {
    public static void main(String[] args) {
        killProcess("edge_v3_bugxia_n2n.exe");
//        System.out.println(runBat("D:\\software\\EasyN2N\\n2n_client\\x64\\connect.bat"));
        System.out.println(run("cmd /k cd D:\\software\\EasyN2N\\n2n_client\\x64 && connect.bat"));
        for (String s : "========================= ======== ================ =========== ============".split(" ")) {
            System.out.println(s.length());
        }
        System.out.println(getTaskList());
    }

    public static Pair<Boolean, String> run(String task) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean runSuccess = true;
        try {
            Process process = Runtime.getRuntime().exec(task);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            reader.close();
            process.destroy();
        } catch (Throwable e) {
            System.out.println("执行命令时出错：");
            e.printStackTrace();
            runSuccess = false;
        }
        return ImmutablePair.of(runSuccess, stringBuilder.toString());
    }

    public static Boolean runNoRead(String task) {
        boolean runSuccess = true;
        try {
            Runtime.getRuntime().exec(task);
        } catch (Throwable e) {
            System.out.println("执行命令时出错：");
            e.printStackTrace();
            runSuccess = false;
        }
        return runSuccess;
    }

    public static Boolean runBat(String batPath) {
        String dir = FileUtil.getDir(batPath);
        String fileName = FileUtil.getFileName(batPath);
        if (dir == null || fileName == null) {
            return false;
        }
        return runNoRead(String.format("cmd /k cd %s && %s", dir, fileName));
    }

    public static List<SystemTask> getTaskList() {
        try {
            String taskStr = run("tasklist").getRight();
            if (StringUtils.isBlank(taskStr)) {
                return new ArrayList<>();
            }
            return Arrays.stream(taskStr.split("\n"))
                    .filter(StringUtils::isNotBlank)
                    .map(str -> {
                        if (str.length() < 40) {
                            return null;
                        }
                        SystemTask systemTask = new SystemTask();
                        String name = StringUtils.strip(str.substring(0, 24));
                        String pid = StringUtils.strip(str.substring(26, 34));
                        systemTask.setPid(pid);
                        systemTask.setName(name);
                        return systemTask;
                    }).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("获取命令时出现异常");
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    public static boolean killProcess(String processName) {
        return run("taskkill /F /IM " + processName).getLeft();
    }
}
