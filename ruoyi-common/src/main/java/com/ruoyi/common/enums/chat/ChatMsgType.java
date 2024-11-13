package com.ruoyi.common.enums.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 接收消息类型
 */
@Getter
@AllArgsConstructor
public enum ChatMsgType {

    /**
     * 系统消息
     */
    SYSTEM(10000),

    /**
     * 建立私聊连接事件
     */
    CONNECTION(10001),
    /**
     * 建立群聊连接事件
     */
    JOIN_GROUP(10002),
    /**
     * 私人聊天消息
     */
    PRIVATE_CHAT(20001),
    /**
     * 群组聊天消息
     */
    GROUP_CHAT(20002),
    /**
     * 向GPT提问
     */
    ASK_GPT(30001),
    /**
     * 断开连接
     */
    DISCONNECTION(90001),

    ERROR(-1),

    ;
    private final Integer code;//根据code码,对应不同的类型

    //写一个match方法，根据怎么获取到我们的，去匹配一下
    public static ChatMsgType match(Integer code) {//返回这个枚举类型
        //到这边就不去加那些缓存了，直接用一个for循环来处理
        for (ChatMsgType value : ChatMsgType.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return ERROR;//如果找不到匹配的枚举，就返回ERROR。
    }

}
