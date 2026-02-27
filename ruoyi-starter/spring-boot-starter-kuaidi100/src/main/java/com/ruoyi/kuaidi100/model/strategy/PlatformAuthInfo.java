package com.ruoyi.kuaidi100.model.strategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 平台授权信息
 *
 * @author kuaidi100
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformAuthInfo {

    /**
     * 合作伙伴ID
     */
    private String partnerId;

    /**
     * 合作伙伴密钥
     */
    private String partnerKey;

    /**
     * 合作伙伴名称
     */
    private String partnerName;

    /**
     * 网络标识
     */
    private String net;

    /**
     * 淘宝订单收件人ID（仅菜鸟淘宝需要）
     */
    private String oaid;
}