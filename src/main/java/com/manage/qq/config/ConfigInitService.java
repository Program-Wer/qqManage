package com.manage.qq.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@Component
public class ConfigInitService {
    @Autowired
    private ResourceLoader resourceLoader;
    @PostConstruct
    public void init() {
//        copyResourcesToCurrentDirectory();
    }

    /**
     * 将sources下的配置文件迁移到当前目录下
     */
    public void copyResourcesToCurrentDirectory() {
        String appPath = System.getProperty("user.dir");
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath*:/*");
            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                File file = new File(appPath + File.separator + fileName);
                if (!file.exists()) {
                    InputStream inputStream = resource.getInputStream();
                    OutputStream outputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                    outputStream.close();
                    inputStream.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
