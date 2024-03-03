package com.ruoyi.system.service;

import com.ruoyi.common.core.dto.XfxhMsgDTO;
import com.ruoyi.common.core.dto.XfxhReqDTO;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import java.util.List;

/**
 * @Author
 * @Date 2024/1/11 17:06
 * @Description
 */

public interface XfXhStreamServer {

    /**
     * 操作令牌
     *
     * @param status 0-获取令牌 1-归还令牌
     * @return 是否操作成功
     */
    public boolean operateToken(int status);

    /**
     * 发送消息
     *
     * @param uid     每个用户的id，用于区分不同用户
     * @param msgList 发送给大模型的消息，可以包含上下文内容
     * @return 获取websocket连接，以便于我们在获取完整大模型回复后手动关闭连接
     */
    public WebSocket sendMsg(String uid, List<XfxhMsgDTO> msgList, WebSocketListener listener);

    /**
     * 生成鉴权方法，具体实现不用关心，这是讯飞官方定义的鉴权方式
     *
     * @return 鉴权访问大模型的路径
     */
    public String getAuthUrl();

    /**
     * 获取请求参数
     *
     * @param uid     每个用户的id，用于区分不同用户
     * @param msgList 发送给大模型的消息，可以包含上下文内容
     * @return 请求DTO，该 DTO 转 json 字符串后生成的格式参考 resources/demo-json/request.json
     */
    public XfxhReqDTO getRequestParam(String uid, List<XfxhMsgDTO> msgList);


}
