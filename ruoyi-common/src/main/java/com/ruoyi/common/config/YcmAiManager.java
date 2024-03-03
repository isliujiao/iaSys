package com.ruoyi.common.config;

import com.ruoyi.common.core.domain.ErrorCode;
import com.ruoyi.common.exception.base.BusinessException;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author liujiao
 * @date 2023/11/4 14:59
 */
@Component
public class YcmAiManager {

    @Resource
    YuCongMingClient yuCongMingClient;

    /**
     * AI 对话
     *
     * @param modelId
     * @param message
     * @return
     */
    // 在外层指定模型id，这样会优雅一点
    public String doChat(long modelId, String message) {
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(1720659664934776834L);
        devChatRequest.setMessage(message);

        BaseResponse<DevChatResponse> response = yuCongMingClient.doChat(devChatRequest);
        if (response == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 响应错误");
        }
        return response.getData().getContent();
    }


}
