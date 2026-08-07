package com.ruoyi.biz.quote;

import com.alibaba.excel.EasyExcel;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.quote.facade.IQuoteBrandFacade;
import com.ruoyi.quote.facade.IQuoteCategoryFacade;
import com.ruoyi.quote.facade.IQuoteProductFacade;
import com.ruoyi.quote.model.bo.QuoteBrandBO;
import com.ruoyi.quote.model.bo.QuoteCategoryBO;
import com.ruoyi.quote.model.bo.QuoteProductBO;
import com.ruoyi.quote.model.param.QuoteBrandParam;
import com.ruoyi.quote.model.param.QuoteCategoryParam;
import com.ruoyi.quote.model.param.QuoteProductParam;
import com.ruoyi.quote.model.query.QuoteBrandQuery;
import com.ruoyi.quote.model.query.QuoteCategoryQuery;
import com.ruoyi.quote.model.query.QuoteProductQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报价后台应用编排服务。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QuoteManageBizService {

    private static final int BRAND_COLUMN = 0;
    private static final int CATEGORY_COLUMN = 1;
    private static final int PRODUCT_NAME_COLUMN = 2;
    private static final int SPEC_NAME_COLUMN = 3;
    private static final int RETAIL_PRICE_COLUMN = 4;
    private static final int DISTRIBUTOR1_PRICE_COLUMN = 5;
    private static final int DISTRIBUTOR2_PRICE_COLUMN = 6;

    private final IQuoteProductFacade quoteProductFacade;
    private final IQuoteBrandFacade quoteBrandFacade;
    private final IQuoteCategoryFacade quoteCategoryFacade;

    /**
     * 分页查询报价商品。
     *
     * @param query     查询条件
     * @param pageParam 分页参数
     * @return 报价商品分页数据
     */
    public PageBO<QuoteProductBO> pageProducts(final QuoteProductQuery query, final PageParamV2 pageParam) {
        return quoteProductFacade.page(query, pageParam);
    }

    /**
     * 保存报价商品基础信息与三档价格。
     *
     * @param param 商品参数
     */
    public void saveProduct(final QuoteProductParam param) {
        quoteProductFacade.save(param);
    }

    /**
     * 仅更新报价商品三档价格。
     *
     * @param param 商品参数（需含 id 与至少一个价格）
     */
    public void saveProductPrices(final QuoteProductParam param) {
        quoteProductFacade.savePrices(param);
    }

    /**
     * 删除报价商品。
     *
     * @param id 商品ID
     */
    public void deleteProduct(final Long id) {
        quoteProductFacade.delete(id);
    }

    /**
     * 分页查询品牌。
     *
     * @param query     查询条件
     * @param pageParam 分页参数
     * @return 品牌分页数据
     */
    public PageBO<QuoteBrandBO> pageBrands(final QuoteBrandQuery query, final PageParamV2 pageParam) {
        return quoteBrandFacade.page(query, pageParam);
    }

    /**
     * 查询全部品牌。
     *
     * @return 品牌集合
     */
    public List<QuoteBrandBO> listBrands() {
        return quoteBrandFacade.list(new QuoteBrandQuery());
    }

    /**
     * 保存品牌。
     *
     * @param param 品牌参数
     */
    public void saveBrand(final QuoteBrandParam param) {
        param.setUpdateBy(SecurityUtils.getUsername());
        quoteBrandFacade.save(param);
    }

    /**
     * 删除品牌。
     *
     * @param id 品牌ID
     */
    public void deleteBrand(final Long id) {
        quoteBrandFacade.delete(id);
    }

    /**
     * 分页查询品类。
     *
     * @param query     查询条件
     * @param pageParam 分页参数
     * @return 品类分页数据
     */
    public PageBO<QuoteCategoryBO> pageCategories(final QuoteCategoryQuery query, final PageParamV2 pageParam) {
        return quoteCategoryFacade.page(query, pageParam);
    }

    /**
     * 查询全部品类。
     *
     * @return 品类集合
     */
    public List<QuoteCategoryBO> listCategories() {
        return quoteCategoryFacade.list(new QuoteCategoryQuery());
    }

    /**
     * 保存品类。
     *
     * @param param 品类参数
     */
    public void saveCategory(final QuoteCategoryParam param) {
        param.setUpdateBy(SecurityUtils.getUsername());
        quoteCategoryFacade.save(param);
    }

    /**
     * 删除品类。
     *
     * @param id 品类ID
     */
    public void deleteCategory(final Long id) {
        quoteCategoryFacade.delete(id);
    }

    /**
     * 导出报价商品 Excel（品牌、品类、商品名、规格、零售价、分销1价、分销2价）。
     *
     * @param query    查询条件
     * @param response HTTP 响应
     */
    public void exportProducts(final QuoteProductQuery query, final HttpServletResponse response)
            throws IOException {
        List<QuoteProductBO> products = quoteProductFacade.list(query);
        List<List<String>> head = List.of(
                List.of("品牌"), List.of("品类"), List.of("商品名"), List.of("规格/型号"),
                List.of("零售价"), List.of("分销1价"), List.of("分销2价"));
        List<List<Object>> rows = new ArrayList<>();
        for (QuoteProductBO product : products) {
            rows.add(List.of(product.getBrand(), product.getCategory(), product.getProductName(),
                    product.getSpecName(), product.getRetailPrice(),
                    product.getDistributor1Price(), product.getDistributor2Price()));
        }
        EasyExcel.write(response.getOutputStream()).head(head).sheet("报价商品").doWrite(rows);
    }

    /**
     * 导入报价商品 Excel。
     *
     * @param file Excel 文件
     * @return 导入统计
     */
    public Map<String, Object> importProducts(final MultipartFile file) {
        Map<String, Long> brandMap = loadBrandMap();
        Map<String, Long> categoryMap = loadCategoryMap();
        QuoteProductImportListener listener = new QuoteProductImportListener(brandMap, categoryMap);
        try {
            EasyExcel.read(file.getInputStream())
                    .headRowNumber(1)
                    .registerReadListener(listener)
                    .sheet()
                    .doRead();
        } catch (IOException e) {
            throw new ServiceException("读取导入文件失败：" + e.getMessage());
        }
        int success = 0;
        List<String> errors = new ArrayList<>(listener.getErrors());
        for (QuoteProductImportListener.ImportRow row : listener.getRows()) {
            try {
                quoteProductFacade.save(row.getParam());
                success++;
            } catch (Exception e) {
                log.warn("导入报价商品失败，行号 {}：{}", row.getRowIndex(), e.getMessage());
                errors.add("第 " + row.getRowIndex() + " 行：" + e.getMessage());
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("failed", errors.size());
        result.put("errors", errors);
        return result;
    }

    private Map<String, Long> loadBrandMap() {
        return listBrands().stream()
                .collect(Collectors.toMap(QuoteBrandBO::getBrandName, QuoteBrandBO::getId));
    }

    private Map<String, Long> loadCategoryMap() {
        return listCategories().stream()
                .collect(Collectors.toMap(QuoteCategoryBO::getCategoryName, QuoteCategoryBO::getId));
    }

    /**
     * 价格解析工具（导入校验用）。
     *
     * @param text 价格文本
     * @return 价格
     */
    public static BigDecimal parsePrice(final String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        return new BigDecimal(text.trim());
    }

    /**
     * Excel 列索引常量访问。
     *
     * @return 品牌列索引
     */
    public static int getBrandColumn() {
        return BRAND_COLUMN;
    }

    /**
     * 品类列索引。
     *
     * @return 品类列索引
     */
    public static int getCategoryColumn() {
        return CATEGORY_COLUMN;
    }

    /**
     * 商品名列索引。
     *
     * @return 商品名列索引
     */
    public static int getProductNameColumn() {
        return PRODUCT_NAME_COLUMN;
    }

    /**
     * 规格列索引。
     *
     * @return 规格列索引
     */
    public static int getSpecNameColumn() {
        return SPEC_NAME_COLUMN;
    }

    /**
     * 零售价列索引。
     *
     * @return 零售价列索引
     */
    public static int getRetailPriceColumn() {
        return RETAIL_PRICE_COLUMN;
    }

    /**
     * 分销1价列索引。
     *
     * @return 分销1价列索引
     */
    public static int getDistributor1PriceColumn() {
        return DISTRIBUTOR1_PRICE_COLUMN;
    }

    /**
     * 分销2价列索引。
     *
     * @return 分销2价列索引
     */
    public static int getDistributor2PriceColumn() {
        return DISTRIBUTOR2_PRICE_COLUMN;
    }
}
