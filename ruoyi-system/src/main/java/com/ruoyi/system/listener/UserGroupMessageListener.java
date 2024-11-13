package com.ruoyi.system.listener;

import com.ruoyi.common.core.domain.entity.MsgList;
import com.ruoyi.common.core.dto.GroupMessageDTO;
import com.ruoyi.system.event.UserGroupMessageEvent;
import com.ruoyi.system.service.MsgListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 用户群聊消息监听器
 *
 * @author liujiao
 * @date 2024/11/12 17:46
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class UserGroupMessageListener {

//    private final RoomService roomService;

    private final MsgListService msgListService;

    @Async
    @EventListener(classes = UserGroupMessageEvent.class)
    public void saveDb(UserGroupMessageEvent event) {
        // 获取群聊消息DTO
        GroupMessageDTO privateMessageDTO = event.getGroupMessageDTO();
        // 获取用户ID
        Long loginUserId = privateMessageDTO.getFromUserId();
        MsgList message = MsgList.builder()
                .fromUid(loginUserId)
                .nickName(privateMessageDTO.getNickName())
                .type(privateMessageDTO.getType())
                .content(privateMessageDTO.getContent())
                .roomId(0L).build();
        msgListService.save(message);
        log.info("【用户 - {}，发送消息存储成功：{}】", loginUserId, message);
        //更新房间信息
//        Long roomId = privateMessageDTO.getToRoomId();
//        Room room = Room.builder().id(roomId).lastMsgId(message.getId()).updateTime(message.getUpdateTime()).build();
//        roomService.updateById(room);
    }

}
