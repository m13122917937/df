package com.ruoyi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 启动程序
 *
 * @author ruoyi
 */
@Slf4j
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class AdminApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(AdminApplication.class, args);

        log.info("项目启动成功，端口号：{}", run.getEnvironment().getProperty("server.port"));
    }
}
