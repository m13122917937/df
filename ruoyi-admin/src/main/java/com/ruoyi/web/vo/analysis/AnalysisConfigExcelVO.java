package com.ruoyi.web.vo.analysis;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 经营核算配置 Excel 行对象。
 */
@Data
public class AnalysisConfigExcelVO {
    @ExcelProperty("发生日期")
    private LocalDate businessDate;
    @ExcelProperty("月份")
    private String monthValue;
    @ExcelProperty("平台")
    private String platform;
    @ExcelProperty("店铺")
    private String shopName;
    @ExcelProperty("订单号")
    private String originalOrderNo;
    @ExcelProperty("货品编码")
    private String goodsNo;
    @ExcelProperty("商品名称")
    private String goodsName;
    @ExcelProperty("品牌")
    private String brand;
    @ExcelProperty("品类")
    private String category;
    @ExcelProperty("金额")
    private BigDecimal amount;
    @ExcelProperty("系数或点位")
    private BigDecimal coefficient;
    @ExcelProperty("生效开始")
    private LocalDate startDate;
    @ExcelProperty("生效结束")
    private LocalDate endDate;
    @ExcelProperty("说明")
    private String reason;
}
