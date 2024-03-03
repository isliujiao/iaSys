package com.ruoyi.system.mq.demo.publishsubscribe;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

public class FanoutConsumer {
    private static final String EXCHANGE_NAME = "fanout-exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();

        // 创建两个通道
        Channel channelA = connection.createChannel();
        Channel channelB = connection.createChannel();

        // 声明交换机（exchange types：`direct`, `topic`, `headers` and `fanout`）
        channelA.exchangeDeclare(EXCHANGE_NAME, "fanout");
        channelB.exchangeDeclare(EXCHANGE_NAME, "fanout");

        // 创建队列1，随机分配一个队列名称
        String queueNameA = "xiaowang_queue";
        channelA.queueDeclare(queueNameA, true, false, false, null);
        channelA.queueBind(queueNameA, EXCHANGE_NAME, "");

        // 创建队列2
        String queueNameB = "xiaoli_queue";
        channelB.queueDeclare(queueNameB, true, false, false, null);
        channelB.queueBind(queueNameB, EXCHANGE_NAME, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // 创建交付回调函数1
        DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [小王] Received '" + message + "'");
        };
        // 创建交付回调函数2
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [小李] Received '" + message + "'");
        };

        // 开始消费消息队列1
        channelA.basicConsume(queueNameA, true, deliverCallback1, consumerTag -> {
            System.out.println(" [*] 开始消费消息队列1");
        });
        // 开始消费消息队列2
        channelB.basicConsume(queueNameB, true, deliverCallback2, consumerTag -> {
            System.out.println(" [*] 开始消费消息队列2");
        });
    }
}
