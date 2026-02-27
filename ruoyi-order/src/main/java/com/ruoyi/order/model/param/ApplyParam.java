package com.ruoyi.order.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

import java.util.Date;

/**
 * 【请填写功能名称】对象 o_apply
 *
 * @author ruoyi
 * @date 2025-09-23
 */
@Data
@Accessors(chain = true)
public class ApplyParam {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 订单主键
     */
    private String orderId;
    /**
     * 挂单主键
     */
    private Long hangOrderId;
    /**
     * 申请企业
     */
    private Long companyId;
    /**
     * 申请类型(1立即毁单 2免责毁单 4修改物流单号申请 )
     */
    private Integer type;
    /**
     * 审批状态 1 申请中 2 已拒绝 3 已审批
     */
    private Integer status;
    /**
     * 申请理由
     */
    private String remark;
    /**
     * 创建人
     */
    private Long createBy;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改人
     */
    private Long updateBy;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 是否作废
     */
    private Integer deleted;
    /**
     * 新物流公司编码
     */
    private String logisticsCode;
    /**
     * 新物流单号
     */
    private String logisticsNo;
    /**
     * 拒绝理由
     */
    private String refuseRemark;
    /**
     * 手机尾号
     */
    private String phoneSuffix;
    /**
     * 取消原因
     */
    private Long reasonType;
    /**
     * o_hanging_order[lastCompeteCompany] 字段对应的省份ID
     */
    private Long lastProvinceId;
    /**
     * 自动拒绝毁单 1是 0否
     */
    private Integer autoReject;
    /**
     * 自动拒绝毁单时间
     */
    private Date autoRejectTime;
    /**
     * 自动拒绝毁单预警时间
     */
    private Date warningTime;


}
