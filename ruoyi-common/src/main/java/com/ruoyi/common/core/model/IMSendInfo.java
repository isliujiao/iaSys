package com.ruoyi.common.core.model;

import lombok.Data;

@Data
public class IMSendInfo<T> {

    /**
     * 命令
     */
    private Integer cmd;

    /**
     * 推送消息体
     */
    private T data;

}
