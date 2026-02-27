package com.ruoyi.biz.excel;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;

import java.util.List;

public class DropdownWriteHandler implements SheetWriteHandler {

    private final int columnIndex; // 下拉列索引（0-based）
    private final List<String> dropdownValues;

    public DropdownWriteHandler(int columnIndex, List<String> dropdownValues) {
        this.columnIndex = columnIndex;
        this.dropdownValues = dropdownValues;
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        // 获取当前 sheet
        var sheet = writeSheetHolder.getSheet();

        // 获取数据验证辅助类
        DataValidationHelper helper = sheet.getDataValidationHelper();

        // 设置下拉选项（注意：Excel 中下拉最多支持 255 个字符，如果选项多需用公式引用其他隐藏 sheet）
        String[] values = dropdownValues.toArray(new String[0]);
        DataValidationConstraint constraint = helper.createExplicitListConstraint(values);

        // 设置作用区域：从第2行开始（假设第1行为标题），到第1000行（可根据需要调整）
        int firstRow = 1; // 数据从第2行开始（0-based 行号）
        int lastRow = 1000;
        int firstCol = columnIndex;
        int lastCol = columnIndex;

        var region = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidation validation = helper.createValidation(constraint, region);

        // 处理 Excel 2007+ 的兼容性问题
        if (validation instanceof XSSFDataValidation) {
            validation.setSuppressDropDownArrow(true); // 显示下拉箭头
            validation.setShowErrorBox(true);
        }

        sheet.addValidationData(validation);
    }
}