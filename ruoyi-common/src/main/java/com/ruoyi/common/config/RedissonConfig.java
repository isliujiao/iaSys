package com.ruoyi.common.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置
 *
 * @author liujiao
 * @date 2023/11/6 21:51
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis") // 读取配置文件
@Data
public class RedissonConfig {

    private Integer database;
    private String host;
    private Integer port;


    /**
     * 注入 RedissonConfig Bean实例
     * @return
     */
    @Bean
    public RedissonClient getRedissonClient(){
        Config config = new Config();
        // 填充连接redis配置
        config.useSingleServer().setDatabase(database).setAddress("redis://" + host + ":" + port);

        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
