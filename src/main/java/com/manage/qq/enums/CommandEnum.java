package com.manage.qq.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@AllArgsConstructor
public enum CommandEnum {
    COMMAND_HELP(null, null, "帮助", "【帮助】：直接获取帮助"),
    COMMAND_EXECUTE("执行 ", null, null, "【执行 +${命令}】：直接在服务器上执行cmd指令"),
    COMMAND_N2N_CHECK(null, null, "查看n2n", "【查看n2n】：查看n2n的运行情况"),
    COMMAND_N2N_RESTART(null, null, "重启n2n", "【重启n2n】：关闭并重新启动n2n"),
    COMMAND_N2N_STOP(null, null, "关闭n2n", "【关闭n2n】：关闭n2n的运行"),
    COMMAND_SHOOT_SCREEN(null, null, "截屏", "【截屏】：直接在服务器上截图"),
    COMMAND_GPT(" ", "  ", null, "【  +${聊天}】和GPT聊天"),
    COMMAND_LEISHEN_INFO("", "", "查看雷神", "【查看雷神】查看雷神状态"),
    COMMAND_LEISHEN_PAUSE("", "", "暂停雷神", "【暂停雷神】暂停雷神时长"),
    ;
    private String prefix;
    private String prohibitedPrefix;
    private String equalsCommand;
    private String desc;

    public boolean judgeCommand(String content) {
        if (content == null) {
            return false;
        }

        if (prefix != null && !content.startsWith(prefix)) {
            return false;
        }

        if (prohibitedPrefix != null && content.startsWith(prohibitedPrefix)) {
            return false;
        }

        return equalsCommand == null || content.equals(equalsCommand);
    }

    public String handleCommand(String content) {
        if (content == null) {
            return content;
        }

        if (prefix != null) {
            content = StringUtils.substringAfter(content, prefix);
        }

        return content;
    }
}
