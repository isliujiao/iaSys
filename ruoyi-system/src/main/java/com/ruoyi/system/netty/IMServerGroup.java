package com.ruoyi.system.netty;

import com.ruoyi.common.constant.IMRedisKey;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;

/**
 * TODO 该IM为盒子IM迁移代码，目前系统未用
 */
@Slf4j
@Component
@AllArgsConstructor
public class IMServerGroup implements CommandLineRunner {

    public static volatile long serverId = 0;

//    RedisTemplate<String, Object> redisTemplate;

    private final List<IMServer> imServers;

    /***
     * 判断服务器是否就绪
     *
     * @return
     **/
    public boolean isReady() {
        for (IMServer imServer : imServers) {
            if (!imServer.isReady()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void run(String... args) throws Exception {
        // 初始化SERVER_ID
        String key = IMRedisKey.IM_MAX_SERVER_ID;
//        serverId = redisTemplate.opsForValue().increment(key, 1);
        // 启动服务
//        for (IMServer imServer : imServers) {
//            imServer.start();
//        }
    }

    @PreDestroy
    public void destroy() {
        // 停止服务
        for (IMServer imServer : imServers) {
            imServer.stop();
        }
    }
}
