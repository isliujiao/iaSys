package com.ruoyi.system.client;

import com.ruoyi.common.constant.IMRedisKey;
import com.ruoyi.common.core.model.IMGroupMessage;
import com.ruoyi.common.core.model.IMRecvInfo;
import com.ruoyi.common.core.model.IMSendResult;
import com.ruoyi.common.core.model.IMUserInfo;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.chat.IMCmdType;
import com.ruoyi.common.enums.chat.IMListenerType;
import com.ruoyi.common.enums.chat.IMSendCode;
import com.ruoyi.common.enums.chat.IMTerminalType;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.*;

@Configuration
@AllArgsConstructor
public class IMClient {

//    @Resource(name="IMRedisTemplate")
//    private RedisTemplate redisTemplate;

    @Resource
    private RedisTemplate redisTemplate;

//    @Value("${spring.application.name}")
//    private String appName;

    /**
     * 发送群聊消息（发送结果通过MessageListener接收）
     *
     * @param message 群聊消息
     */
    public<T> void sendGroupMessage(IMGroupMessage<T> message) {

        // 根据群聊每个成员所连的IM-server，进行分组
        Map<String, IMUserInfo> sendMap = new HashMap<>();
        for (Integer terminal : message.getRecvTerminals()) {
            message.getRecvIds().forEach(id -> {
                String key = String.join(":", IMRedisKey.IM_USER_SERVER_ID, id.toString(), terminal.toString());
                sendMap.put(key,new IMUserInfo(id, terminal));
            });
        }
        // 批量拉取
        List<Object> serverIds = redisTemplate.opsForValue().multiGet(sendMap.keySet());
        // 格式:map<服务器id,list<接收方>>
        Map<Integer, List<IMUserInfo>> serverMap = new HashMap<>();
        List<IMUserInfo> offLineUsers = new LinkedList<>();
        int idx = 0;
        for (Map.Entry<String,IMUserInfo> entry : sendMap.entrySet()) {
            Integer serverId = (Integer)serverIds.get(idx++);
            if (serverId != null) {
                List<IMUserInfo> list = serverMap.computeIfAbsent(serverId, o -> new LinkedList<>());
                list.add(entry.getValue());
            } else {
                // 加入离线列表
                offLineUsers.add(entry.getValue());
            }
        }
        // 逐个server发送
        for (Map.Entry<Integer, List<IMUserInfo>> entry : serverMap.entrySet()) {
            IMRecvInfo recvInfo = new IMRecvInfo();
            recvInfo.setCmd(IMCmdType.GROUP_MESSAGE.code());
            recvInfo.setReceivers(new LinkedList<>(entry.getValue()));
            recvInfo.setSender(message.getSender());
            recvInfo.setServiceName("appName");
            recvInfo.setSendResult(message.getSendResult());
            recvInfo.setData(message.getData());
            // 推送至队列
            String key = String.join(":", IMRedisKey.IM_MESSAGE_GROUP_QUEUE, entry.getKey().toString());
            redisTemplate.opsForList().rightPush(key, recvInfo);
        }

        // 推送给自己的其他终端
//        if (message.getSendToSelf()) {
//            for (Integer terminal : IMTerminalType.codes()) {
//                if (terminal.equals(message.getSender().getTerminal())) {
//                    continue;
//                }
//                // 获取终端连接的channelId
//                String key = String.join(":", IMRedisKey.IM_USER_SERVER_ID, message.getSender().getId().toString(), terminal.toString());
//                Integer serverId = (Integer)redisTemplate.opsForValue().get(key);
//                // 如果终端在线，将数据存储至redis，等待拉取推送
//                if (serverId != null) {
//                    IMRecvInfo recvInfo = new IMRecvInfo();
//                    recvInfo.setCmd(IMCmdType.GROUP_MESSAGE.code());
//                    recvInfo.setSender(message.getSender());
//                    recvInfo.setReceivers(Collections.singletonList(new IMUserInfo(message.getSender().getId(), terminal)));
//                    // 自己的消息不需要回推消息结果
//                    recvInfo.setSendResult(false);
//                    recvInfo.setData(message.getData());
//                    String sendKey = String.join(":", IMRedisKey.IM_MESSAGE_GROUP_QUEUE, serverId.toString());
//                    redisTemplate.opsForList().rightPush(sendKey, recvInfo);
//                }
//            }
//        }
    }

//    private final IMSender imSender;
//
//    /**
//     * 判断用户是否在线
//     *
//     * @param userId 用户id
//     */
//    public Boolean isOnline(Long userId){
//        return imSender.isOnline(userId);
//    }
//
//    /**
//     * 判断多个用户是否在线
//     *
//     * @param userIds 用户id列表
//     * @return 在线的用户列表
//     */
//    public List<Long> getOnlineUser(List<Long> userIds){
//        return imSender.getOnlineUser(userIds);
//    }
//
//
//    /**
//     * 判断多个用户是否在线
//     *
//     * @param userIds 用户id列表
//     * @return 在线的用户终端
//     */
//    public Map<Long,List<IMTerminalType>> getOnlineTerminal(List<Long> userIds){
//        return imSender.getOnlineTerminal(userIds);
//    }
//
//    /**
//     * 发送私聊消息（发送结果通过MessageListener接收）
//     *
//     * @param message 私有消息
//     */
//    public<T> void sendPrivateMessage(IMPrivateMessage<T> message){
//        imSender.sendPrivateMessage(message);
//    }


}
