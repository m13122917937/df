package com.ruoyi.web.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("企业")
public class CompanyVO {


    /**
     * $column.columnComment
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 企业名称
     */
    @ApiModelProperty("企业名称")
    private String companyName;

    /**
     * 企业别名
     */
    @ApiModelProperty("企业别名")
    private String nickName;


    @ApiModelProperty("是否是当前登录企业")
    private Boolean curr;


    @ApiModelProperty("是否是主账号")
    private Integer owner;

    @ApiModelProperty("是否禁抢 ，0 未禁止 1 禁止")
    /**
     * 是否禁抢 ，0 未禁止 1 禁止
     */
    private Integer grabStatus;

}
