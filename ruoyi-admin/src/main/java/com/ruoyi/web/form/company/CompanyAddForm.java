package com.ruoyi.web.form.company;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;


@Data
public class CompanyAddForm {


    @NotNull(groups = UpdateGroup.class)
    private Long id;


    private String companyNameLike;


    @NotNull(groups = AddGroup.class, message = "企业名称不能为空")
    private String companyName;


    @NotNull(groups = AddGroup.class, message = "企业别名不能为空")
    private String nickName;


    @NotNull(groups = AddGroup.class, message = "是否停款不能为空")
    private Integer stopPurchase;

    /**
     * 账期类型（1:字典；2:自定义）
     */

    private Integer accountingPeriod;

    /**
     * 是否禁抢 ，0 未禁止 1 禁止
     */

    private Integer grabStatus;




    private String outNo;


    @NotNull(groups = AddGroup.class, message = "统一社会信用代码不能为空")
    private String creditCode;

    /**
     * 法人名称
     */

    @NotNull(groups = AddGroup.class, message = "法人名称不能为空")
    private String corporateName;

    /**
     * 省
     */
    private String province;

    /**
     * 省id
     */
    @NotNull(groups = AddGroup.class)
    private Long provinceId;

    /**
     * 市
     */
    private String city;

    /**
     * 市id
     */
    @NotNull(groups = AddGroup.class)
    private Long cityId;

    /**
     * 企业成立时间
     */
    @NotNull(groups = AddGroup.class)

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date establishedTime;


}
