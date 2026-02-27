//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ruoyi.web.util;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public final class ExcelUtil {
    public static <T> void write(final File file, final List<T> datas, final Class<T> clazz) {
        write((File)file, (String)null, datas, clazz);
    }

    public static <T> void write(final OutputStream outputStream, final List<T> datas, final Class<T> clazz) {
        write((OutputStream)outputStream, (String)null, datas, clazz);
    }

    public static <T> void write(final File file, final String sheetName, final List<T> datas, final Class<T> clazz) {
        write(file, sheetName, datas, clazz, new LongestMatchColumnWidthStyleStrategy());
    }

    public static <T> void write(final OutputStream outputStream, final String sheetName, final List<T> datas, final Class<T> clazz) {
        write(outputStream, sheetName, datas, clazz, new LongestMatchColumnWidthStyleStrategy());
    }

    public static <T> void write(final OutputStream outputStream, final String sheetName, final List<T> datas, final Class<T> clazz, final WriteHandler... writeHandler) {
        ExcelWriterBuilder write = EasyExcel.write(outputStream, clazz);
        if (ArrayUtil.isNotEmpty(writeHandler)) {
            for(WriteHandler handler : writeHandler) {
                write.registerWriteHandler(handler);
            }
        }

        write.sheet(sheetName).doWrite(datas);
    }

    public static <T> void write(final File file, final String sheetName, final List<T> datas, final Class<T> clazz, final WriteHandler... writeHandler) {
        ExcelWriterBuilder write = EasyExcel.write(file, clazz);
        if (ArrayUtil.isNotEmpty(writeHandler)) {
            for(WriteHandler handler : writeHandler) {
                write.registerWriteHandler(handler);
            }
        }

        write.sheet(sheetName).doWrite(datas);
    }





    public static <T> void write(OutputStream outputStream, String sheetName, List<T> datas, Class<T> clazz, Integer relativeHeadRowIndex) {
        ((ExcelWriterBuilder)((ExcelWriterBuilder)EasyExcel.write(outputStream, clazz).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())).relativeHeadRowIndex(relativeHeadRowIndex)).sheet(sheetName).doWrite(datas);
    }

    public static <T> void writeTemplate(OutputStream outputStream, String sheetName, List<T> datas, Class<T> clazz, Integer relativeHeadRowIndex, String templateFilePath) {
        ((ExcelWriterBuilder)((ExcelWriterBuilder)EasyExcel.write(outputStream, clazz).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())).relativeHeadRowIndex(relativeHeadRowIndex)).withTemplate(templateFilePath).sheet(sheetName).doWrite(datas);
    }

    public static <T> void writeTemplate(OutputStream outputStream, String sheetName, List<T> datas, Class<T> clazz, Integer relativeHeadRowIndex, InputStream templateFile) {
        ((ExcelWriterBuilder)((ExcelWriterBuilder)EasyExcel.write(outputStream, clazz).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())).relativeHeadRowIndex(relativeHeadRowIndex)).withTemplate(templateFile).sheet(sheetName).doWrite(datas);
    }



    private ExcelUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
