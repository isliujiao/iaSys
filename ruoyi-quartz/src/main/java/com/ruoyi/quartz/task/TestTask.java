package com.ruoyi.quartz.task;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.common.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;

@Component
@Slf4j
public class TestTask {

//    @Scheduled(cron = "0/2 0/1 * * * ?")
    public void ThreadTest() throws InterruptedException {
        String time = DateUtils.getTime();
        for (int i = 0; i < 1000; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
//                            System.out.println("Hello, World!");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    log.info("{}：,线程：{}" , time, Thread.currentThread().getName());
                }
            }).start();
        }
    }
}
