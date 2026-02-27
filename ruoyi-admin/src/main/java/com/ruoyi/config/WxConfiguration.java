package com.ruoyi.config;

import cn.hutool.core.collection.CollUtil;
import com.ruoyi.biz.wx.WxLogHandler;
import com.ruoyi.biz.wx.WxScanHandler;
import com.ruoyi.biz.wx.WxSubscribeHandler;
import com.ruoyi.common.core.redis.RedisKeyUtil;
import com.ruoyi.config.properties.WxMpProperties;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import me.chanjar.weixin.mp.config.impl.WxMpRedisConfigImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static me.chanjar.weixin.common.api.WxConsts.EventType.SCAN;
import static me.chanjar.weixin.common.api.WxConsts.EventType.SUBSCRIBE;
import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType.EVENT;

@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(value = {WxMpProperties.class})
public class WxConfiguration {
    private final StringRedisTemplate redisTemplate;
    private final WxLogHandler logHandler;
    private final WxSubscribeHandler subscribeHandler;
    private final WxScanHandler scanHandler;

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
                configStorage = new WxMpRedisConfigImpl(new RedisTemplateWxRedisOps(redisTemplate), RedisKeyUtil.generate("fy_wx", a.getAppId()));
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
    public WxMpMessageRouter messageRouter(WxMpService wxMpService) {
        final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);

        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(this.logHandler).next();

        // 关注事件
        newRouter.rule().async(false).msgType(EVENT).event(SUBSCRIBE).handler(this.subscribeHandler).end();
//        // 取消关注事件
//        newRouter.rule().async(false).msgType(EVENT).event(UNSUBSCRIBE).handler(this.unsubscribeHandler).end();
        //扫码事件
        newRouter.rule().async(false).msgType(EVENT).event(SCAN).handler(this.scanHandler).end();

        // 默认
//        newRouter.rule().async(false).handler(this.msgHandler).end();

        return newRouter;
    }


}
