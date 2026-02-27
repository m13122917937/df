package com.ruoyi.web.form.order;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.web.vo.bill.converter.AccountingConvert;
import com.ruoyi.web.vo.order.converter.OrderStyleConvert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("订单信息")
public class OrderImportVO {


    @ApiModelProperty("订单编号")
    @ExcelProperty(value = "订单编号")
    private String orderCode;

    @ApiModelProperty("旺店通单号")
    @ExcelProperty(value = "旺店通单号")
    private String erpOrderId;

    @ApiModelProperty("物流公司")
    @ExcelProperty(value = "物流公司")
    private String express;

    @ApiModelProperty("寄件人手机号后四位")
    @ExcelProperty(value = "寄件人手机号后四位")
    private String sendPhone;

    @ExcelProperty(value = "串码")
    @ApiModelProperty("串码")
    private String snList;


    @ExcelProperty(value = "序列号SN")
    @ApiModelProperty("序列号SN")
    private String imeiList;

    @ApiModelProperty("物流单号")
    @ExcelProperty(value = "物流单号")
    private String expressNO;

}
