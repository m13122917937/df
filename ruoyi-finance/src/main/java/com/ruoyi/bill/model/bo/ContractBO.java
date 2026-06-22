package com.ruoyi.bill.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 合同BO
 *
 * @author ruoyi
 * @date 2026-06-15
 */
@Data
@Accessors(chain = true)
public class ContractBO {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 合同编号
     */
    private String contractNo;
    /**
     * 合同名称
     */
    private String contractName;
    /**
     * 合同类型 1采购 2框架
     */
    private Integer contractType;
    /**
     * 我方主体（u_company.id）
     */
    private Long ourPayerId;
    /**
     * 供应商（u_company.id）
     */
    private Long vendorCompanyId;
    /**
     * e签宝签署流程ID
     */
    private String esignFlowId;
    /**
     * e签宝文件ID
     */
    private String esignFileId;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 签署完成时间
     */
    private Date signedTime;
    /**
     * 签署过期时间
     */
    private Date expireTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建人
     */
    private Long createBy;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 修改人
     */
    private Long updateBy;
}
