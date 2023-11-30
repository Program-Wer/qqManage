package com.manage.qq.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonKvEnum {
    LEISHEN_SILENT_TIME(Long.class),
    ;
    private Class<?> tClass;
}
