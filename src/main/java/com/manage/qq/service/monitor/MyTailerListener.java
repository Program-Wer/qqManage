package com.manage.qq.service.monitor;

import lombok.AllArgsConstructor;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.util.function.Consumer;

@AllArgsConstructor
public class MyTailerListener extends TailerListenerAdapter {
    Consumer<String> consumer;

    @Override
    public void handle(String line) {
        try {
            System.out.println("监听新行：" + line);
            consumer.accept(line);
        } catch (Throwable e) {
            System.out.println("处理时出错：");
            e.printStackTrace();
        }
    }
}
