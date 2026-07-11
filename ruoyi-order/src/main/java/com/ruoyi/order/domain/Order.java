package com.ruoyi.order.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("o_order")
public class Order {
    private static final long serialVersionUID = 1L;

    /**
     * 订单编码
     */
    @TableId(type = IdType.INPUT)
    private String orderCode;
    /**
     * 管家订单号
     */
    private String erpOrderId;
    /**
     * 吉客云唯一单号（jy开头）
     */
    private String jkyTradeNo;
    /**
     * 原始订单号
     */
    private String originalOrderId;
    /**
     * 采购类型（1:入仓，2:一件代发）
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
     * 地址状态 1：未补 2：已申请补地址 3：已成功补地址 4：补地址失败
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
    private Long quantity;

    /**
     * 付款主体id
     */
    private Long payerId;

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
    private Long subStatus;
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
    @TableLogic
    private Long deleted;
    /**
     * 是否需要处理申请（0 不需要 1 需要）
     */
    private Long handleApply;



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
