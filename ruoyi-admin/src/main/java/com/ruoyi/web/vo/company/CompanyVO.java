package com.ruoyi.web.vo.company;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data

public class CompanyVO {


    /**
     * $column.columnComment
     */

    private Long id;

    /**
     * 企业名称
     */

    private String companyName;

    /**
     * 企业别名
     */

    private String nickName;

    /**
     * 是否停款，默认0停款，1不停。
     */

    private Integer stopPurchase;

    /**
     * 账期类型（1:字典；2:自定义）
     */

    private Integer accountingPeriod;

    /**
     * 是否禁抢 ，0 未禁止 1 禁止
     */

    private Integer grabStatus;
    /**
     * 统一社会信用代码
     */

    private String creditCode;

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

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date establishedTime;

    /**
     * 营业执照相对路径
     */
    private String licenseUrl;

}
