package com.ruoyi.web.controller.chat;

import com.ruoyi.chat.ChatNettyServer;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.dto.chart.GenChartByAiRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.ruoyi.common.core.domain.AjaxResult.success;

/**
 * @author liujiao
 * @date 2024/1/7 16:35
 */
@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatMsgController {

    @Autowired
    private TokenService tokenService;

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
        if(!StringUtils.isEmpty(SecurityUtils.getUserId().toString())){
            nettyServer.USERS.remove(SecurityUtils.getUserId().toString());
            log.info("---关闭连接用户：{}、用户名：{}--", SecurityUtils.getUserId(),SecurityUtils.getUsername());
        }
    }
}
