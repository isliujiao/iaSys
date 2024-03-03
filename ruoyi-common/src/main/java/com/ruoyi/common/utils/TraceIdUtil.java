package com.ruoyi.common.utils;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * @Description: 多线程日志trace跟踪
 */
public class TraceIdUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraceIdUtil.class);
    private static final String MDC_TRACE_ID_KEY = "traceId";
    private static ThreadLocal<Long> LOCAL = new ThreadLocal();

    public static void create() {
        String traceId = UUID.randomUUID().toString().replace("-", "");
        MDC.put(MDC_TRACE_ID_KEY, traceId);
        LOCAL.set(System.currentTimeMillis());
    }

    public static void create(@NonNull String traceId) {
        if (traceId == null) {
            throw new NullPointerException("traceId is marked @NonNull but is null");
        } else {
            MDC.put(MDC_TRACE_ID_KEY, traceId);
            LOCAL.set(System.currentTimeMillis());
        }
    }

    public static String getLocalTraceId() {
        return MDC.get(MDC_TRACE_ID_KEY);
    }

    public static void remove() {
        LOGGER.info(" 耗时: {} ms", System.currentTimeMillis() - LOCAL.get());
        MDC.remove(MDC_TRACE_ID_KEY);
        LOCAL.remove();
    }
}
