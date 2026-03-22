package com.ruoyi.web.form.rule;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data

@Accessors(chain = true)
public class RuleQueryForm {


    /**
     * 省
     */

    private Long province;


    private String brand;


    private String category;

    /**
     * 商品编码
     */

    private String skuCode;



    private Date gtCreateTime;



    private Integer ruleRange;

    /**
     * 平台
     */

    private String platform;


    /**
     * 店铺
     */

    private String shopName;

}
