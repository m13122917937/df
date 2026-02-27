package com.ruoyi.order.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 拼多多增量订单对象 pdd_order_increment
 *
 * @author ruoyi
 * @date 2025-02-08
 */
@Data
@Accessors(chain = true)
@TableName("pdd_order_increment")
public class PddOrderIncrement {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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
     *
     */
    private Integer orderTag ;


    /**
     * 创建时间
     */
    private Date createTime;


    /**
     * 是否被删除 0 未删除 1 已删除
     */
    @TableLogic
    private Integer deleted;
}