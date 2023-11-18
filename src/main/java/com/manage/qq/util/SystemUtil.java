package com.manage.qq.util;

import com.manage.qq.model.SystemTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class SystemUtil {
    static Charset cmdCharset = Charset.forName("GBK");
    public static void main(String[] args) {
        System.out.println(runAndReturn("tasklist"));
    }


//        killProcess("edge_v3_bugxia_n2n.exe");
////        logger.info(runBat("D:\\software\\EasyN2N\\n2n_client\\x64\\connect.bat"));
////        log.info(run("cmd /k cd D:\\software\\EasyN2N\\n2n_client\\x64 && connect.bat"));
//        for (String s : "========================= ======== ================ =========== ============".split(" ")) {
//            log.info(String.valueOf(s.length()));
//        }
//        log.info(String.valueOf(getTaskList()));

    public static void run(String task) {
        try {
            CommandLine commandLine = CommandLine.parse(task);
            DefaultExecutor executor = new DefaultExecutor();
            executor.setStreamHandler(new PumpStreamHandler(System.out, System.err));
            executor.execute(commandLine);
        } catch (Exception e) {
            log.error("执行命令时出错： task:{}", JsonUtil.toJson(task), e);
        }
    }

    public static String runAndReturn(String task) {
        try {
            CommandLine commandLine = CommandLine.parse(task);
            DefaultExecutor executor = new DefaultExecutor();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
            executor.setStreamHandler(streamHandler);
            executor.execute(commandLine);

            return outputStream.toString(cmdCharset.name());
        } catch (Exception e) {
            log.error("执行命令时出错： task:{}", JsonUtil.toJson(task), e);
        }
        return null;
    }

    public static boolean runNoRead(String task) {
        try {
            CommandLine commandLine = CommandLine.parse(task);
            DefaultExecutor executor = new DefaultExecutor();
            executor.execute(commandLine);
            return true;
        } catch (Exception e) {
            log.error("执行命令时出错： task:{}", JsonUtil.toJson(task), e);
        }
        return false;
    }

    public static boolean runBat(String batPath) {
        String dir = FileUtil.getDir(batPath);
        String fileName = FileUtil.getFileName(batPath);
        if (dir == null || fileName == null) {
            return false;
        }
        return runNoRead(String.format("cmd /k cd %s && %s", dir, fileName));
    }

    public static List<SystemTask> getTaskList() {
        try {
            CommandLine commandLine = CommandLine.parse("tasklist");
            DefaultExecutor executor = new DefaultExecutor();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
            executor.setStreamHandler(streamHandler);
            executor.execute(commandLine);

            String taskStr = outputStream.toString(Charset.defaultCharset().name());
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
            log.error("获取命令时出现异常", e);
        }
        return new ArrayList<>();
    }

    public static boolean killProcess(String processName) {
        try {
            CommandLine commandLine = CommandLine.parse("taskkill /F /IM " + processName);
            DefaultExecutor executor = new DefaultExecutor();
            executor.execute(commandLine);
            return true;
        } catch (Exception e) {
            log.error("执行命令时出错：", e);
        }
        return false;
    }
}