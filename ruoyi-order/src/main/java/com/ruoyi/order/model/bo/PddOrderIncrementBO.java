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

}