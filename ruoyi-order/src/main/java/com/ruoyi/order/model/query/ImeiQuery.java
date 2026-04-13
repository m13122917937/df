package com.ruoyi.order.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 订单串码对象 o_imei
 *
 * @author ruoyi
 * @date 2025-09-11
 */
@Data
@Accessors(chain = true)
public class ImeiQuery {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private Long id;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 型号
     */
    private String skuName;
    /**
     * sku编码
     */
    private String skuCode;
    /**
     * 商品串码
     */
    private String sn;
    /**
     * 86码
     */
    private String imel;
    /**
     * 订单id
     */
    private String orderId;

    /**
     * 订单id
     */
    @QueryField(operator = DynamicCondition.Operator.NE, field = "order_id")
    private String notEqOrderId;

    /**
     * 订单id
     */
    @QueryField(operator = DynamicCondition.Operator.IN, field = "order_id")
    private List<String> orderIdList;

    /**
     * 原始订单号
     */
    private Long tradeNo;
    /**
     * 挂单id
     */
    private Long hangingOrderId;
    /**
     * 激活状态
     */
    private Integer activated;

    /**
     * 激活状态
     */
    @QueryField(operator = DynamicCondition.Operator.NE, field = "activated")
    private Integer noActivated;

    /**
     * 激活状态
     */
    private Date activatedTime;
    /**
     * 平台效验状态
     */
    private Integer platformImei;
    /**
     * 平台效验时间
     */
    private Date platformTime;
    /**
     * 创建时间
     */
    private Date createTime;


    /**
     * 创建时间
     */
    @QueryField(operator = DynamicCondition.Operator.GT, field = "create_time")
    private Date gtCreateTime;

    /**
     * $column.columnComment
     */
    private Integer deleted;


}
