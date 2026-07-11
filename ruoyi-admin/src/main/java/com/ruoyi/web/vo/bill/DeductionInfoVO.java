package com.ruoyi.web.vo.bill;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class DeductionInfoVO {

//
//    @ExcelProperty(value = "平台")
//    private String platform;


    /**
     * 店铺名
     */

    @ExcelProperty(value = "店铺名")
    private String shopName;

    /**
     * 产品型号
     */

    @ExcelProperty(value = "商品名")
    private String productName;
    /**
     * 产品型号
     */

    @ExcelProperty(value = "产品型号")
    private String skuName;


    @ExcelProperty(value = "抢单企业名称")
    private String companyName;

    /**
     * 最晚发货时间
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    @ExcelProperty(value = "发货时间")
    private Date sendTime;

    /**
     * 串码（SN）
     */
    private String sn;

    /**
     * 86码/IMEI
     */
    private String imei;
}
