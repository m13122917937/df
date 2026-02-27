package com.ruoyi.kuaidi100.model.e;

import lombok.Data;

/**
 * 电子面单V2 请求参数主体
 */
@Data
public class EOrderRequestParam {
    private String com; // 快递公司编码
    private String sender; // 寄件人信息 JSON 字符串
    private String recipient; // 收件人信息 JSON 字符串
    private String cargo; // 物品信息 JSON 字符串
    private String weight; // 包裹重量
    private String quantity; // 包裹数量
    private String volume; // 包裹体积
    private String templateSize; // 面单模板ID
    private String callbackurl; // 回调地址
    private String monthCode; // 月结卡号
    private String serviceType; // 服务类型
    private String payType; // 支付方式
    private String expType; // 快递类型
    private String remark; // 备注
    private String printType; // 打印类型

    // ========== 第三方平台脱敏订单特有字段 ==========

    private String partnerId; // 合作伙伴ID
    private String partnerKey; // 合作伙伴密钥
    private String partnerName; // 合作伙伴名称
    private String net; // 网络标识
    private String thirdOrderId; // 平台订单ID
    private String oaid; // 淘宝订单收件人ID（仅菜鸟淘宝需要）
}