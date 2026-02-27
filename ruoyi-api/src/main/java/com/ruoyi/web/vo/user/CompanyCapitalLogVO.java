package com.ruoyi.web.vo.user;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 企业押金记录对象 u_company_capital_log
 *
 * @author ruoyi
 * @date 2025-09-08
 */
@Data
@Accessors(chain = true)
@ExcelIgnoreUnannotated // 只导出有@ExcelProperty注解的字段
public class CompanyCapitalLogVO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("订单号")
    @ExcelProperty("订单号")
    private String orderNo;

    @ApiModelProperty("出项")
    @ExcelProperty("出项")
    private BigDecimal outAmount;

    @ApiModelProperty("进项")
    @ExcelProperty("进项")
    private BigDecimal addAmount;

    @ApiModelProperty("小计")
    @ExcelProperty("小计")
    private BigDecimal subtotal;

    @ApiModelProperty("可用金额")
    @ExcelProperty("可用金额")
    private BigDecimal availableAmount;


    @ApiModelProperty("业务类型，1，充值， 2 抢单 3 追单")
    private Integer type;

    @ApiModelProperty("业务类型，1，充值， 2 抢单 3 追单")
    @ExcelProperty(value = "业务类型")
    private String typeName;


}
