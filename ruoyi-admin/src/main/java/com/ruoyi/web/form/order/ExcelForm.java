package com.ruoyi.web.form.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("excel表单")
public class ExcelForm {

    @ApiModelProperty("品牌")
    private List<String> brandSet;


    @ApiModelProperty("分类")
    private List<String> categoryList;
}
