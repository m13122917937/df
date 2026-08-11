package com.ruoyi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 微信小程序应用启动入口。
 */
@SpringBootApplication
@EnableScheduling
public class AppApplication {

    /**
     * 启动小程序接口服务。
     *
     * @param args 启动参数
     */
    public static void main(final String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }
}
