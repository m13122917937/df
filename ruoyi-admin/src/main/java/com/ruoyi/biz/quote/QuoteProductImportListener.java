package com.ruoyi.biz.quote;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.ruoyi.quote.model.param.QuoteProductParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 报价商品 Excel 导入监听器；固定列序：品牌、品类、商品名、规格/型号、零售价、分销1价、分销2价。
 */
public class QuoteProductImportListener extends AnalysisEventListener<Map<Integer, String>> {

    private final Map<String, Long> brandMap;
    private final Map<String, Long> categoryMap;
    private final List<ImportRow> rows = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();
    private int currentRowIndex = 1;

    /**
     * 构造导入监听器。
     *
     * @param brandMap    品牌名称到ID映射
     * @param categoryMap 品类名称到ID映射
     */
    public QuoteProductImportListener(final Map<String, Long> brandMap,
                                      final Map<String, Long> categoryMap) {
        this.brandMap = brandMap;
        this.categoryMap = categoryMap;
    }

    /** {@inheritDoc} */
    @Override
    public void invoke(final Map<Integer, String> data, final AnalysisContext context) {
        currentRowIndex++;
        String error = validateAndBuild(data);
        if (error == null) {
            rows.add(toRow(data));
        } else {
            errors.add("第 " + currentRowIndex + " 行：" + error);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void doAfterAllAnalysed(final AnalysisContext context) {
    }

    /**
     * 解析成功的商品行。
     *
     * @return 商品行集合
     */
    public List<ImportRow> getRows() {
        return rows;
    }

    /**
     * 解析失败的错误信息。
     *
     * @return 错误信息集合
     */
    public List<String> getErrors() {
        return errors;
    }

    private String validateAndBuild(final Map<Integer, String> data) {
        String brandName = cell(data, QuoteManageBizService.getBrandColumn());
        String categoryName = cell(data, QuoteManageBizService.getCategoryColumn());
        String productName = cell(data, QuoteManageBizService.getProductNameColumn());
        if (brandName.isBlank()) {
            return "品牌不能为空";
        }
        if (categoryName.isBlank()) {
            return "品类不能为空";
        }
        if (productName.isBlank()) {
            return "商品名不能为空";
        }
        if (!brandMap.containsKey(brandName)) {
            return "品牌【" + brandName + "】不存在，请先在品牌管理中维护";
        }
        if (!categoryMap.containsKey(categoryName)) {
            return "品类【" + categoryName + "】不存在，请先在品类管理中维护";
        }
        BigDecimal retail = parsePriceOrNull(data, QuoteManageBizService.getRetailPriceColumn());
        BigDecimal distributor1 = parsePriceOrNull(data, QuoteManageBizService.getDistributor1PriceColumn());
        BigDecimal distributor2 = parsePriceOrNull(data, QuoteManageBizService.getDistributor2PriceColumn());
        if (retail == null && distributor1 == null && distributor2 == null) {
            return "至少需要填写一个价格";
        }
        if (isNegative(retail) || isNegative(distributor1) || isNegative(distributor2)) {
            return "价格必须为不小于 0 的数字";
        }
        return null;
    }

    private ImportRow toRow(final Map<Integer, String> data) {
        QuoteProductParam param = new QuoteProductParam()
                .setBrandId(brandMap.get(cell(data, QuoteManageBizService.getBrandColumn())))
                .setCategoryId(categoryMap.get(cell(data, QuoteManageBizService.getCategoryColumn())))
                .setBrand(cell(data, QuoteManageBizService.getBrandColumn()))
                .setCategory(cell(data, QuoteManageBizService.getCategoryColumn()))
                .setProductName(cell(data, QuoteManageBizService.getProductNameColumn()))
                .setSpecName(cell(data, QuoteManageBizService.getSpecNameColumn()))
                .setRetailPrice(parsePriceOrNull(data, QuoteManageBizService.getRetailPriceColumn()))
                .setDistributor1Price(parsePriceOrNull(data, QuoteManageBizService.getDistributor1PriceColumn()))
                .setDistributor2Price(parsePriceOrNull(data, QuoteManageBizService.getDistributor2PriceColumn()));
        return new ImportRow(currentRowIndex, param);
    }

    private BigDecimal parsePriceOrNull(final Map<Integer, String> data, final int column) {
        String value = cell(data, column);
        if (value.isBlank()) {
            return null;
        }
        return new BigDecimal(value);
    }

    private boolean isNegative(final BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) < 0;
    }

    private String cell(final Map<Integer, String> data, final int column) {
        String value = data == null ? null : data.get(column);
        return value == null ? "" : value.trim();
    }

    /**
     * 待保存的商品行。
     */
    public static class ImportRow {

        private final int rowIndex;
        private final QuoteProductParam param;

        ImportRow(final int rowIndex, final QuoteProductParam param) {
            this.rowIndex = rowIndex;
            this.param = param;
        }

        /**
         * Excel 行号。
         *
         * @return 行号
         */
        public int getRowIndex() {
            return rowIndex;
        }

        /**
         * 商品参数。
         *
         * @return 商品参数
         */
        public QuoteProductParam getParam() {
            return param;
        }
    }
}
