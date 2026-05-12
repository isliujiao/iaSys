package com.ruoyi.system.mq.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * 消息生产者
 */
@Component
public class MyMessageProducer {

    private static final Logger log = LoggerFactory.getLogger(MyMessageProducer.class);

    @Autowired(required = false)
    @Nullable
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String exchange, String routingKey, String message) {
        if (rabbitTemplate == null) {
            log.warn("RabbitMQ 未连接，跳过消息发送 exchange={}, key={}, msg={}", exchange, routingKey, message);
            return;
        }
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

}
