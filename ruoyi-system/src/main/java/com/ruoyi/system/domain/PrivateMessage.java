package com.ruoyi.system.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class PrivateMessage {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 发送用户id
     */
    private Long sendId;

    /**
     * 接收用户id
     */
    private Long recvId;

    /**
     * 发送内容
     */
    private String content;

    /**
     * 消息类型 0:文字 1:图片 2:文件 3:语音  10:撤回消息
     */
    private Integer type;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 发送时间
     */
    private Date sendTime;
}
