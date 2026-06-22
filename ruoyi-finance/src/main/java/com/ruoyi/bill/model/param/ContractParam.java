package com.ruoyi.bill.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 合同 Param
 *
 * @author ruoyi
 * @date 2026-06-15
 */
@Data
@Accessors(chain = true)
public class ContractParam {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String contractNo;
    private String contractName;
    private Integer contractType;
    private Long ourPayerId;
    private Long vendorCompanyId;
    private String esignFlowId;
    private String esignFileId;
    private Integer status;
    private Date signedTime;
    private Date expireTime;
    private String remark;
    private Date createTime;
    private Long createBy;
    private Date updateTime;
    private Long updateBy;
}
