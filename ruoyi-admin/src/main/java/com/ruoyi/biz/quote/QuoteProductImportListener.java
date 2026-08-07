package com.ruoyi.biz.quote;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.ruoyi.quote.model.param.QuotePriceHistoryParam;
import com.ruoyi.quote.model.param.QuoteProductParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 报价商品 Excel 导入监听器；固定列序：品牌、品类、商品名、规格/型号、零售价、分销1价、分销2价。
 */
public class QuoteProductImportListener extends AnalysisEventListener<Map<Integer, String>> {

    private static final int BRAND_COLUMN = 0;
    private static final int CATEGORY_COLUMN = 1;
    private static final int PRODUCT_NAME_COLUMN = 2;
    private static final int SPEC_NAME_COLUMN = 3;
    private static final int RETAIL_PRICE_COLUMN = 4;
    private static final int DISTRIBUTOR1_PRICE_COLUMN = 5;
    private static final int DISTRIBUTOR2_PRICE_COLUMN = 6;

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
        String brandName = cell(data, BRAND_COLUMN);
        String categoryName = cell(data, CATEGORY_COLUMN);
        String productName = cell(data, PRODUCT_NAME_COLUMN);
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
        BigDecimal retail = parsePriceOrNull(data, RETAIL_PRICE_COLUMN);
        BigDecimal distributor1 = parsePriceOrNull(data, DISTRIBUTOR1_PRICE_COLUMN);
        BigDecimal distributor2 = parsePriceOrNull(data, DISTRIBUTOR2_PRICE_COLUMN);
        if (isNegative(retail) || isNegative(distributor1) || isNegative(distributor2)) {
            return "价格必须为不小于 0 的数字";
        }
        return null;
    }

    private ImportRow toRow(final Map<Integer, String> data) {
        String brandName = cell(data, BRAND_COLUMN);
        String categoryName = cell(data, CATEGORY_COLUMN);
        QuoteProductParam param = new QuoteProductParam()
                .setBrandId(brandMap.get(brandName))
                .setCategoryId(categoryMap.get(categoryName))
                .setBrand(brandName)
                .setCategory(categoryName)
                .setProductName(cell(data, PRODUCT_NAME_COLUMN))
                .setSpecName(cell(data, SPEC_NAME_COLUMN));
        BigDecimal retail = parsePriceOrNull(data, RETAIL_PRICE_COLUMN);
        BigDecimal distributor1 = parsePriceOrNull(data, DISTRIBUTOR1_PRICE_COLUMN);
        BigDecimal distributor2 = parsePriceOrNull(data, DISTRIBUTOR2_PRICE_COLUMN);
        QuotePriceHistoryParam priceParam = null;
        if (retail != null || distributor1 != null || distributor2 != null) {
            priceParam = new QuotePriceHistoryParam()
                    .setRetailPrice(retail)
                    .setDistributor1Price(distributor1)
                    .setDistributor2Price(distributor2);
        }
        return new ImportRow(currentRowIndex, param, priceParam);
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
        private final QuotePriceHistoryParam priceParam;

        ImportRow(final int rowIndex, final QuoteProductParam param, final QuotePriceHistoryParam priceParam) {
            this.rowIndex = rowIndex;
            this.param = param;
            this.priceParam = priceParam;
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

        /**
         * 当天报价参数。
         *
         * @return 报价参数
         */
        public QuotePriceHistoryParam getPriceParam() {
            return priceParam;
        }
    }
}
