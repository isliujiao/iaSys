package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.entity.MsgList;
import com.ruoyi.common.core.dto.ChatMsgVO;

import java.util.List;

/**
 * @author liujiao
 * @date 2024/11/12 18:08
 */
public interface MsgListService {
    int save(MsgList message);

    /**
     * 聊天查询请求
     *
     * @param msgQueryRequest
     * @return
     */
    List<ChatMsgVO> getMessageNoticeList(MsgList msgQueryRequest);
}
