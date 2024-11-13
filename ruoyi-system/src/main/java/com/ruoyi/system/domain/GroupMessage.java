package com.ruoyi.system.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 群消息
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GroupMessage {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 群id
     */
    private Long groupId;

    /**
     * 发送用户id
     */
    private Long sendId;

    /**
     * 发送用户昵称
     */
    private String sendNickName;

    /**
     * @用户列表
     */
    private String atUserIds;

    /**
     * 发送内容
     */
    private String content;

    /**
     * 消息类型 0:文字 1:图片 2:文件
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
