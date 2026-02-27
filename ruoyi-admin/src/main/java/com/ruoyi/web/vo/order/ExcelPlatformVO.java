package com.ruoyi.web.vo.order;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  // 显式添加无参构造（Lombok）
@AllArgsConstructor
public class ExcelPlatformVO {


    @ExcelProperty(value = "订单编号")
    private String orderCode;

    @ExcelProperty(value = "商家单号")
    private String originalOrderId;

    @ExcelProperty(value = "imei/86")
    private String imei;

    @ExcelProperty(value = "sn")
    private String sn;

    @ExcelProperty(value = "平台")
    private String platform;

    @ExcelProperty(value = "分类")
    private String category;

    @ExcelProperty(value = "品牌")
    private String brand;

    @ExcelProperty(value = "是否验证：正常、风险")
    private String validated;

}
