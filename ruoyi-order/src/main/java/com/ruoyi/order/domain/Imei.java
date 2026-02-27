package com.ruoyi.order.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 订单串码对象 o_imei
 *
 * @author ruoyi
 * @date 2025-09-11
 */
@Data
@Accessors(chain = true)
@TableName("o_imei")
public class Imei {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
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
     * 商品串码
     */
    private String imel;
    /**
     * 订单id
     */
    private String orderId;
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
     * $column.columnComment
     */
    @TableLogic
    private Integer deleted;


}
