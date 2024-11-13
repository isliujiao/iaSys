package com.ruoyi.common.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回消息体
 *
 * @author liujiao
 * @date 2024/1/1 16:11
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ChatMsgVO {

    /**
     * 消息类型 0-系统消息、1-普通消息、2-gpt消息
     */
    private Integer type;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 接收目标
     */
    private String target;

    /**
     * 内容
     */
    private String content;

    /**
     * 发送时间
     */
    private String sendTime;

    /**
     * 发送的消息
     */
    private String sendMsg;

    /**
     * 房间ID
     */
    private Long roomId;

    /**
     * 发送人ID
     */
    private Long fromUid;
}
