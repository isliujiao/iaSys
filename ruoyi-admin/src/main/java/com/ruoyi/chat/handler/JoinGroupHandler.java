package com.ruoyi.chat.handler;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.chat.ChatNettyServer;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.dto.ChatMsgDTO;
import com.ruoyi.common.core.dto.ChatMsgVO;
import com.ruoyi.common.enums.chat.ChatMsgType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.web.service.TokenService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * 加入群聊处理器
 *
 * @author liujiao
 * @date 2024/1/1 17:28
 */
@Configuration
@Slf4j
public class JoinGroupHandler {

    @Autowired
    private ChatNettyServer nettyServer;

    @Autowired
    private TokenService tokenService;

    public void execute(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        ChatMsgDTO chatMsg = JSON.parseObject(frame.text(), ChatMsgDTO.class);
        LoginUser loginUser = tokenService.getLoginUser(chatMsg.getToken());
        // 如果该用户已存在，就不能重复上线，并断掉连接
//        if (nettyServer.USERS.containsKey(loginUser.getUserId().toString())) {
//            chatMsgVO.setType(ChatResType.WARNING.getCode());
//            chatMsgVO.setContent("该用户已进入聊天室，请退出后重试");
//            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(chatMsgVO)));
//            ctx.channel().disconnect();
//            return;
//        }

        // 加到映射表里
        nettyServer.USERS.put(loginUser.getUserId().toString(), ctx.channel());
        nettyServer.GROUP.add(ctx.channel());
        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(
                ChatMsgVO.builder()
                        .type(ChatMsgType.SYSTEM.getCode())
                        .sendTime(DateUtils.getTime())
                        .content("成功加入系统默认群聊！")
                        .build()
        )));
        log.info("【" + ctx.name() + "---" + ctx.channel() + "】加入系统默认群聊");
    }
}
