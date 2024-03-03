package com.ruoyi.web.controller.chat;


import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.enums.chat.XfxhTokenEnum;
import com.ruoyi.common.listener.XfXhWebSocketListener;
import com.ruoyi.common.config.XfxhConfig;
import com.ruoyi.common.core.dto.XfxhMsgDTO;
import com.ruoyi.system.service.XfXhStreamServer;
import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.UUID;


/**
 * 讯飞星火消息模型
 */
@RestController
@RequestMapping("/xfxh")
@Slf4j
public class XfxhController {

    @Autowired
    private XfXhStreamServer xfXhStreamServer;

    @Autowired
    private XfxhConfig xfXhConfig;


    /**
     * 发送问题
     *
     * @param question 问题
     * @return 星火大模型的回答
     */
    @GetMapping("/sendQuestion")
    public String sendQuestion(@RequestParam("question") String question) {
        // 如果是无效字符串，则不对大模型进行请求
        if (StrUtil.isBlank(question)) {
            return "无效问题，请重新输入";
        }
        // 获取连接令牌
        if (!xfXhStreamServer.operateToken(XfxhTokenEnum.GET_TOKEN_STATUS.getCode())) {
            return "当前大模型连接数过多，请稍后再试";
        }

        // 创建消息对象
        XfxhMsgDTO msgDTO = XfxhMsgDTO.createUserMsg(question);
        // 创建监听器
        XfXhWebSocketListener listener = new XfXhWebSocketListener();
        // 发送问题给大模型，生成 websocket 连接
        String uuid = UUID.randomUUID().toString().substring(0, 10);
        WebSocket webSocket = xfXhStreamServer.sendMsg(uuid, Collections.singletonList(msgDTO),
                listener);

        if (webSocket == null) {
            // 归还令牌
            xfXhStreamServer.operateToken(XfxhTokenEnum.BACK_TOKEN_STATUS.getCode());
            return "系统内部错误，请联系管理员";
        }
        try {
            int count = 0;
            // 为了避免死循环，设置循环次数来定义超时时长
            int maxCount = xfXhConfig.getMaxResponseTime() * 5;
            while (count <= maxCount) {
                Thread.sleep(200);
                if (listener.isWsCloseFlag()) {
                    break;
                }
                count++;
            }
            if (count > maxCount) {
                return "大模型响应超时，请联系管理员";
            }
            // 响应大模型的答案
            System.out.println("响应：" + listener.getAnswer().toString());
            return listener.getAnswer().toString();
        } catch (InterruptedException e) {
            log.error("错误：" + e.getMessage());
            return "系统内部错误，请联系管理员";
        } finally {
            // 关闭 websocket 连接
            webSocket.close(1000, "");
            // 归还令牌
            xfXhStreamServer.operateToken(XfxhTokenEnum.BACK_TOKEN_STATUS.getCode());
        }
    }
}
