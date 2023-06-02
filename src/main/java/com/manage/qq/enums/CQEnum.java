package com.manage.qq.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CQEnum {
    IMAGE("[CQ:image,file=%s]"),
    ;
    private String sendFormat;
}
