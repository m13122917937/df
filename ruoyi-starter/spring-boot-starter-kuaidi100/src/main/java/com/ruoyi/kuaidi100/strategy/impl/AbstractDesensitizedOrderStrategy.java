package com.ruoyi.kuaidi100.strategy.impl;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.kuaidi100.model.e.EOrderRequestParam;
import com.ruoyi.kuaidi100.model.e.EOrderCargoInfo;
import com.ruoyi.kuaidi100.model.strategy.DesensitizedOrderRequest;
import com.ruoyi.kuaidi100.model.strategy.PlatformType;
import com.ruoyi.kuaidi100.strategy.DesensitizedOrderStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * 脱敏订单打印策略抽象基类
 *
 * @author kuaidi100
 */
@Slf4j
public abstract class AbstractDesensitizedOrderStrategy implements DesensitizedOrderStrategy {

    /**
     * 构建基础收件人信息（省市区）
     *
     * @param request 脱敏订单请求
     * @return 收件人信息JSON
     */
    protected String buildBaseRecipientInfo(DesensitizedOrderRequest request) {
        DesensitizedOrderRequest.RecipientEncryptedInfo info = request.getRecipientInfo();

        // 构建基础地址信息
        StringBuilder addrBuilder = new StringBuilder();
        addrBuilder.append("\"province\":\"").append(info.getProvince()).append("\",");
        addrBuilder.append("\"city\":\"").append(info.getCity()).append("\",");
        addrBuilder.append("\"district\":\"").append(info.getDistrict()).append("\",");

        // 如果有街道信息（快手）
        if (StrUtil.isNotBlank(info.getStreet())) {
            addrBuilder.append("\"street\":\"").append(info.getStreet()).append("\",");
        }

        // 详细地址
        addrBuilder.append("\"addr\":\"").append(info.getAddr()).append("\"");

        return "{" + addrBuilder + "}";
    }

    /**
     * 构建收件人完整信息（包含姓名、电话）
     *
     * @param request 脱敏订单请求
     * @param baseAddrInfo 基础地址信息
     * @return 收件人完整信息JSON
     */
    protected String buildFullRecipientInfo(DesensitizedOrderRequest request, String baseAddrInfo) {
        DesensitizedOrderRequest.RecipientEncryptedInfo info = request.getRecipientInfo();

        StringBuilder recipientBuilder = new StringBuilder();
        recipientBuilder.append("\"name\":\"").append(info.getName()).append("\",");
        recipientBuilder.append("\"mobile\":\"").append(info.getMobile()).append("\",");
        recipientBuilder.append(baseAddrInfo.substring(1, baseAddrInfo.length() - 1)); // 去掉外层大括号

        // 如果有加密详细地址
        if (StrUtil.isNotBlank(info.getDetailAddrEnc())) {
            recipientBuilder.append(",\"detailAddrEnc\":\"").append(info.getDetailAddrEnc()).append("\"");
        }

        return "{" + recipientBuilder + "}";
    }

    /**
     * 构建寄件人信息
     *
     * @param request 脱敏订单请求
     * @return 寄件人信息JSON
     */
    protected String buildSenderInfo(DesensitizedOrderRequest request) {
        DesensitizedOrderRequest.SenderInfo info = request.getSenderInfo();

        StringBuilder senderBuilder = new StringBuilder();
        senderBuilder.append("\"name\":\"").append(info.getName()).append("\",");
        senderBuilder.append("\"mobile\":\"").append(info.getMobile()).append("\",");
        senderBuilder.append("\"province\":\"").append(info.getProvince()).append("\",");
        senderBuilder.append("\"city\":\"").append(info.getCity()).append("\",");
        senderBuilder.append("\"district\":\"").append(info.getDistrict()).append("\",");
        senderBuilder.append("\"addr\":\"").append(info.getAddr()).append("\"");

        return "{" + senderBuilder + "}";
    }

    /**
     * 构建货物信息
     *
     * @param request 脱敏订单请求
     * @return 货物信息JSON
     */
    protected String buildCargoInfo(DesensitizedOrderRequest request) {
        DesensitizedOrderRequest.CargoInfo info = request.getCargoInfo();

        EOrderCargoInfo cargo = EOrderCargoInfo.builder()
                .name(info.getName())
                .count(info.getCount())
                .unit(info.getUnit())
                .weight(info.getWeight())
                .build();

        return JacksonUtil.toJson(cargo);
    }

    /**
     * 构建基础电子面单请求参数
     *
     * @param request 脱敏订单请求
     * @param recipientJson 收件人信息JSON
     * @param senderJson 寄件人信息JSON
     * @param cargoJson 货物信息JSON
     * @return 电子面单请求参数
     */
    protected EOrderRequestParam buildBaseOrderParam(
            DesensitizedOrderRequest request,
            String recipientJson,
            String senderJson,
            String cargoJson) {

        EOrderRequestParam param = new EOrderRequestParam();
        param.setCom(request.getExpressCom());
        param.setPrintType(request.getPrintType());
        param.setRecipient(recipientJson);
        param.setSender(senderJson);
        param.setCargo(cargoJson);
        param.setWeight(request.getWeight());
        param.setQuantity(request.getQuantity());
        param.setVolume(request.getVolume());
        param.setTemplateSize(request.getTemplateSize());
        param.setCallbackurl(request.getCallbackurl());
        param.setMonthCode(request.getMonthCode());
        param.setServiceType(request.getServiceType());
        param.setPayType(request.getPayType());
        param.setExpType(request.getExpType());
        param.setRemark(request.getRemark());

        return param;
    }
}