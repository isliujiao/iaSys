package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import com.ruoyi.common.core.domain.entity.MsgList;
import com.ruoyi.common.core.dto.ChatMsgVO;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.dto.chat.MsgQueryRequest;
import com.ruoyi.system.mapper.MsgListMapper;
import com.ruoyi.system.service.MsgListService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liujiao
 * @date 2024/11/12 18:09
 */
@Service
public class MsgListServiceImpl implements MsgListService {
    @Autowired
    MsgListMapper msgListMapper;

    @Override
    public int save(MsgList message) {
        return msgListMapper.save(message);
    }

    @Override
    public List<ChatMsgVO> getMessageNoticeList(MsgQueryRequest msgQueryRequest) {
        if (ObjectUtils.isEmpty(msgQueryRequest.getRoomId())) {
            msgQueryRequest.setRoomId(0L);
        }
        List<MsgList> msgList = msgListMapper.getMessageNoticeList(msgQueryRequest);
        List<ChatMsgVO> msgVOList = msgList.stream().map(info -> {
            ChatMsgVO chatMsgVO = new ChatMsgVO();
            BeanUtil.copyProperties(info, chatMsgVO);
            chatMsgVO.setSendTime(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss",info.getCreateTime()));
            chatMsgVO.setSendMsg(info.getContent());
            return chatMsgVO;
        }).collect(Collectors.toList());
        return ListUtil.reverse(msgVOList);
    }
}
