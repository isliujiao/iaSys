package com.ruoyi.system.domain.dto.chat;

import com.ruoyi.common.core.domain.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 聊天查询请求
 *
 * @author liujiao
 * @date 2024/11/13 11:32
 */
@Data
public class MsgQueryRequest extends PageRequest implements Serializable {

    /**
     * 房间 ID
     */
    private Long roomId;

}
