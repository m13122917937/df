package com.ruoyi.user.model.query;

import com.ruoyi.framework.annotation.Operator;
import com.ruoyi.framework.annotation.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

import java.util.Date;
import java.util.Set;

/**
 * 【请填写功能名称】对象 u_company_bank
 *
 * @author ruoyi
 * @date 2025-09-28
 */
@Data
@Accessors(chain = true)
public class CompanyBankQuery {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 企业主键
     */
    private Long companyId;

    /**
     * 企业主键
     */
    @QueryField(operator = Operator.IN, field = "company_id")
    private Set<Long> companyIdSet;
    /**
     * 企业主体name
     */
    private String nickName;
    /**
     * 企业别称
     */
    private String companyName;
    /**
     * 银行开户名
     */
    private String accountBankName;
    /**
     * 税号
     */
    private String creditCode;
    /**
     * 开户银行
     */
    private String accountBank;
    /**
     * 银行账号
     */
    private String bankAccount;
    /**
     * 注册省
     */
    private Long province;
    /**
     * 注册市
     */
    private Long city;
    /**
     * 是否默认 0 否 1 是
     */
    private Integer defaulted;
    /**
     * 是否启用：0-禁用；1-启用
     */
    private Integer valid;
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
    private Integer deleted;


}
