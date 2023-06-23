package com.manage.qq.model.qq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgSendContext {
    private String groupId;
    private String userId;
    private String msg;
}
