package com.ruoyi.order.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 拼多多增量订单参数对象
 *
 * @author ruoyi
 * @date 2025-02-08
 */
@Data
@Accessors(chain = true)
public class PddOrderIncrementParam {
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



}