package com.ruoyi.quartz.task;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.IMRedisKey;
import com.ruoyi.common.core.model.IMRecvInfo;
import com.ruoyi.common.enums.chat.IMCmdType;
import com.ruoyi.system.netty.IMServerGroup;
import com.ruoyi.system.netty.processor.AbstractMessageProcessor;
import com.ruoyi.system.netty.processor.ProcessorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class PullPrivateMessageTask extends AbstractPullMessageTask {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void pullMessage() {
        // 从redis拉取未读消息
//        String key = String.join(":", IMRedisKey.IM_MESSAGE_PRIVATE_QUEUE, IMServerGroup.serverId + "");
//        JSONObject jsonObject = (JSONObject) redisTemplate.opsForList().leftPop(key);
//        while (!Objects.isNull(jsonObject)) {
//            IMRecvInfo recvInfo = jsonObject.toJavaObject(IMRecvInfo.class);
//            AbstractMessageProcessor processor = ProcessorFactory.createProcessor(IMCmdType.PRIVATE_MESSAGE);
//            processor.process(recvInfo);
//            // 下一条消息
//            jsonObject = (JSONObject) redisTemplate.opsForList().leftPop(key);
//        }
    }
}
