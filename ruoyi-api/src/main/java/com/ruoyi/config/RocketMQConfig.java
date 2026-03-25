package com.ruoyi.config;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.ruoyi.biz.mq.OrderTradeListener;
import com.ruoyi.biz.mq.SubscribeListener;
import com.ruoyi.config.properties.OnsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.Properties;

@Slf4j
@Configuration
public class RocketMQConfig {

    @Autowired
    OnsProperties onsProperties;

    @Autowired
    OrderTradeListener orderTradeListener;

    @Autowired
    SubscribeListener subscribeListener;


    @Bean
    @Lazy
    public Producer producer() {
        Properties properties = new Properties();
        // 实例用户名和密码在消息队列RocketMQ版控制台访问控制的智能身份识别页签中获取。
        properties.put(PropertyKeyConst.AccessKey, onsProperties.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, onsProperties.getSecretKey());
        properties.put(PropertyKeyConst.Namespace, onsProperties.getInstanceId());
        // 设置发送超时时间，单位：毫秒。
        properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, "3000");
        // 设置为您从消息队列RocketMQ版控制台获取的接入点，类似“rmq-cn-XXXX.rmq.aliyuncs.com:8080”。
        // 注意！！！直接填写控制台提供的域名和端口即可，请勿添加http://或https://前缀标识，也不要用IP解析地址。
        properties.put(PropertyKeyConst.NAMESRV_ADDR, onsProperties.getNameServer());
        Producer producer = ONSFactory.createProducer(properties);
        // 在发送消息前，必须调用start方法来启动Producer，只需调用一次即可。
        producer.start();
        return producer;
    }


    @Bean
    @Lazy
    public Consumer consumer() {
        Properties properties = new Properties();
        // 实例用户名和密码在消息队列RocketMQ版控制台访问控制的智能身份识别页签中获取。
        properties.put(PropertyKeyConst.AccessKey, onsProperties.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, onsProperties.getSecretKey());
        properties.put(PropertyKeyConst.Namespace, onsProperties.getInstanceId());


        properties.put(PropertyKeyConst.GROUP_ID, onsProperties.getCustomerGroup());
        // 设置发送超时时间，单位：毫秒。
        properties.put(PropertyKeyConst.NAMESRV_ADDR, onsProperties.getNameServer());
        Consumer consumer = ONSFactory.createConsumer(properties);
        consumer.subscribe(onsProperties.getOrderTopic(), "*", orderTradeListener);
        consumer.subscribe(onsProperties.getExpressTopic(), "*", subscribeListener);
        consumer.start();
        return consumer;
    }


}