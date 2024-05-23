package com.ruoyi;

import com.ruoyi.chat.ChatNettyServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动程序
 */
@EnableScheduling
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan("com.ruoyi.system.mapper")
public class RuoYiApplication implements CommandLineRunner, ApplicationRunner {

    public static void main(String[] args) {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(RuoYiApplication.class, args);
    }

    @Autowired
    private ChatNettyServer nettyServer;

    @Override
    public void run(String... args) throws Exception {
        nettyServer.start();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("(ﾉ◕ヮ◕)ﾉ*:･ﾟ✧  IA智能分析平台 启动成功 ╲(｡◕‿◕｡)╱\n"+
                " ___    _    \n" +
                "|_ _|  / \\   \n" +
                " | |  / _ \\  \n" +
                " | | / ___ \\ \n" +
                "|___/_/   \\_\\\n" +
                "             "
        );
    }
}
