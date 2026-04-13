package com.ruoyi.order.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 成交订单对象 o_trade_order
 *
 * @author ruoyi
 * @date 2025-09-09
 */
@Data
@Accessors(chain = true)
public class TradeOrderQuery {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 订单id
     */
    private String orderId;

    /**
     * 订单id
     */
    @QueryField(field = "order_id" , operator = DynamicCondition.Operator.IN)
    private List<String> orderIdList;
    /**
     * 挂价单id
     */
    private Long hangOrderId;
    /**
     * 成交价格
     */
    private BigDecimal tradePrice;
    /**
     * 提交抢单的用户id
     */
    private Long tradeUserId;
    /**
     * 抢单人姓名
     */
    private String tradeUserName;

    /**
     * 抢单用户手机
     */
    private String tradeUserPhone;

    /**
     * 抢单用户公司名称
     */
    private String tradeCompanyName;

    /**
     * 抢单用户昵称
     */
    private String tradeNickName;

    /**
     * 成交的企业id
     */
    private Long tradeCompanyId;
    /**
     * 账期
     */
    private Long accountingPeriod;
    /**
     * 状态 1.待确认 2.已失效 3.已成交 4.被抢走
     */
    private Integer status;

    /**
     * 状态 1.待确认 2.已失效 3.已成交 4.被抢走
     */
    @QueryField(field = "status" , operator = DynamicCondition.Operator.IN)
    private List<Integer> statusList;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新者
     */
    private Long updateBy;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 报价商品名称
     */
    private String productName;
    /**
     * 品牌
     */
    private String brand;
    /**
     * 报价商品名称
     */
    private String skuName;
    /**
     * 报价商品名称
     */
    private String skuCode;
    /**
     * 省
     */
    private Long province;
    /**
     * 数量
     */
    private Long quantity;
    /**
     * 采购类型
     */
    private Long orderType;
    /**
     * 第几价成交
     */
    private Long tradeIndex;
    /**
     * 是否走电子面单 0,不走电子面单 1,走电子面单
     */
    private Long electronicSheet;

    /**
     * 快递单号
     */
    private String trackingNumber;

    /**
     * 快递公司
     */
    private String trackingCompany;

    /**
     * sn数量
     */
    private Integer snQuantity;


    /**
     * 创建时间
     */
    private Date startCreateTime;

    /**
     * 创建时间
     */
    private Date endCreateTime;

    /**
     * 送货码
     */
    private Integer deliveryCode;
}
