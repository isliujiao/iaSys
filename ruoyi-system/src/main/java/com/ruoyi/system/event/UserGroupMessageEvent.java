package com.ruoyi.system.event;

import com.ruoyi.common.core.dto.GroupMessageDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 用户群聊事件
 *
 * @author liujiao
 * @date 2024/11/12 17:56
 */
@Getter
public class UserGroupMessageEvent extends ApplicationEvent {
    private final GroupMessageDTO groupMessageDTO;

    public UserGroupMessageEvent(Object source, GroupMessageDTO groupMessageDTO) {
        super(source);
        this.groupMessageDTO = groupMessageDTO;
    }
}
