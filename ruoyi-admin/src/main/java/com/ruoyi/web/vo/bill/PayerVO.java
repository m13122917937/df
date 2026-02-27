package com.ruoyi.web.vo.bill;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PayerVO {

    /**
     * $column.columnComment
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 付款户名
     */
    @ApiModelProperty("付款主体简称")
    private String nickName;
    /**
     * 付款户名
     */
    @ApiModelProperty("付款户名")

    private String payName;
    /**
     * 银行信息
     */
    @ApiModelProperty("银行信息")

    private String bankName;
    /**
     * 银行卡号
     */
    @ApiModelProperty("银行卡号")
    private String payNo;

    /**
     * 账户余额
     */
    @ApiModelProperty("账户余额")
    private BigDecimal balance;

    /**
     * 是否激活， 0 激活， 1弃用
     */
    @ApiModelProperty("是否激活， 0 激活， 1弃用")
    private Integer actived;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 创建人
     */

    private Long createBy;

    @ApiModelProperty("创建人")
    private String createName;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /**
     * 修改人
     */
    private Long updateBy;

    @ApiModelProperty("修改人")

    private String updateName;
}
