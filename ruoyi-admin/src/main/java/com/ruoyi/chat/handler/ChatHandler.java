package com.ruoyi.chat.handler;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.chat.ChatNettyServer;
import com.ruoyi.common.config.XfxhConfig;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.dto.ChatMsgDTO;
import com.ruoyi.common.core.dto.ChatMsgVO;
import com.ruoyi.common.core.dto.GroupMessageDTO;
import com.ruoyi.common.core.dto.XfxhMsgDTO;
import com.ruoyi.common.enums.chat.ChatMsgType;
import com.ruoyi.common.enums.chat.XfxhTokenEnum;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.event.UserGroupMessageEvent;
import com.ruoyi.system.listener.XfXhWebSocketListener;
import com.ruoyi.system.service.XfXhStreamServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

/**
 * 聊天消息处理器
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ChatHandler {

    @Autowired
    private ChatNettyServer nettyServer;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private XfXhStreamServer xfXhStreamServer;

    @Autowired
    private XfxhConfig xfXhConfig;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void execute(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        try {
            //把消息的格式转成ChatMessage
            ChatMsgDTO chatMsg = JSON.parseObject(frame.text(), ChatMsgDTO.class);
            log.info("接收到聊天消息--->{}", frame.text());
            // todo 目前从redis中获取信息，【改进一：如果没必要前端直接传；改进二：从Security中获取(未通)】
            LoginUser loginUser = tokenService.getLoginUser(chatMsg.getToken());

            if (ChatMsgType.PRIVATE_CHAT.equals(ChatMsgType.match(chatMsg.getMsgType()))) {
                // 发送私聊消息
                privateChat(ctx, chatMsg);
            } else if (ChatMsgType.match(chatMsg.getMsgType()) == ChatMsgType.GROUP_CHAT) {
                // 发送群聊消息
                groupChat(chatMsg, loginUser);
                GroupMessageDTO groupMessageDTO = GroupMessageDTO.builder()
                        .content(chatMsg.getContent())
                        .fromUserId(loginUser.getUserId())
                        .nickName(loginUser.getUser().getNickName())
                        .type(chatMsg.getMsgType())
                        .groupId(0L).build();
                applicationEventPublisher.publishEvent(new UserGroupMessageEvent(this, groupMessageDTO));
            } else if (ChatMsgType.match(chatMsg.getMsgType()) == ChatMsgType.ASK_GPT) {
                // 向GPT发送消息
                askGpt(ctx, chatMsg.getContent(), loginUser);
            } else {
                writeFlushFailUtil(ctx, "不支持该消息类型");
            }
        } catch (Exception e) {
            writeFlushFailUtil(ctx, "系统内部发生异常");
            log.error("消息发送发生错误：", e);
        }
    }

    /**
     * 发送群聊消息
     *
     * @param chatMsg
     * @param loginUser
     */
    private void groupChat(ChatMsgDTO chatMsg, LoginUser loginUser) {
        //群聊消息
        nettyServer.GROUP.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(
                ChatMsgVO.builder()
                        .fromUid(loginUser.getUserId())
                        .type(ChatMsgType.GROUP_CHAT.getCode())
                        .nickName(loginUser.getUser().getNickName())
                        .sendTime(DateUtils.getTime())
                        .avatar(loginUser.getUser().getAvatar())
                        .content(chatMsg.getContent())
                        .build()
        )));
    }

    /**
     * 向xfxh请求消息
     *
     * @param ctx       通道处理程序
     * @param question  消息内容
     * @param loginUser 登录用户
     */
    private void askGpt(ChannelHandlerContext ctx, String question, LoginUser loginUser) {
        log.info("---------开始向xfxh发送消息---------");
        // 如果是无效字符串，则不对大模型进行请求
        if (StringUtils.isBlank(question)) {
            writeFlushFailUtil(ctx, "无效问题，请重新输入");
            return;
        }
        // 获取连接令牌
        if (!xfXhStreamServer.operateToken(XfxhTokenEnum.GET_TOKEN_STATUS.getCode())) {
            writeFlushFailUtil(ctx, "当前大模型连接数过多，请稍后再试");
            return;
        }

        // 创建消息对象
        XfxhMsgDTO msgDTO = XfxhMsgDTO.createUserMsg(question);
        // 创建监听器
        XfXhWebSocketListener listener = new XfXhWebSocketListener();
        // 发送问题给大模型，生成 websocket 连接
        String uuid = UUID.randomUUID().toString().substring(0, 10);
        WebSocket webSocket = xfXhStreamServer.sendMsg(uuid, Collections.singletonList(msgDTO),
                listener);

        if (webSocket == null) {
            // 归还令牌
            xfXhStreamServer.operateToken(XfxhTokenEnum.BACK_TOKEN_STATUS.getCode());
            writeFlushFailUtil(ctx, "建立连接失败，请联系管理员");
            return;
        }
        try {
            int count = 0;
            // 为了避免死循环，设置循环次数来定义超时时长
            int maxCount = xfXhConfig.getMaxResponseTime() * 5;
            while (count <= maxCount) {
                Thread.sleep(200);
                if (listener.isWsCloseFlag()) {
                    break;
                }
                count++;
            }
            if (count > maxCount) {
                writeFlushFailUtil(ctx, "大模型响应超时，请联系管理员");
                log.error("大模型响应超时，请联系管理员");
                return;
            }

            // 响应大模型的答案
            log.info("-------xfxh响应：{}-----------", listener.getAnswer().toString());
            ChatMsgVO chatMsgVO = new ChatMsgVO();
            chatMsgVO.setType(ChatMsgType.ASK_GPT.getCode());
            chatMsgVO.setContent(listener.getAnswer().toString());
            chatMsgVO.setNickName(loginUser.getUser().getNickName());
            chatMsgVO.setAvatar(loginUser.getUser().getAvatar());
            chatMsgVO.setSendTime(DateUtils.getTime());
            chatMsgVO.setSendMsg(question);
            nettyServer.GROUP.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(chatMsgVO)));
        } catch (InterruptedException e) {
            writeFlushFailUtil(ctx, "系统内部错误，请联系管理员");
            log.error("错误：" + e.getMessage());
        } finally {
            // 关闭 websocket 连接
            webSocket.close(1000, "");
            // 归还令牌
            xfXhStreamServer.operateToken(XfxhTokenEnum.BACK_TOKEN_STATUS.getCode());
        }
    }

    /**
     * 发送私聊消息
     *
     * @param ctx
     * @param chatMsg
     */
    private void privateChat(ChannelHandlerContext ctx, ChatMsgDTO chatMsg) {
        if (StringUtil.isNullOrEmpty(chatMsg.getTarget())) {
            writeFlushFailUtil(ctx, "消息发送失败，发送消息前请指定接收对象");
            return;
        }
        //取出目标对象的Channel
        Channel channel = nettyServer.USERS.get(chatMsg.getTarget());
        // 判断通道是否存在、是否存活
        if (channel == null || !channel.isActive()) {
            writeFlushFailUtil(ctx, "消息发送失败，" + chatMsg.getTarget() + "不在线");
        } else {
            //如果在线，就直接发送消息
            ChatMsgVO chatMsgVO = new ChatMsgVO();
            chatMsgVO.setType(ChatMsgType.PRIVATE_CHAT.getCode());
            chatMsgVO.setNickName(chatMsg.getNickName() + "系统消息" + LocalDateTime.now());
            chatMsgVO.setContent(chatMsg.getContent());
            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(chatMsgVO)));
        }
    }

    /**
     * 发送系统警告消息
     *
     * @param ctx 连接通道
     * @param str 消息内容
     */
    private void writeFlushFailUtil(ChannelHandlerContext ctx, String str) {
        ChatMsgVO chatMsgVO = new ChatMsgVO();
        chatMsgVO.setContent(str);
        chatMsgVO.setType(ChatMsgType.ERROR.getCode());
        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(chatMsgVO)));
    }
}
