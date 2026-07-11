package com.ruoyi.order.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 吉客云物流更新延迟任务BO
 *
 * @author ruoyi
 * @date 2026-06-24
 */
@Data
@Accessors(chain = true)
public class JkyLogisticsTaskBO {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String orderCode;

    private String erpOrderId;

    private String logisticsNo;

    private String logisticsName;

    private String logisticsCode;

    private Integer status;

    private Date executeTime;

    private Integer retryCount;

    private String errorMsg;

    private Date createTime;

    private Date updateTime;
}
