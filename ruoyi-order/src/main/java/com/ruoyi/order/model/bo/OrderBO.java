package com.ruoyi.order.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 订单对象 o_order
 *
 * @author ruoyi
 * @date 2025-09-09
 */
@Data
@Accessors(chain = true)
public class OrderBO {
    private static final long serialVersionUID = 1L;

    /**
     * 订单编码
     */
    private String orderCode;
    /**
     * 管家订单号
     */
    private String erpOrderId;
    /**
     * 原始订单号
     */
    private String originalOrderId;
    /**
     * 采购类型（1:批量采购，2:一件代发,3:接龙抢单,4:群接龙,5:采购入仓,6:预付采购）
     */
    private Integer orderType;

    /**
     * 店铺名
     */
    private String shopName;
    /**
     * 收件人
     */
    private String addressee;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 收货地址
     */
    private String receivingAddress;
    /**
     * 省
     */
    private Long province;
    /**
     * 市
     */
    private Long city;
    /**
     * 区
     */
    private Long area;
    /**
     * 地址状态 1：未补 2：已成功补地址
     */
    private Integer addressStatus;
    /**
     * 类别
     */
    private String category;
    /**
     * 品牌
     */
    private String brand;
    /**
     * 系列名称
     */
    private String seriesName;
    /**
     * 产品型号
     */
    private String productName;
    /**
     * 产品型号
     */
    private String skuName;
    /**
     * sku编码
     */
    private String skuCode;
    /**
     * 数量
     */
    private Integer quantity;
    /**
     * 付款主体简称
     */
    private String payerName;
    /**
     * 平台
     */
    private String platform;
    /**
     * 状态(1: 未报价；2: 待提交；3:报价中；4:待发货；5: 已发货；6:已完成；7：退货退单；8：待补全地址；9：串码异常；10：售后；11：在途；12：物流异常；13：撤销；14：未追回订单)
     */
    private Integer status;
    /**
     * 子状态（20:新建；21:撤销；22:超时发货；23:超时流拍，30:待抢单 ，31:待确认）
     */
    private Integer subStatus;
    /**
     * 管家交易时间
     */
    private Date erpTradeTime;
    /**
     * 最晚发货时间
     */
    private Date lastShippingTime;

    /**
     * 发货时间
     */
    private Date sendTime;
    /**
     * 快递发货时间
     */
    private Date shipmentsTime;
    /**
     * 签收时间
     */
    private Date signedTime;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否被删除 0 未删除 1 已删除
     */
    private Long deleted;
    /**
     * 是否需要处理申请（0 不需要 1 需要）
     */
    private Long handleApply;

    /**
     * 结算方式 0-后排款 1-预充值 2-订单预付 3-平台分账
     */
    private Long settlementType;


    /**
     * 撤销类型 撤销原因 0，店铺后台急速退款； 1 顾客拒签/拒收； 2 派送未联系到顾客，退回， 3 24小时物流无揽收信息; 4 供应商私自拦截 5 已经从其他渠道发货
     */
    private Integer revokeType;

    /**
     *
     */
    private Integer orderStyle;

    /**
     * 订单备注
     */
    private String remark;
}
