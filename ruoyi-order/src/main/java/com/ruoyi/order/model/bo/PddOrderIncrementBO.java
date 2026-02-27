package com.ruoyi.order.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 拼多多增量订单业务对象
 *
 * @author ruoyi
 * @date 2025-02-08
 */
@Data
@Accessors(chain = true)
public class PddOrderIncrementBO {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 付款主体ID
     */
    private Long payerConfigId;

    /**
     * 付款主体名称
     */
    private String payerName;


    /**
     *
     */
    private Integer orderTag ;


    /**
     * 收件人姓名
     */
    private String receiverName;

    /**
     * 收件人手机号
     */
    private String receiverPhone;

    /**
     * 收件人完整地址
     */
    private String receiverAddress;


    /**
     * 创建时间
     */
    private Date createTime;

    // ========== 拼多多脱敏订单打印相关字段 ==========

    /**
     * 收件人加密姓名
     */
    private String encryptedName;

    /**
     * 收件人加密电话
     */
    private String encryptedMobile;

    /**
     * 收件人省份
     */
    private String province;

    /**
     * 收件人城市
     */
    private String city;

    /**
     * 收件人区/县
     */
    private String district;

    /**
     * 收件人加密详细地址
     */
    private String encryptedAddr;

    /**
     * 收件人加密详细地址（完整版，用于detailAddrEnc）
     */
    private String detailAddrEnc;

    /**
     * 快递公司编码（用于打印面单）
     */
    private String expressCom;

    /**
     * 商品名称（用于打印面单）
     */
    private String cargoName;

    /**
     * 商品数量（用于打印面单）
     */
    private Integer cargoCount;

    /**
     * 商品单位（用于打印面单）
     */
    private String cargoUnit;

    /**
     * 商品重量（用于打印面单）
     */
    private String cargoWeight;

    // ========== 平台授权信息（用于打印面单）==========

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

}