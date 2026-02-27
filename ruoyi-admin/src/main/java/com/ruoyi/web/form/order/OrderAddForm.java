package com.ruoyi.web.form.order;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.validator.group.AddGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ApiModel("订单实体")
public class OrderAddForm {

    /**
     * 管家订单号
     */
    @NotBlank(message = "管家订单号不能为空", groups = AddGroup.class)
    @ApiModelProperty(value = "管家订单号", required = true)
    @ExcelProperty(value = "订单编号")
    private String erpOrderId;
    /**
     * 原始订单号
     */
    @NotBlank(message = "原始订单号不能为空", groups = AddGroup.class)
    @ApiModelProperty(value = "原始订单号", required = true)
    @ExcelProperty(value = "原始单号")
    private String originalOrderId;

    /**
     * 店铺名
     */
    @NotBlank(message = "店铺名不能为空", groups = AddGroup.class)
    @ApiModelProperty(value = "店铺名", required = true)
    @ExcelProperty(value = "店铺")
    private String shopName;

    /**
     * sku编码
     */
    @NotBlank(message = "sku编码不能为空", groups = AddGroup.class)
    @ApiModelProperty(value = "sku编码", required = true)
    @ExcelProperty(value = "货品商家编码")
    private String skuCode;
    /**
     * 数量
     */
    @NotNull(message = "数量不能为空", groups = AddGroup.class)
    @ApiModelProperty(value = "数量", required = true)
    @ExcelProperty(value = "原始货品数量")
    private Long quantity;
    /**
     * 平台
     */
    @NotBlank(message = "平台不能为空", groups = AddGroup.class)
    @ApiModelProperty(value = "平台", required = true)
    @ExcelProperty(value = "平台类型")
    private String platform;

    /**
     * 管家交易时间
     */
    @NotNull(message = "管家交易时间不能为空", groups = AddGroup.class)
    @ApiModelProperty(value = "管家交易时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "下单时间")
    private Date erpTradeTime;
    /**
     * 最晚发货时间
     */
    @NotNull(message = "最晚发货时间不能为空", groups = AddGroup.class)
    @ApiModelProperty(value = "最晚发货时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = "最晚发货时间")
    @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
    private Date lastShippingTime;


    @NotBlank(message = "地址不能为空", groups = AddGroup.class)
    @ApiModelProperty(value = "客服备注", required = true)
    @ExcelProperty(value = "客服备注")
    private String address;


    @ApiModelProperty(value = "收件人", required = true)
    @ExcelIgnore
    private String addressee;

    @ApiModelProperty(value = "收件人手机号码", required = true)
    @ExcelIgnore
    private String phone;


    @ApiModelProperty(value = "收件人手机号码", required = true)
    @ExcelProperty(value = "平台标签")
    private String orderStyle;
}
