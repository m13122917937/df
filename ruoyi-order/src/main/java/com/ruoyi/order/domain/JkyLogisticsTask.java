package com.ruoyi.order.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 吉客云物流更新延迟任务对象 jky_logistics_task
 *
 * @author ruoyi
 * @date 2026-06-24
 */
@Data
@Accessors(chain = true)
@TableName("jky_logistics_task")
public class JkyLogisticsTask {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private String orderCode;

    /**
     * ERP订单号
     */
    private String erpOrderId;

    /**
     * 物流单号
     */
    private String logisticsNo;

    /**
     * 物流公司名称
     */
    private String logisticsName;

    /**
     * 物流公司编码
     */
    private String logisticsCode;

    /**
     * 状态:0=待处理 1=成功 2=失败
     */
    private Integer status;

    /**
     * 计划执行时间
     */
    private Date executeTime;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;
}
