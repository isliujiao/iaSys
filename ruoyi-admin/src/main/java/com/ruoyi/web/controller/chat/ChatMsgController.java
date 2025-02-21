package com.ruoyi.web.controller.chat;

import com.github.pagehelper.PageInfo;
import com.ruoyi.chat.ChatNettyServer;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.entity.MsgList;
import com.ruoyi.common.core.dto.ChatMsgVO;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.service.MsgListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liujiao
 * @date 2024/1/7 16:35
 */
@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatMsgController extends BaseController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private MsgListService msgListService;
    @Autowired
    private ChatNettyServer nettyServer;

    /**
     * 关闭用户连接
     */
    @GetMapping("/close")
    public void closeUserConnect(String token) {
//        if(StringUtils.isEmpty(token)){
//            log.error("---关闭用户连接，token为空---");
//            return;
//        }
//        LoginUser loginUser = tokenService.getLoginUser(token);
//        nettyServer.USERS.remove(loginUser.getUserId().toString());
//        log.info("---关闭用户：{} 连接--", loginUser.getUserId());
        if (!StringUtils.isEmpty(SecurityUtils.getUserId().toString())) {
            nettyServer.USERS.remove(SecurityUtils.getUserId().toString());
            log.info("---关闭连接用户：{}、用户名：{}--", SecurityUtils.getUserId(), SecurityUtils.getUsername());
        }
    }


    @GetMapping("/getMessageNoticeList")
    public TableDataInfo getMessageNoticeList(MsgList msgList) {
        PageInfo<ChatMsgVO> pageInfo = msgListService.getMessageNoticeList(msgList);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(pageInfo.getList());
        rspData.setTotal(pageInfo.getTotal());
        return rspData;
    }

}
