package com.ruoyi.web.form.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("串码表单")
public class ImeiForm {

    @ApiModelProperty("品牌")
    private String brand;

    @ApiModelProperty("类别")
    private String category;

    @ApiModelProperty("产品名称")
    private String productNameLike;

    @ApiModelProperty("状态 1待处理 2已确认 3已拒绝")
    private Integer status;

}
