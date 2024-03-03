package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.Date;

/**
 * 私聊消息返回
 */
@Data
public class PrivateMessageVO {

//    @ApiModelProperty(value = " 消息id")
    private Long id;

//    @ApiModelProperty(value = " 发送者id")
    private Long sendId;

//    @ApiModelProperty(value = " 接收者id")
    private Long recvId;

//    @ApiModelProperty(value = " 发送内容")
    private String content;

//    @ApiModelProperty(value = "消息内容类型 IMCmdType")
    private Integer type;

//    @ApiModelProperty(value = " 状态")
    private Integer status;

//    @ApiModelProperty(value = " 发送时间")
    private Date sendTime;
}
