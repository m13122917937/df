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

    @QueryField(operator = Operator.LIKE, field = "nickName")
    private String nickNameLike;

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
    private Date establishment;

    /**
     * 合同认证状态（0:未认证 1:已认证）
     */
    private Integer contractAuthStatus;

    private String creator;


    private Long creatorId;




}
