package com.ruoyi.jky.param.fin;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 开放平台收付款单创建参数
 */
@Data
@Accessors(chain = true)
public class CreateCashOrCostRecPayBillParam {

    /** 单据来源，2-手工新建 */
    private Integer billSource;

    /** 公司名称 */
    private String companyName;

    /** 公司币种编码 */
    private String companyFcCode;

    /** 公司币种名称 */
    private String companyFcName;

    /** 单据日期 */
    private String billDate;

    /** 结算账户币种编码 */
    private String fcCode;

    /** 结算账户币种名称 */
    private String fcName;

    /** 单据号 */
    private String oriBillNum;

    /** 摘要 */
    private String textLine;

    /** 总金额 */
    private BigDecimal tranAmountSum;

    /** 本币总金额 */
    private BigDecimal baseAmountSum;

    /** 汇率 */
    private BigDecimal fcRate;

    /** 结算账户 ID */
    private Integer settAccId;

    /** 结算账户名称 */
    private String settAccName;

    /** 账户类型编码 */
    private String bankAccount;

    /** 账户类型名称 */
    private String bankAccountName;

    /** 客户往来单位编码 */
    private String unitCode;

    /** 公司编码 */
    private String companyCode;

    /** 公司 ID */
    private Long companyId;

    /** 单据类型：11-收款单，12-付款单 */
    private Integer billType;

    /** 是否重复性校验 */
    private Integer isCheckRepeat;

    /** 业务明细列表 */
    private List<BillTransDetailCreateDto> billTransDetailCreateDtoList;

    /** 多种结算详情 */
    private List<BankAccountVO> bankAccountVOS;

    @Data
    @Accessors(chain = true)
    public static class BillTransDetailCreateDto {

        /** 含税金额 */
        private BigDecimal trTaxValue;

        /** 税率 */
        private BigDecimal taxRate;

        /** 税额 */
        private BigDecimal trTax;

        /** 借贷方向：1-借，2-贷 */
        private Integer jd;

        /** 其他收支项目编码 */
        private String itemCode;

        /** 其他收支项目名称 */
        private String itemName;

        /** 摘要 */
        private String textLine;

        /** 客户 ID */
        private Long custId;

        /** 客户名称 */
        private String custName;

        /** 供应商 ID */
        private Long vendId;

        /** 供应商名称 */
        private String vendName;

        /** 员工 ID */
        private Long userId;

        /** 员工名称 */
        private String userName;

        /** 部门 ID */
        private Long deptId;

        /** 部门名称 */
        private String deptName;

        /** 核算项目 ID */
        private Long projId;

        /** 核算项目名称 */
        private String projName;

        /** 销售渠道 ID */
        private Long shopId;

        /** 销售渠道名称 */
        private String shopName;

        /** 业务员 ID */
        private Long sellerId;

        /** 业务员 */
        private String seller;

        /** 销售团队 ID */
        private Long salesId;

        /** 销售团队名称 */
        private String salesName;

        /** 销售/采购订单号 */
        private String valueb;

        /** 网店订单号 */
        private String onlineShopTradeNo;
    }

    @Data
    @Accessors(chain = true)
    public static class BankAccountVO {

        /** 账户类型编码 */
        private String bankAccount;

        /** 账户类型名称 */
        private String bankAccountName;

        /** 结算账户 ID */
        private Integer settAccId;

        /** 结算账户名称 */
        private String settAccName;

        /** 金额 */
        private BigDecimal trValue;
    }
}
