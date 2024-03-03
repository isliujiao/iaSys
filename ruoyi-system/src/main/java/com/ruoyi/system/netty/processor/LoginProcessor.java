package com.ruoyi.system.netty.processor;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.constant.ChannelAttrKey;
import com.ruoyi.common.constant.IMRedisKey;
import com.ruoyi.common.core.model.IMLoginInfo;
import com.ruoyi.common.core.model.IMSendInfo;
import com.ruoyi.common.core.model.IMSessionInfo;
import com.ruoyi.common.enums.chat.IMCmdType;
import com.ruoyi.system.netty.IMServerGroup;
import com.ruoyi.system.netty.UserChannelCtxMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginProcessor extends AbstractMessageProcessor<IMLoginInfo> {
//
//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;
//
//    @Value("${jwt.accessToken.secret}")
//    private String accessTokenSecret;
//
//    @Override
//    public synchronized void process(ChannelHandlerContext ctx, IMLoginInfo loginInfo) {
//        if (!JwtUtil.checkSign(loginInfo.getAccessToken(), accessTokenSecret)) {
//            ctx.channel().close();
//            log.warn("用户token校验不通过，强制下线,token:{}", loginInfo.getAccessToken());
//        }
//        String strInfo = JwtUtil.getInfo(loginInfo.getAccessToken());
//        IMSessionInfo sessionInfo = JSON.parseObject(strInfo, IMSessionInfo.class);
//        Long userId = sessionInfo.getUserId();
//        Integer terminal = sessionInfo.getTerminal();
//        log.info("用户登录，userId:{}", userId);
//        ChannelHandlerContext context = UserChannelCtxMap.getChannelCtx(userId, terminal);
//        if (context != null && !ctx.channel().id().equals(context.channel().id())) {
//            // 不允许多地登录,强制下线
//            IMSendInfo<Object> sendInfo = new IMSendInfo<>();
//            sendInfo.setCmd(IMCmdType.FORCE_LOGUT.code());
//            sendInfo.setData("您已在其他地方登陆，将被强制下线");
//            context.channel().writeAndFlush(sendInfo);
//            log.info("异地登录，强制下线,userId:{}", userId);
//        }
//        // 绑定用户和channel
//        UserChannelCtxMap.addChannelCtx(userId, terminal, ctx);
//        // 设置用户id属性
//        AttributeKey<Long> userIdAttr = AttributeKey.valueOf(ChannelAttrKey.USER_ID);
//        ctx.channel().attr(userIdAttr).set(userId);
//        // 设置用户终端类型
//        AttributeKey<Integer> terminalAttr = AttributeKey.valueOf(ChannelAttrKey.TERMINAL_TYPE);
//        ctx.channel().attr(terminalAttr).set(terminal);
//        // 初始化心跳次数
//        AttributeKey<Long> heartBeatAttr = AttributeKey.valueOf("HEARTBEAt_TIMES");
//        ctx.channel().attr(heartBeatAttr).set(0L);
//        // 在redis上记录每个user的channelId，15秒没有心跳，则自动过期
//        String key = String.join(":", IMRedisKey.IM_USER_SERVER_ID, userId.toString(), terminal.toString());
//        redisTemplate.opsForValue().set(key, IMServerGroup.serverId, IMConstant.ONLINE_TIMEOUT_SECOND, TimeUnit.SECONDS);
//        // 响应ws
//        IMSendInfo<Object> sendInfo = new IMSendInfo<>();
//        sendInfo.setCmd(IMCmdType.LOGIN.code());
//        ctx.channel().writeAndFlush(sendInfo);
//    }
//
//
//    @Override
//    public IMLoginInfo transForm(Object o) {
//        HashMap map = (HashMap) o;
//        return BeanUtil.fillBeanWithMap(map, new IMLoginInfo(), false);
//    }
}
