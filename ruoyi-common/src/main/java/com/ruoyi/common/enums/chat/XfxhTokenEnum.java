package com.ruoyi.common.enums.chat;

import lombok.Getter;

/**
 * @Author
 * @Date 2024/1/11 17:14
 * @Description
 */
@Getter
public enum XfxhTokenEnum {

    /**
       /**
     * 获取token
     */
    GET_TOKEN_STATUS(0),
    /**
     * 归还令牌
     */
    BACK_TOKEN_STATUS(1);

    private final int code;

    XfxhTokenEnum(int code){
        this.code = code;
    }
}
