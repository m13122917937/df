package com.ruoyi.web.vo.bill;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class DeductionInfoVO {

//    @ApiModelProperty("平台")
//    @ExcelProperty(value = "平台")
//    private String platform;


    /**
     * 店铺名
     */
    @ApiModelProperty("店铺名")
    @ExcelProperty(value = "店铺名")
    private String shopName;

    /**
     * 产品型号
     */
    @ApiModelProperty("产品型号")
    @ExcelProperty(value = "商品名")
    private String productName;
    /**
     * 产品型号
     */
    @ApiModelProperty("产品型号")
    @ExcelProperty(value = "产品型号")
    private String skuName;

    @ApiModelProperty("企业名称")
    @ExcelProperty(value = "抢单企业名称")
    private String companyName;

    /**
     * 最晚发货时间
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("发货时间")
    @ExcelProperty(value = "发货时间")
    private Date sendTime;
}
