package com.ruoyi.chat.handler;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.chat.ChatNettyServer;
import com.ruoyi.common.core.dto.ChatMsgDTO;
import com.ruoyi.common.core.dto.ChatMsgVO;
import com.ruoyi.common.enums.chat.ChatMsgType;
import com.ruoyi.common.utils.SecurityUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 连接事件处理器
 */
@Component
public class ConnectionHandler {

    @Autowired
    ChatNettyServer nettyServer;

    public void execute(ChannelHandlerContext ctx, ChatMsgDTO chatMsg) {
        ChatMsgVO chatMsgVO = new ChatMsgVO();
        // 获取当前用户，并将其与服务器建立连接
        Long userId = SecurityUtils.getUserId();

        // 如果该用户已存在，就不能重复上线，并断掉连接
        if (nettyServer.USERS.containsKey(userId + chatMsg.getNickName())) {
            chatMsgVO.setContent("该用户已上线，请退出后再试");
            chatMsgVO.setType(ChatMsgType.ERROR.getCode());
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(chatMsgVO)));
            ctx.channel().disconnect();
            return;
        }
        // 加到映射表里
        nettyServer.USERS.put(userId + chatMsg.getNickName(), ctx.channel());
        // 告诉前端，恭喜你加入成功
        chatMsgVO.setContent("与服务端连接建立成功");
        chatMsgVO.setType(ChatMsgType.ERROR.getCode());
        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(chatMsgVO)));
        //返回当前在线用户
//        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(nettyServer.USERS.keySet())));

    }
}
