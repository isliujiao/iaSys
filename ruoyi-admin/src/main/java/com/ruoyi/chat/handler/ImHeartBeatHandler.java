package com.ruoyi.chat.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 心跳检测包
 */
@Slf4j
public class ImHeartBeatHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent) {
            //将  evt 向下转型 IdleStateEvent
            IdleStateEvent event = (IdleStateEvent) evt;
            String eventType = null;
            switch (event.state()) {
                case READER_IDLE:
                    eventType = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;
                default:
                    break;
            }
            //这里已经可以知道浏览器所处的空闲是何种空闲，可以执行对应的处理逻辑了
//            System.out.println(ctx.channel().remoteAddress() + "--超时时间--" + eventType);
//            System.out.println("服务器做相应处理..");
            //如果发生空闲，我们关闭通道
            // ctx.channel().close();
        }
    }
}