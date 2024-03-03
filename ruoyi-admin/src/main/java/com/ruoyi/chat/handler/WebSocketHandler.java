package com.ruoyi.chat.handler;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.dto.ChatMsgDTO;
import com.ruoyi.common.core.dto.ChatMsgVO;
import com.ruoyi.common.enums.chat.ChatMsgType;
import com.ruoyi.common.enums.chat.ChatResType;
import com.ruoyi.common.exception.UtilException;
import com.ruoyi.common.utils.StringUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Handler 处理器
 * 泛型<TextWebSocketFrame>，表示是由哪一个对象来承载消息。
 * 因为消息都是文本，所以用TextWebSocketFrame
 */
@ChannelHandler.Sharable
@Component
@Scope("singleton")
@Slf4j
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Autowired
    private ConnectionHandler connectionHandler;

    @Autowired
    private ChatHandler chatHandler;

    @Autowired
    private JoinGroupHandler joinGroupHandler;

    /**
     * 消息读取
     *
     * @param ctx   通道消息内容
     * @param frame 消息长连接框架
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {

//        ctx.writeAndFlush(new TextWebSocketFrame("服务端收到消息--->" + frame));
        log.info("消息内容 -- >{}", frame.text());
        //保存映射关系，在建立链接时，要把状态信息存起来，首先要定义一个模型(或者说指令)，即 Command
        //1、首先要解析frame，我们可以定义一种序列化的格式，比如可以前后端约定好用json的方式来处理消息
        try {
            ChatMsgDTO chatMsg = JSON.parseObject(frame.text(), ChatMsgDTO.class);
            if (StringUtils.isEmpty(chatMsg.getToken())) {
                throw new UtilException("用户无权限");
            }
            switch (ChatMsgType.match(chatMsg.getMsgType())) {
                // 建立私聊连接事件
                case CONNECTION:
                    connectionHandler.execute(ctx, chatMsg);
                    break;
                // 建立群聊连接事件 ---
                case JOIN_GROUP:
                    joinGroupHandler.execute(ctx, frame);
                    break;
                // 聊天事件（私聊、群聊、请求GPT）
                case PRIVATE_CHAT:
                case GROUP_CHAT:
                case ASK_GPT:
                    chatHandler.execute(ctx, frame);
                    break;
                default:
                    //都不支持的话就返回一条错误消息
                    throw new UtilException("暂不支持此消息");
            }
        } catch (Exception e) {
            ChatMsgVO chatMsgVO = new ChatMsgVO();
            chatMsgVO.setType(ChatResType.WARNING.getCode());
            chatMsgVO.setContent(e.getMessage());
            ctx.channel().writeAndFlush(chatMsgVO);
        }
    }
}
