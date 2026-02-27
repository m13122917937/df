package com.ruoyi.kuaidi100.strategy.impl;

import com.ruoyi.kuaidi100.model.e.EOrderRequestParam;
import com.ruoyi.kuaidi100.model.strategy.DesensitizedOrderRequest;
import com.ruoyi.kuaidi100.model.strategy.PlatformAuthInfo;
import com.ruoyi.kuaidi100.model.strategy.PlatformType;
import com.ruoyi.kuaidi100.strategy.DesensitizedOrderStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 拼多多脱敏订单打印策略
 *
 * <p>拼多多脱敏规则：
 * <ul>
 *   <li>电话加密格式：$...=4$</li>
 *   <li>姓名加密格式：~...=~4~</li>
 *   <li>地址拆分：province, city, district, addr（加密）</li>
 *   <li>需要 detailAddrEnc（加密详细地址）</li>
 * </ul>
 *
 * @author kuaidi100
 */
@Slf4j
@Component
public class PinduoduoDesensitizedOrderStrategy extends AbstractDesensitizedOrderStrategy {

    @Override
    public EOrderRequestParam buildOrderParam(DesensitizedOrderRequest request) {
        log.info("【拼多多脱敏订单】开始构建订单参数，平台订单ID: {}", request.getThirdOrderId());

        // 1. 构建收件人信息
        String recipientJson = buildFullRecipientInfo(request, buildBaseRecipientInfo(request));

        // 2. 构建寄件人信息
        String senderJson = buildSenderInfo(request);

        // 3. 构建货物信息
        String cargoJson = buildCargoInfo(request);

        // 4. 构建基础参数
        EOrderRequestParam param = buildBaseOrderParam(request, recipientJson, senderJson, cargoJson);

        // 5. 添加平台特有参数
        addPlatformSpecificParams(param, request.getAuthInfo(), request.getThirdOrderId());

        log.info("【拼多多脱敏订单】订单参数构建完成");
        return param;
    }

    /**
     * 添加拼多多平台特有参数
     *
     * @param param 电子面单请求参数
     * @param authInfo 平台授权信息
     * @param thirdOrderId 平台订单ID
     */
    private void addPlatformSpecificParams(EOrderRequestParam param, PlatformAuthInfo authInfo, String thirdOrderId) {
        param.setPartnerId(authInfo.getPartnerId());
        param.setPartnerKey(authInfo.getPartnerKey());
        param.setPartnerName(authInfo.getPartnerName());
        param.setNet(authInfo.getNet());
        param.setThirdOrderId(thirdOrderId);

        log.debug("【拼多多脱敏订单】平台参数 - partnerId: {}, thirdOrderId: {}",
                authInfo.getPartnerId(), thirdOrderId);
    }

    @Override
    public String getPlatformCode() {
        return PlatformType.PINDUODUO.getCode();
    }
}