package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.core.domain.entity.MsgList;
import com.ruoyi.common.core.dto.ChatMsgVO;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.mapper.MsgListMapper;
import com.ruoyi.system.service.MsgListService;
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
    // Service层修改
    public PageInfo<ChatMsgVO> getMessageNoticeList(MsgList msgQueryRequest) {
        // 获取分页参数
        PageDomain pageDomain = TableSupport.buildPageRequest();
        PageHelper.startPage(pageDomain.getPageNum(), pageDomain.getPageSize());

        // 原始查询
        List<MsgList> msgList = msgListMapper.getMessageNoticeList(msgQueryRequest);

        // 转换为Page对象
        Page<MsgList> page = (Page<MsgList>) msgList;

        // 处理数据并保留分页信息
        List<ChatMsgVO> msgVOList = page.getResult().stream().map(info -> {
            ChatMsgVO chatMsgVO = new ChatMsgVO();
            BeanUtil.copyProperties(info, chatMsgVO);
            chatMsgVO.setSendTime(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss",info.getCreateTime()));
            chatMsgVO.setSendMsg(info.getContent());
            return chatMsgVO;
        }).collect(Collectors.toList());

        // 创建新的Page对象
        Page<ChatMsgVO> resultPage = new Page<>(page.getPageNum(), page.getPageSize());
        resultPage.setTotal(page.getTotal());
        resultPage.addAll(ListUtil.reverse(msgVOList));

        return new PageInfo<>(resultPage);
    }
}
