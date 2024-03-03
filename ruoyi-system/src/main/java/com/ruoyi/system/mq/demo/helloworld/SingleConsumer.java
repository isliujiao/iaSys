package com.ruoyi.system.mq.demo.helloworld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/**
 * 消费者
 */
public class SingleConsumer {
	// 定义我们正在监听的队列名称（消费者和生产者队列名保持一致）
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
         // 创建连接,创建连接工厂，并设置主机地址
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

    	// 从工厂获取一个新的连接
        Connection connection = factory.newConnection();
        // 从连接中创建一个新的通道
        Channel channel = connection.createChannel();

    	// 创建队列,在该通道上声明我们正在监听的队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    	// 创建一个 DeliverCallback 来处理接收到的消息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");
        };

        // 在通道上开始消费队列中的消息，接收到的消息会传递给 deliverCallback 来处理,会持续阻塞
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
}
