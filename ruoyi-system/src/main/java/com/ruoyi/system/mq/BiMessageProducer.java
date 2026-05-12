package com.ruoyi.system.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class BiMessageProducer {

    private static final Logger log = LoggerFactory.getLogger(BiMessageProducer.class);

    @Autowired(required = false)
    @Nullable
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     * @param message
     */
    public void sendMessage(String message) {
        if (rabbitTemplate == null) {
            log.warn("RabbitMQ 未连接，跳过消息发送: {}", message);
            return;
        }
        // 交换机名、路由Key、消息体
        rabbitTemplate.convertAndSend(BiMqConstant.BI_EXCHANGE_NAME, BiMqConstant.BI_ROUTING_KEY, message);
    }

}
