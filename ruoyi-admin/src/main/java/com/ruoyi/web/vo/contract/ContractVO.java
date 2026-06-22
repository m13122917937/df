package com.ruoyi.web.vo.contract;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 合同 VO
 *
 * @author ruoyi
 * @date 2026-06-15
 */
@Data
public class ContractVO {

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
     * 我方主体名称
     */
    private String ourPayerName;

    /**
     * 供应商（u_company.id）
     */
    private Long vendorCompanyId;

    /**
     * 供应商名称
     */
    private String vendorCompanyName;

    /**
     * e签宝流程ID
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date signedTime;

    /**
     * 签署过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    /**
     * 备注
     */
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private Long createBy;

    private String createName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private Long updateBy;

    private String updateName;
}
