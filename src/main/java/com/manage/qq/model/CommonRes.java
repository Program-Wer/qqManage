package com.manage.qq.model;

import lombok.Data;

@Data
public class CommonRes<T> {
    private T data;
    private String message;
    private int retcode;
    private String status;

    public boolean isSuccess() {
        return retcode == 0;
    }
}
