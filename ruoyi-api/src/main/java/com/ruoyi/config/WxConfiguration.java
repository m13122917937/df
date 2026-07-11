package com.ruoyi.config;

import cn.hutool.core.collection.CollUtil;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.ruoyi.common.core.redis.RedisKeyUtil;
import com.ruoyi.config.properties.WxMpProperties;
import com.ruoyi.config.properties.WxPayProperties;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.redis.RedissonWxRedisOps;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import me.chanjar.weixin.mp.config.impl.WxMpRedisConfigImpl;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(value = {WxMpProperties.class, WxPayProperties.class})
public class WxConfiguration {

    private final RedissonClient redissonClient;

    @Bean
    public WxMpService wxMpService(final WxMpProperties mpProperties) {
        // 代码里 getConfigs()处报错的同学，请注意仔细阅读项目说明，你的IDE需要引入lombok插件！！！！
        final List<WxMpProperties.MpConfig> configs = mpProperties.getConfigs();
        if (CollUtil.isEmpty(configs)) {
            throw new RuntimeException("未配置wx-java相关配置");
        }

        WxMpService service = new WxMpServiceImpl();
        service.setMultiConfigStorages(configs.stream().map(a -> {
            WxMpDefaultConfigImpl configStorage;
            if (mpProperties.isUseRedis()) {
                configStorage = new WxMpRedisConfigImpl(new RedissonWxRedisOps(redissonClient), RedisKeyUtil.generate("fy_wx", a.getAppId()));
            } else {
                configStorage = new WxMpDefaultConfigImpl();
            }

            configStorage.setAppId(a.getAppId());
            configStorage.setSecret(a.getSecret());
            configStorage.setToken(a.getToken());
            configStorage.setAesKey(a.getAesKey());
            return configStorage;
        }).collect(Collectors.toMap(WxMpDefaultConfigImpl::getAppId, c -> c, (o, n) -> o)));
        return service;
    }

    @Bean
    public WxPayService wxPayService(final WxPayProperties payProperties, final WxMpProperties mpProperties) {
        WxPayService payService = new WxPayServiceImpl();
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setMchId(payProperties.getMchId());
        payConfig.setMchKey(payProperties.getMchKey());
        payConfig.setKeyPath(payProperties.getKeyPath());
        payConfig.setApiV3Key(payProperties.getMchV3Key());
        payConfig.setPrivateKeyPath(payProperties.getPrivateKeyPath());
        payConfig.setPrivateCertPath(payProperties.getPrivateCertPath());
        payService.setConfig(payConfig);
        return payService;
    }


}
