package com.ruoyi.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义线程池
 *
 * @Author
 * @Date 2023/11/7 14:01
 * @Description
 */
@Configuration
public class ThreadPoolExecutorConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        //创建一个线程工厂
        ThreadFactory threadFactory = new ThreadFactory() {
            // 初始化线程数为 1
            private int count = 1;

            // 每当线程池需要创建新线程时，就会调用newThread方法
            @Override
            public Thread newThread(@NotNull Runnable r) {
                // 创建一个新的线程，并给新线程设置一个名称，名称中包含线程数的当前值
                Thread thread = new Thread(r);
                thread.setName("thread-" + count++);
                return thread;
            }
        };
        // 创建线程池，核心-2 Max-4 Time-100s 任务队列-4长阻塞队列 自定义工场
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                2,4,100,
                TimeUnit.SECONDS,new ArrayBlockingQueue<>(4),
                threadFactory);
        return threadPoolExecutor;
    }

}
