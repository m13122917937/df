package com.ruoyi.web.vo.bill;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel
public class DeductionVO {


    /**
     * 供应商id
     */
    @ApiModelProperty("供应商id")
    private Long companyId;

    /**
     * 供应商名称
     */
    @ApiModelProperty("供应商名称")
    private String companyName;

    /**
     * 订单号
     */
    @ApiModelProperty("订单号")
    private String orderCode;
    /**
     * 旺店通id
     */
    @ApiModelProperty("旺店通id")
    private String erpId;
    /**
     * 商家单号
     */
    @ApiModelProperty("商家单号")
    private String originalOrderId;
    /**
     * 品牌
     */
    @ApiModelProperty("品牌")
    private String brand;
    /**
     * 品类
     */
    @ApiModelProperty("品类")
    private String category;
    /**
     * 商品名
     */
    private String spuName;
    /**
     *
     *
     */
    private String skuName;


    /**
     * 发货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("发货时间")
    private Date sendTime;
    /**
     * 订单金额
     */
    @ApiModelProperty("订单金额")
    private BigDecimal tradePrice;
    /**
     * 扣罚金额
     */
    @ApiModelProperty("扣罚金额")
    private BigDecimal amount;
    /**
     * 错误原因
     */
    @ApiModelProperty("异常原因")
    private String remark;

    /**
     * 状态 ， 0 已经扣罚 ， 1 已经撤销
     */
    @ApiModelProperty("状态 ， 0 已经扣罚 ， 1 已经撤销")
    private Integer status;
}
