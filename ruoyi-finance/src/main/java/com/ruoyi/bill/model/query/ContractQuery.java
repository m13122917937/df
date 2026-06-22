package com.ruoyi.bill.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 合同 Query
 *
 * @author ruoyi
 * @date 2026-06-15
 */
@Data
@Accessors(chain = true)
public class ContractQuery {
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 合同编号（精确）
     */
    private String contractNo;

    /**
     * 合同名称（模糊）
     */
    @QueryField(operator = DynamicCondition.Operator.LIKE, field = "contract_name")
    private String contractNameLike;

    /**
     * 合同类型 1采购 2框架
     */
    private Integer contractType;

    /**
     * 合同类型集合（多选）
     */
    @QueryField(operator = DynamicCondition.Operator.IN, field = "contract_type")
    private List<Integer> contractTypeList;

    /**
     * 我方主体
     */
    private Long ourPayerId;

    /**
     * 供应商
     */
    private Long vendorCompanyId;

    /**
     * e签宝流程ID
     */
    private String esignFlowId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 状态集合（多选）
     */
    @QueryField(operator = DynamicCondition.Operator.IN, field = "status")
    private List<Integer> statusList;

    /**
     * 创建时间起
     */
    @QueryField(operator = DynamicCondition.Operator.GTE, field = "create_time")
    private Date gteCreateTime;

    /**
     * 创建时间止
     */
    @QueryField(operator = DynamicCondition.Operator.LTE, field = "create_time")
    private Date lteCreateTime;

    /**
     * 签署完成时间起
     */
    @QueryField(operator = DynamicCondition.Operator.GTE, field = "signed_time")
    private Date gteSignedTime;

    /**
     * 签署完成时间止
     */
    @QueryField(operator = DynamicCondition.Operator.LTE, field = "signed_time")
    private Date lteSignedTime;

    private Long createBy;
}
