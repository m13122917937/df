package com.ruoyi.kuaidi100.strategy.impl;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.kuaidi100.model.e.EOrderRequestParam;
import com.ruoyi.kuaidi100.model.strategy.DesensitizedOrderRequest;
import com.ruoyi.kuaidi100.model.strategy.PlatformAuthInfo;
import com.ruoyi.kuaidi100.model.strategy.PlatformType;
import com.ruoyi.kuaidi100.strategy.DesensitizedOrderStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 菜鸟淘宝脱敏订单打印策略
 *
 * <p>菜鸟淘宝脱敏规则：
 * <ul>
 *   <li>电话：脱敏格式（如：138****25 或 ******_5336）</li>
 *   <li>姓名：脱敏格式（如：锦*_）</li>
 *   <li>地址拆分：province, city, district, addr（脱敏地址）</li>
 *   <li>不需要 detailAddrEnc</li>
 *   <li>需要 oaid（淘宝订单收件人ID）</li>
 * </ul>
 *
 * @author kuaidi100
 */
@Slf4j
@Component
public class TaobaoDesensitizedOrderStrategy extends AbstractDesensitizedOrderStrategy {

    @Override
    public EOrderRequestParam buildOrderParam(DesensitizedOrderRequest request) {
        log.info("【菜鸟淘宝脱敏订单】开始构建订单参数，平台订单ID: {}", request.getThirdOrderId());

        // 1. 构建收件人信息（菜鸟淘宝不需要 detailAddrEnc）
        String recipientJson = buildTaobaoRecipientInfo(request);

        // 2. 构建寄件人信息
        String senderJson = buildSenderInfo(request);

        // 3. 构建货物信息
        String cargoJson = buildCargoInfo(request);

        // 4. 构建基础参数
        EOrderRequestParam param = buildBaseOrderParam(request, recipientJson, senderJson, cargoJson);

        // 5. 添加平台特有参数（包括 oaid）
        addPlatformSpecificParams(param, request.getAuthInfo(), request.getThirdOrderId());

        log.info("【菜鸟淘宝脱敏订单】订单参数构建完成");
        return param;
    }

    /**
     * 构建菜鸟淘宝收件人信息（不包含 detailAddrEnc）
     *
     * @param request 脱敏订单请求
     * @return 收件人信息JSON
     */
    private String buildTaobaoRecipientInfo(DesensitizedOrderRequest request) {
        DesensitizedOrderRequest.RecipientEncryptedInfo info = request.getRecipientInfo();

        StringBuilder recipientBuilder = new StringBuilder();
        recipientBuilder.append("\"name\":\"").append(info.getName()).append("\",");
        recipientBuilder.append("\"mobile\":\"").append(info.getMobile()).append("\",");
        recipientBuilder.append("\"province\":\"").append(info.getProvince()).append("\",");
        recipientBuilder.append("\"city\":\"").append(info.getCity()).append("\",");
        recipientBuilder.append("\"district\":\"").append(info.getDistrict()).append("\",");
        recipientBuilder.append("\"addr\":\"").append(info.getAddr()).append("\"");

        return "{" + recipientBuilder + "}";
    }

    /**
     * 添加菜鸟淘宝平台特有参数
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

        // 菜鸟淘宝需要 oaid 参数
        if (authInfo != null && StrUtil.isNotBlank(authInfo.getOaid())) {
            param.setOaid(authInfo.getOaid());
            log.debug("【菜鸟淘宝脱敏订单】平台参数 - partnerId: {}, thirdOrderId: {}, oaid: {}",
                    authInfo.getPartnerId(), thirdOrderId, authInfo.getOaid());
        } else {
            log.warn("【菜鸟淘宝脱敏订单】缺少 oaid 参数");
        }
    }

    @Override
    public String getPlatformCode() {
        return PlatformType.TAOBAO.getCode();
    }
}