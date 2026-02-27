package com.ruoyi.kuaidi100.strategy;

import com.ruoyi.kuaidi100.model.e.EOrderRequestParam;
import com.ruoyi.kuaidi100.model.strategy.DesensitizedOrderRequest;

/**
 * 脱敏订单打印策略接口
 *
 * @author kuaidi100
 */
public interface DesensitizedOrderStrategy {

    /**
     * 构建电子面单请求参数
     *
     * @param request 脱敏订单请求
     * @return 电子面单请求参数
     */
    EOrderRequestParam buildOrderParam(DesensitizedOrderRequest request);

    /**
     * 获取平台类型
     *
     * @return 平台类型
     */
    String getPlatformCode();
}