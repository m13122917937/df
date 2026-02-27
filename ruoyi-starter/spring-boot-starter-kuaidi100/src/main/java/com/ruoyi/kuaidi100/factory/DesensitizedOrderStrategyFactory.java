package com.ruoyi.kuaidi100.factory;

import com.ruoyi.kuaidi100.model.strategy.PlatformType;
import com.ruoyi.kuaidi100.strategy.DesensitizedOrderStrategy;
import com.ruoyi.kuaidi100.strategy.impl.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 脱敏订单打印策略工厂
 *
 * <p>根据平台类型获取对应的策略实现
 *
 * @author kuaidi100
 */
@Slf4j
@Component
public class DesensitizedOrderStrategyFactory {

    @Autowired
    private PinduoduoDesensitizedOrderStrategy pinduoduoStrategy;

    @Autowired
    private DouyinDesensitizedOrderStrategy douyinStrategy;

    @Autowired
    private KuaishouDesensitizedOrderStrategy kuaishouStrategy;

    @Autowired
    private TaobaoDesensitizedOrderStrategy taobaoStrategy;

    /**
     * 策略映射表
     */
    private final Map<String, DesensitizedOrderStrategy> strategyMap = new HashMap<>();

    /**
     * 初始化策略映射表
     */
    @PostConstruct
    public void init() {
        strategyMap.put(PlatformType.PINDUODUO.getCode(), pinduoduoStrategy);
        strategyMap.put(PlatformType.DOUYIN.getCode(), douyinStrategy);
        strategyMap.put(PlatformType.KUAISHOU.getCode(), kuaishouStrategy);
        strategyMap.put(PlatformType.TAOBAO.getCode(), taobaoStrategy);

        log.info("【脱敏订单策略工厂】初始化完成，支持平台数量: {}", strategyMap.size());
    }

    /**
     * 根据平台类型获取策略
     *
     * @param platformType 平台类型
     * @return 对应的策略实现
     */
    public DesensitizedOrderStrategy getStrategy(PlatformType platformType) {
        DesensitizedOrderStrategy strategy = strategyMap.get(platformType.getCode());
        if (strategy == null) {
            log.error("【脱敏订单策略工厂】未找到平台策略: {}", platformType.getCode());
            throw new IllegalArgumentException("不支持的平台类型: " + platformType.getName());
        }
        log.debug("【脱敏订单策略工厂】获取策略: {}", platformType.getName());
        return strategy;
    }

    /**
     * 根据平台编码获取策略
     *
     * @param platformCode 平台编码
     * @return 对应的策略实现
     */
    public DesensitizedOrderStrategy getStrategy(String platformCode) {
        return getStrategy(PlatformType.fromCode(platformCode));
    }
}