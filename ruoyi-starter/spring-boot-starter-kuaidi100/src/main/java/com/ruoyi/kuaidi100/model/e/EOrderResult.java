package com.ruoyi.kuaidi100.model.e;

import lombok.Data;

/**
 * 电子面单V2 响应结果模型
 */
@Data
public class EOrderResult {
    private String result; // 请求结果 true/false
    private String returnCode; // 返回码
    private String message; // 返回消息
    private String kuaidinum; // 快递单号
    private String orderId; // 订单ID
    private String pdf; // PDF面单URL
    private String qrCode; // 二维码内容
    private String html; // HTML面单内容
    // ... 根据实际返回结构添加更多字段
}