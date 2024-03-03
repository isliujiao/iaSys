package com.ruoyi.common.config;

import com.ruoyi.common.core.domain.ErrorCode;
import com.ruoyi.common.exception.base.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Redis 限流器；令牌桶算法
 *
 * @author liujiao
 * @date 2023/11/6 21:50
 */
@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 设置 Redis 限流器
     */
    public void setRedisCurrentLimit(String key){
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
//        rateLimiter.setRate(RateType.OVERALL,2,1, RateIntervalUnit.SECONDS); //设置整个速率限制
        // 每 1 秒 2 个请求。 尝试设置速率限制,成功true 失败false
        rateLimiter.trySetRate(RateType.OVERALL,2,1, RateIntervalUnit.SECONDS);

        // 每当一个操作来了后，请求一个令牌
        boolean canOp = rateLimiter.tryAcquire(1);
        // 如果没有令牌,还想执行操作,就抛出异常
        if (!canOp) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }

}
