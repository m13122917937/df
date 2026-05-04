package com.ruoyi.user.model.bo;

import lombok.Data;

import java.util.Date;

@Data
public class CompanyBO {


    /** $column.columnComment */
    private Long id;

    /** 企业名称 */
    private String companyName;

    /** 企业别名 */
    private String nickName;
    /**
     * 账期类型（1:字典；2:自定义）
     */
    private Integer accountingPeriod;


    /**
     * 是否禁抢 ，0 未禁止 1 禁止
     */
    private Integer grabStatus;

    /** 是否停款，默认0停款，1不停。 */
    private Integer stopPurchase;

    /** 对外的编码 */
    private String outNo;

    /** 统一社会信用代码 */
    private String creditCode;

    /** 法人名称 */
    private String corporateName;

    /** 省 */
    private String province;

    /** 省id */
    private Long provinceId;

    /** 市 */
    private String city;

    /** 市id */
    private Long cityId;

    /** 企业成立时间 */
    private Date establishment;

    /**
     * 合同认证状态（0:未认证 1:认证中 2:认证成功 3:认证失败 4:认证取消 */
    private Integer contractAuthStatus ;

    /**
     * 是否负责人
     */
    private Integer owner;

}
