package com.ruoyi.web.form.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.framework.annotation.Operator;
import com.ruoyi.framework.annotation.QueryField;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OrderNewForm {


    private Long province;


    private String brand;


    private String originalOrderId;


    private Integer status;


    private List<String> orderCodeList;


    private List<String> originalOrderIdList;


    private String category;


    private String shopName;


    private String productNameLike;


    private String skuNameLike;


    private Long companyId;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastShippingTimeStart;

    /**
     * 最晚发货时间
     */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastShippingTimeEnd;


    private Integer orderType;
//
//
//    private String skuCode;
//
//
//    private List<Integer> statusList;

//    /**
//     * NOT_SUPPLEMENTED(1, "未补"),
//     * <p>
//     * SUCCESS(2, "已成功补地址");
//     */
//
//    private Integer addressStatus;

//
//
//    private List<Integer> subStatusList;
}
