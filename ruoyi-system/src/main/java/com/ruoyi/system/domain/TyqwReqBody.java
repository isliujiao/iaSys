package com.ruoyi.system.domain;

/**
 * 百炼-通义千问 消息请求体
 * @author liujiao
 * @date 2024/12/6 16:07
 */
public class TyqwReqBody {

    String model;
    TyqwMsg[] messages;

    public TyqwReqBody(String model, TyqwMsg[] messages) {
        this.model = model;
        this.messages = messages;
    }

}
