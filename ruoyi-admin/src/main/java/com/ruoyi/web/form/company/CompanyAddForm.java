package com.ruoyi.web.form.company;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel(description = "企业查询实体")
@Data
public class CompanyAddForm {

    @ApiModelProperty("id")
    @NotNull(groups = UpdateGroup.class)
    private Long id;

    @ApiModelProperty("企业名称模糊查询")
    private String companyNameLike;

    @ApiModelProperty("企业名称")
    @NotNull(groups = AddGroup.class, message = "企业名称不能为空")
    private String companyName;

    @ApiModelProperty("企业别名")
    @NotNull(groups = AddGroup.class, message = "企业别名不能为空")
    private String nickName;

    @ApiModelProperty("是否停款，默认0停款，1不停。")
    @NotNull(groups = AddGroup.class, message = "是否停款不能为空")
    private Integer stopPurchase;

    /**
     * 账期类型（1:字典；2:自定义）
     */
    @ApiModelProperty("账期类型, 0 现款 ， 1 T+1 ， 2 T+2")
    private Integer accountingPeriod;

    /**
     * 是否禁抢 ，0 未禁止 1 禁止
     */
    @ApiModelProperty("是否禁抢 ，0 未禁止 1 禁止")
    private Integer grabStatus;



    @ApiModelProperty("对外的编码")
    private String outNo;

    @ApiModelProperty("统一社会信用代码")
    @NotNull(groups = AddGroup.class, message = "统一社会信用代码不能为空")
    private String creditCode;

    /**
     * 法人名称
     */
    @ApiModelProperty("法人名称")
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
    @ApiModelProperty("企业成立时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date establishedTime;


}
