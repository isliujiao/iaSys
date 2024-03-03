package com.ruoyi.system.netty.processor;

import com.ruoyi.common.core.model.IMRecvInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;



@Slf4j
@Component
@RequiredArgsConstructor
public class PrivateMessageProcessor extends AbstractMessageProcessor<IMRecvInfo> {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void process(IMRecvInfo recvInfo) {
//        IMUserInfo sender = recvInfo.getSender();
//        IMUserInfo receiver = recvInfo.getReceivers().get(0);
//        log.info("接收到消息，发送者:{},接收者:{}，内容:{}", sender.getId(), receiver.getId(), recvInfo.getData());
//        try {
//            ChannelHandlerContext channelCtx = UserChannelCtxMap.getChannelCtx(receiver.getId(), receiver.getTerminal());
//            if (channelCtx != null) {
//                // 推送消息到用户
//                IMSendInfo<Object> sendInfo = new IMSendInfo<>();
//                sendInfo.setCmd(IMCmdType.PRIVATE_MESSAGE.code());
//                sendInfo.setData(recvInfo.getData());
//                channelCtx.channel().writeAndFlush(sendInfo);
//                // 消息发送成功确认
//                sendResult(recvInfo, IMSendCode.SUCCESS);
//            } else {
//                // 消息推送失败确认
//                sendResult(recvInfo, IMSendCode.NOT_FIND_CHANNEL);
//                log.error("未找到channel，发送者:{},接收者:{}，内容:{}", sender.getId(), receiver.getId(), recvInfo.getData());
//            }
//        } catch (Exception e) {
//            // 消息推送失败确认
//            sendResult(recvInfo, IMSendCode.UNKONW_ERROR);
//            log.error("发送异常，发送者:{},接收者:{}，内容:{}", sender.getId(), receiver.getId(), recvInfo.getData(), e);
//        }
//
    }
//
//    private void sendResult(IMRecvInfo recvInfo, IMSendCode sendCode) {
//        if (recvInfo.getSendResult()) {
//            IMSendResult<Object> result = new IMSendResult<>();
//            result.setSender(recvInfo.getSender());
//            result.setReceiver(recvInfo.getReceivers().get(0));
//            result.setCode(sendCode.code());
//            result.setData(recvInfo.getData());
//            // 推送到结果队列
//            String key = StrUtil.join(":", IMRedisKey.IM_RESULT_PRIVATE_QUEUE,recvInfo.getServiceName());
//            redisTemplate.opsForList().rightPush(key, result);
//        }
//    }
}
