package com.ruoyi.web.form.rule;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@ApiModel("规则查询参数")
@Accessors(chain = true)
public class RuleQueryForm {


    /**
     * 省
     */
    @ApiModelProperty("省")
    private Long province;

    @ApiModelProperty("品牌")
    private String brand;

    @ApiModelProperty("类别-分类")
    private String category;

    /**
     * 商品编码
     */
    @ApiModelProperty("商品编码")
    private String skuCode;


    @ApiModelProperty(value = "开始时间", hidden = true)
    private Date gtCreateTime;


    @ApiModelProperty(value = "规则范围, 0 省 1 国  2店铺 3 平台", hidden = true)
    private Integer ruleRange;

    /**
     * 平台
     */
    @ApiModelProperty(value = "平台", hidden = true)
    private String platform;


    /**
     * 店铺
     */
    @ApiModelProperty(value = "店铺", hidden = true)
    private String shopName;

}
