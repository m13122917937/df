package com.ruoyi.web.form.index;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("产品表单")
public class ProductForm {

    @ApiModelProperty("tab")
    @NotBlank(message = "tabName不能为空")
    private String tabName;

    @ApiModelProperty("省")
    private Long province;

    @ApiModelProperty("市")
    private Long city;

    @ApiModelProperty("商品名称")
    @Length(min = 0, message = "产品名称不能超过30个字符", max = 30)
    private String productName;

    @ApiModelProperty("产品名称")
    private String skuName;

    @ApiModelProperty("订单类型")
    private Integer orderStyle;

}

