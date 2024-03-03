package com.ruoyi.common.enums.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 聊天返回相应枚举
 * @author liujiao
 * @date 2024/1/7 15:56
 */
@Getter
@AllArgsConstructor
public enum ChatResType {

    /**
     * 系统提示消息
     */
    PROMPT(1001),
    /**
     * 系统警告消息
     */
    WARNING(1002),
    /**
     * 普通聊天消息
     */
    CHAT_MSG(2001),
    /**
     * gpt消息
     */
    ASK_GPT(3001),

    ERROR(-1),

    ;
    private final Integer code;//根据code码,对应不同的类型

    //写一个match方法，根据怎么获取到我们的，去匹配一下
    public static ChatResType match(Integer code) {//返回这个枚举类型
        //到这边就不去加那些缓存了，直接用一个for循环来处理
        for (ChatResType value : ChatResType.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return ERROR;//如果找不到匹配的枚举，就返回ERROR。
    }

}
