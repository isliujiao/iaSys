package com.ruoyi.chat;

import com.ruoyi.chat.handler.ImHeartBeatHandler;
import com.ruoyi.chat.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ChatNettyServer {

    @Autowired
    private WebSocketHandler webSocketHandler;

    // 把映射关系通过一个CoCurrentHashmap保存下来
    public final Map<String, Channel> USERS = new ConcurrentHashMap<>(1024);//KEY是昵称，value是channel

    // 保存群聊组
    public final ChannelGroup GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public void start() throws Exception {
        // TODO 【序号】 Netty启动过程
        // 【1】bossGroup用于接收连接，workerGroup用于具体的读写等处理
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //【2】创建服务端启动引导/辅助类：ServerBootstrap
            ServerBootstrap sb = new ServerBootstrap();
            sb.option(ChannelOption.SO_BACKLOG, 1024);
            //【3】给引导类配置两大线程组,确定了线程模型
            sb.group(group, bossGroup) // 绑定线程池
                    //【4】指定IO模型、 绑定端口
                    .channel(NioServerSocketChannel.class) // 指定使用的channel
                    .localAddress(9044)// 绑定监听端口
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 绑定客户端连接时候触发操作
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //【5】自定义处理逻辑
                            ch.pipeline().addLast(new IdleStateHandler(5, 5, 0, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new ImHeartBeatHandler());
                            //websocket协议本身是基于http协议的，所以这边也要使用http解编码器
                            ch.pipeline().addLast(new HttpServerCodec());
                            //以块的方式来写的处理器
                            ch.pipeline().addLast(new ChunkedWriteHandler());
                            ch.pipeline().addLast(new HttpObjectAggregator(8192));
                            ch.pipeline().addLast(new WebSocketServerProtocolHandler("/ws", "WebSocket", true, 65536 * 10));
                            // 自定义消息处理类，处理业务逻辑
                            ch.pipeline().addLast(webSocketHandler);
                            // ch.pipeline().addLast(new NettyWebSocketServerHandler());
                        }
                    });
            //【6】阻塞等待直到服务器Channel关闭(closeFuture()方法获取Channel
            ChannelFuture cf = sb.bind().sync(); // 服务器异步创建绑定
            log.info(ChatNettyServer.class + "已启动，正在监听： {}", cf.channel().localAddress());
            cf.channel().closeFuture().sync(); // 关闭服务器通道
        } finally {
            //【7】优雅释放相关线程组资源
            group.shutdownGracefully().sync(); // 释放线程池资源
            bossGroup.shutdownGracefully().sync();
        }
    }
}
