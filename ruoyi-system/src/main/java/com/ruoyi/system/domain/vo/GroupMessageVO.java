package com.ruoyi.system.domain.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 消息返回对象
 */
@Data
public class GroupMessageVO {

    /**
     * 消息id
     */
    private Long id;

    /**
     * 群聊id
     */
    private Long groupId;

    /**
     * 发送者id
     */
    private Long sendId;

    /**
     * 发送者昵称
     */
    private String sendNickName;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息内容类型 具体枚举值由应用层定义
     */
    private Integer type;

    /**
     * 被 @ 用户列表
     */
    private List<Long> atUserIds;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 发送时间
     */
    private Date sendTime;
}
