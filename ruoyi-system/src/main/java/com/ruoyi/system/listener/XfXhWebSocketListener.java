package com.ruoyi.system.listener;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.dto.XfxhMsgDTO;
import com.ruoyi.common.core.dto.XfxhRespDTO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;


/**
 * 讯飞星火监听器
 */
@Slf4j
public class XfXhWebSocketListener extends WebSocketListener {
    private StringBuilder answer = new StringBuilder();

    private boolean wsCloseFlag = false;

    public StringBuilder getAnswer() {
        return answer;
    }

    public boolean isWsCloseFlag() {
        return wsCloseFlag;
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        super.onOpen(webSocket, response);
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        super.onMessage(webSocket, text);
        // 将大模型回复的 JSON 文本转为 ResponseDTO 对象
        XfxhRespDTO responseData = JSONObject.parseObject(text, XfxhRespDTO.class);
        // 如果响应数据中的 header 的 code 值不为 0，则表示响应错误
        if (responseData.getHeader().getCode() != 0) {
            // 日志记录
            log.error("发生错误，错误码为：" + responseData.getHeader().getCode() + "; " + "信息：" + responseData.getHeader().getMessage());
            // 设置回答
            this.answer = new StringBuilder("大模型响应错误，请稍后再试");
            // 关闭连接标识
            wsCloseFlag = true;
            return;
        }
        // 将回答进行拼接
        for (XfxhMsgDTO msgDTO : responseData.getPayload().getChoices().getText()) {
            this.answer.append(msgDTO.getContent());
        }
        // 对最后一个文本结果进行处理
        if (2 == responseData.getHeader().getStatus()) {
            wsCloseFlag = true;
        }
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosed(webSocket, code, reason);
    }
}
