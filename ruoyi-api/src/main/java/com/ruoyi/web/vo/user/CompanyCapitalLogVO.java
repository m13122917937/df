package com.ruoyi.web.vo.user;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
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


    @ExcelProperty("订单号")
    private String orderNo;


    @ExcelProperty("出项")
    private BigDecimal outAmount;


    @ExcelProperty("进项")
    private BigDecimal addAmount;


    @ExcelProperty("小计")
    private BigDecimal subtotal;


    @ExcelProperty("可用金额")
    private BigDecimal availableAmount;



    private Integer type;


    @ExcelProperty(value = "业务类型")
    private String typeName;


}
