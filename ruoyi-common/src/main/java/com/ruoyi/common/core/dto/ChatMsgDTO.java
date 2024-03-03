package com.ruoyi.common.core.dto;

import com.fasterxml.jackson.databind.ObjectReader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 指令模型，用于消息的传输
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatMsgDTO {

    /**
     * 用户唯一ID
     */
    private String userId;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 消息类型
     */
    private Integer msgType;

    /**
     * 目标接收对象（接收人是谁，把消息发给谁）
     */
    private String target;

    /**
     * 内容
     */
    private String content;

    /**
     * 携带的Token信息
     */
    private String token;
}
