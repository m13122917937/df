package com.ruoyi.web.vo.bill.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.ruoyi.bill.constant.BillConsts;

public class AccountingConvert implements Converter<Integer> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return Integer.class; // 支持的 Java 字段类型
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING; // 写入 Excel 时作为文本
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            Integer value,
            ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration) {
        return new WriteCellData<>("T+" + value);
    }


}
