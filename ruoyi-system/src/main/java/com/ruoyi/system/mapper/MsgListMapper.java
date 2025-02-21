package com.ruoyi.system.mapper;

import com.ruoyi.common.core.domain.entity.MsgList;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author liujiao
 * @date 2024/11/12 18:10
 */
@Mapper
public interface MsgListMapper {

    int save(MsgList message);

    /**
     * 分页获取用户房间消息列表
     *
     * @param msgList
     * @return
     */
    List<MsgList> getMessageNoticeList(MsgList msgList);

}
