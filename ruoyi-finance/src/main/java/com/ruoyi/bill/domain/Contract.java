package com.ruoyi.bill.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 合同对象 f_contract
 *
 * @author ruoyi
 * @date 2026-06-15
 */
@Data
@Accessors(chain = true)
@TableName("f_contract")
public class Contract {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 合同编号 HT{yyyyMMdd}{6位流水}
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
     * 状态 0草稿 1签署中 2已完成 3已拒签 4已过期 5已撤销
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
    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;
}
