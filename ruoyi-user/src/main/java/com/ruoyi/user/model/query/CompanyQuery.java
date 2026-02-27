package com.ruoyi.user.model.query;

import com.ruoyi.framework.annotation.Operator;
import com.ruoyi.framework.annotation.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class CompanyQuery {

    @QueryField(operator = Operator.LIKE, field = "companyName")
    private String companyNameLike;

    private Long id;

    private String companyName;

    private String nickName;

    /**
     * 账期类型（1:字典；2:自定义）
     */
    private Integer accountingPeriod;


    private Integer stopPurchase;

    private String outNo;

    private String creditCode;

    /**
     * 是否禁抢 ，0 未禁止 1 禁止
     */
    private Integer grabStatus;

    /**
     * 法人名称
     */
    private String corporateName;

    /**
     * 省
     */
    private String province;

    /**
     * 省id
     */
    private Long provinceId;

    /**
     * 市
     */
    private String city;

    /**
     * 市id
     */
    private Long cityId;

    /**
     * 企业成立时间
     */
    private Date establishedTime;

    /**
     * 营业执照相对路径
     */
    private String licenseUrl;

    private String creator;


    private Long creatorId;




}
