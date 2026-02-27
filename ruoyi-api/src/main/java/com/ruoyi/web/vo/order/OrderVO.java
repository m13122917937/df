package com.ruoyi.web.vo.order;

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
@Accessors(chain = true)
@ApiModel("订单信息")
public class OrderVO {


    @ApiModelProperty("订单编号")
    @ExcelProperty(value = "订单编号")
    private String orderCode;

    @ApiModelProperty("旺店通单号")
    @ExcelProperty(value = "旺店通单号")
    private String erpOrderId;

    @ApiModelProperty("订单类型： 0 百亿 1 百亿微派'")
    @ExcelProperty(value = "订单类型", converter = OrderStyleConvert.class)
    private Integer orderStyle;

    @ExcelProperty(value = "付款主体简称")
    @ApiModelProperty("付款主体简称")
    private String payerName;


    @ApiModelProperty("抢单人")
    @ExcelProperty(value = "抢单人")
    private String tradeUserName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("抢单时间")
    @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "抢单时间")
    private Date tradeTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelIgnore
    @ApiModelProperty("发货时间")
    private Date sendTime;

    @ExcelIgnore
    @ApiModelProperty("揽收时间")
    private Date shipmentsTime;

    @ExcelIgnore
    @ApiModelProperty("签收时间")
    private Date signedTime;

    @ExcelProperty(value = "品牌")
    @ApiModelProperty("品牌")
    private String brand;

    @ExcelProperty(value = "品类")
    @ApiModelProperty("分类")
    private String category;

    @ExcelProperty(value = "商品名称")
    @ApiModelProperty("商品名称")
    private String productName;

    @ExcelProperty(value = "规格信息")
    @ApiModelProperty("SKU名称")
    private String skuName;

    @ExcelProperty(value = "数量")
    @ApiModelProperty("数量")
    private Integer quantity;

    @ExcelIgnore
    @ApiModelProperty("省")
    private Long province;

    @ExcelIgnore
    @ApiModelProperty("省")
    private String provinceName;

    @ExcelIgnore
    @ApiModelProperty("市")
    private Long city;

    @ApiModelProperty("市")
    @ExcelIgnore
    private String cityName;

    @ApiModelProperty("收件人")
    @ExcelProperty(value = "收件人")
    private String addressee;

    @ApiModelProperty("手机号")
    @ExcelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty("收货地址")
    @ExcelProperty(value = "收货地址")
    private String receivingAddress;

    @ApiModelProperty("交易价格")
    @ExcelProperty(value = "成交价")
    private BigDecimal tradePrice;

    @ApiModelProperty("账期")
    @ExcelProperty(value = "账期", converter = AccountingConvert.class)
    private Integer accountingPeriod;

    @ExcelIgnore
    @ApiModelProperty("物流单号")
    private String trackingNumber;

    @ExcelIgnore
    @ApiModelProperty("物流公司")
    private String trackingCompany;

    @ExcelIgnore
    @ApiModelProperty("串码选项 0  发货前提供串码  1 发货后提供串码 2 不需要串码 ")
    private Integer codeOptions;

    @ExcelIgnore
    @ApiModelProperty("发货时效")
    private Integer deliveryTime;

    @ApiModelProperty("发货时效, if deliveryDeadline = 过去的一天 then 当天; if deliveryDeadline = 今天 then 当天; deliveryDeadline = 明天 then 明天;   ")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ExcelIgnore
    private Date deliveryDeadline;

    @ExcelIgnore
    @ApiModelProperty("其他要求")
    private String otherRequire;

    @ApiModelProperty("最后一次抢单时间")
    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastCompeteTime;

    @ApiModelProperty("抢单用户手机")
    @ExcelIgnore
    private String tradeUserPhone;

    @ApiModelProperty("处理申请,是否需要处理申请（ 1不需要 0 需要） ")
    @ExcelIgnore
    private Integer handleApply;

    @ApiModelProperty("撤销类型 撤销原因 0，店铺后台急速退款； 1 顾客拒签/拒收； 2 派送未联系到顾客，退回， 3 24小时物流无揽收信息; 4 供应商私自拦截 5 已经从其他渠道发货")
    @ExcelIgnore
    private Integer revokeType;

    @ExcelIgnore
    @ApiModelProperty("订单子状态, 81物流无流转信息; 82签收异常; 83手机号后四位错误")
    private Integer subStatus;

    @ApiModelProperty("物流公司")
    @ExcelProperty(value = "物流公司")
    private String express;

    @ApiModelProperty("寄件人手机号后四位")
    @ExcelProperty(value = "寄件人手机号后四位")
    private String sendPhone;

    @ExcelProperty(value = "86")
    @ApiModelProperty("串码")
    private String snList;


    @ExcelProperty(value = "SN")
    @ApiModelProperty("序列号SN")
    private String imeiList;

    @ApiModelProperty("物流单号")
    @ExcelProperty(value = "物流单号")
    private String expressNO;

}
