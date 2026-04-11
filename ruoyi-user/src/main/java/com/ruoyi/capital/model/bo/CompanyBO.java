package com.ruoyi.capital.model.bo;

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

    /** 是否认证成功（0.认证通过 1.信息验证 2.打款验证） */
    private Integer auth;

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

}
