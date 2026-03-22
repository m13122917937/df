package com.ruoyi.web.form.index;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data

public class ProductForm {


    @NotBlank(message = "tabName不能为空")
    private String tabName;


    private Long province;


    private Long city;


    @Length(min = 0, message = "产品名称不能超过30个字符", max = 30)
    private String productName;


    private String skuName;


    private Integer orderStyle;

}

