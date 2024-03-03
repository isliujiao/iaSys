package com.ruoyi.system.mq.demo.helloworld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

/**
 * 发布者
 */
public class SingleProducer {

    /**
     * 定义一个名为QUEUE_NAME的常量，用于指定队列名称
     */
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        // 创建一个ConnectionFactory对象，这个对象可以用于创建到RabbitMQ服务器的连接
        ConnectionFactory factory = new ConnectionFactory();

        // 将连接到本地运行的RabbitMQ服务器（如果修改了用户名、密码、端口则需要设置）
        factory.setHost("localhost");
        // factory.setUsername();
        // factory.setPassword();
        // factory.setPort();

        // 创建一个与mq交互的连接、通过已建立的连接创建一个新的通道
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            // 在通道上声明一个队列，使用指定队列名
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello World!";
            // 使用通道向队列发布消息
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}
