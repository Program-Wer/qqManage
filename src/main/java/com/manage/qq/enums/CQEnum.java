package com.manage.qq.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CQEnum {
    IMAGE("[CQ:image,file=%s]"),
    AT("[CQ:at,qq=%s]"),
    ;
    private String sendFormat;
}
