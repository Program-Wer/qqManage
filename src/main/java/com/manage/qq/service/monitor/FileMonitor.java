package com.manage.qq.service.monitor;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileMonitor {
    private final String dir;
    private final String fileName;
    private final MyTailerListener myTailerListener;
    private final Map<String, Tailer> fileTailerMap = new ConcurrentHashMap<>();

    private ExecutorService executor = Executors.newFixedThreadPool(100);

    public FileMonitor(String dir, String fileName, MyTailerListener myTailerListener) {
        this.dir = dir;
        this.fileName = fileName;
        this.myTailerListener = myTailerListener;
    }

    public void start() {
        init();
        FileAlterationObserver observer = new FileAlterationObserver(new File(dir));
        observer.addListener(new FileAlterationListener() {
            @Override
            public void onStart(FileAlterationObserver observer) {
//                System.out.println("开始监听目录：" + observer.getDirectory());
            }

            @Override
            public void onDirectoryCreate(File directory) {
                System.out.println("创建目录：" + directory.getAbsolutePath());
            }

            @Override
            public void onDirectoryChange(File directory) {
                System.out.println("修改目录：" + directory.getAbsolutePath());
            }

            @Override
            public void onDirectoryDelete(File directory) {
                System.out.println("删除目录：" + directory.getAbsolutePath());
            }

            @Override
            public void onFileCreate(File file) {
                if (file.getName().contains(fileName)) {
                    System.out.println("新增文件：" + file.getAbsolutePath());
                    if (!fileTailerMap.containsKey(file.getAbsolutePath())) {
                        Tailer tailer = new Tailer(file, myTailerListener, 1000, false);
                        executor.submit(tailer);
                        System.out.println("新增文件加入监听：" + file.getAbsolutePath());
                        fileTailerMap.put(file.getAbsolutePath(), tailer);
                    }
                }
            }

            @Override
            public void onFileChange(File file) {
                if (file.getName().contains(fileName)) {
                    if (!fileTailerMap.containsKey(file.getAbsolutePath())) {
                        Tailer tailer = new Tailer(file, myTailerListener, 1000, true);
                        executor.submit(tailer);
                        System.out.println("修改文件加入监听：" + file.getAbsolutePath());
                        fileTailerMap.put(file.getAbsolutePath(), tailer);
                    }
                }
            }

            @Override
            public void onFileDelete(File file) {
                if (file.getName().contains(fileName)) {
                    System.out.println("删除文件：" + file.getAbsolutePath());
                    Tailer tailer = fileTailerMap.get(file.getAbsolutePath());
                    if (tailer != null) {
                        tailer.stop();
                        System.out.println("删除文件移除监听：" + file.getAbsolutePath());
                        fileTailerMap.remove(file.getAbsolutePath());
                    }
                }
            }

            @Override
            public void onStop(FileAlterationObserver observer) {
//                System.out.println("停止监听目录：" + observer.getDirectory());
            }
        });
        FileAlterationMonitor monitor = new FileAlterationMonitor(1000);
        monitor.addObserver(observer);
        try {
            monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        File dir = new File(this.dir);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.getName().contains(fileName)) {
                        continue;
                    }
                    if (!fileTailerMap.containsKey(file.getAbsolutePath())) {
                        Tailer tailer = new Tailer(file, myTailerListener, 1000, true);
                        executor.submit(tailer);
                        System.out.println("初始化，文件加入监听：" + file.getAbsolutePath());
                        fileTailerMap.put(file.getAbsolutePath(), tailer);
                    }
                }
            }
        }
    }
}
