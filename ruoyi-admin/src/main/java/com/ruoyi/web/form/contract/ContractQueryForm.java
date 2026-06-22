package com.ruoyi.web.form.contract;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 合同列表查询表单
 *
 * @author ruoyi
 * @date 2026-06-15
 */
@Data
@Accessors(chain = true)
public class ContractQueryForm {

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 合同名称（模糊）
     */
    private String contractName;

    /**
     * 合同类型集合（多选）
     */
    private List<Integer> contractTypeList;

    /**
     * 状态集合（多选）
     */
    private List<Integer> statusList;

    /**
     * 我方主体
     */
    private Long ourPayerId;

    /**
     * 供应商
     */
    private Long vendorCompanyId;

    /**
     * 创建时间起
     */
    private Date createTimeStart;

    /**
     * 创建时间止
     */
    private Date createTimeEnd;

    /**
     * 签署完成时间起
     */
    private Date signedTimeStart;

    /**
     * 签署完成时间止
     */
    private Date signedTimeEnd;
}
