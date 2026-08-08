package com.ruoyi.biz.quote;

import com.alibaba.excel.EasyExcel;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.quote.facade.IQuoteBrandFacade;
import com.ruoyi.quote.facade.IQuoteCategoryFacade;
import com.ruoyi.quote.facade.IQuotePriceHistoryFacade;
import com.ruoyi.quote.facade.IQuoteProductFacade;
import com.ruoyi.quote.model.bo.QuoteBrandBO;
import com.ruoyi.quote.model.bo.QuoteCategoryBO;
import com.ruoyi.quote.model.bo.QuotePriceHistoryBO;
import com.ruoyi.quote.model.bo.QuoteProductBO;
import com.ruoyi.quote.model.param.QuoteBrandParam;
import com.ruoyi.quote.model.param.QuoteCategoryParam;
import com.ruoyi.quote.model.param.QuotePriceHistoryParam;
import com.ruoyi.quote.model.param.QuoteProductParam;
import com.ruoyi.quote.model.query.QuoteBrandQuery;
import com.ruoyi.quote.model.query.QuoteCategoryQuery;
import com.ruoyi.quote.model.query.QuotePriceHistoryQuery;
import com.ruoyi.quote.model.query.QuoteProductQuery;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.param.CompanyParam;
import com.ruoyi.user.model.query.CompanyQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    private final IQuoteProductFacade quoteProductFacade;
    private final IQuoteBrandFacade quoteBrandFacade;
    private final IQuoteCategoryFacade quoteCategoryFacade;
    private final IQuotePriceHistoryFacade quotePriceHistoryFacade;
    private final ICompanyFacade companyFacade;

    /**
     * 分页查询报价商品（含最新报价）。
     *
     * @param query     查询条件
     * @param pageParam 分页参数
     * @return 报价商品分页数据
     */
    public PageBO<QuoteProductBO> pageProducts(final QuoteProductQuery query, final PageParamV2 pageParam) {
        return quoteProductFacade.page(query, pageParam);
    }

    /**
     * 保存报价商品基础信息（不含价格）。
     *
     * @param param 商品参数
     */
    public void saveProduct(final QuoteProductParam param) {
        quoteProductFacade.save(param);
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
     * 保存当天报价（幂等覆盖当天）。
     *
     * @param param 报价流水参数
     */
    public void saveQuote(final QuotePriceHistoryParam param) {
        quotePriceHistoryFacade.saveQuote(param);
    }

    /**
     * 查询商品历史报价（按日期倒序）。
     *
     * @param productId 商品ID
     * @return 历史报价集合
     */
    public List<QuotePriceHistoryBO> listQuoteHistory(final Long productId) {
        return quotePriceHistoryFacade.list(
                new QuotePriceHistoryQuery().setProductId(productId));
    }

    /**
     * 查询全部报价商品（含最新报价，按品牌/商品名/规格排序，报价单图片用）。
     *
     * @return 报价商品集合
     */
    public List<QuoteProductBO> listQuoteImageData() {
        return quoteProductFacade.list(new QuoteProductQuery());
    }

    /**
     * 分页查询客户（公司）列表（含报价层级，默认零售）。
     *
     * @param pageParam 分页参数
     * @return 客户分页数据
     */
    public PageBO<CompanyBO> pageCompanies(final PageParamV2 pageParam) {
        return companyFacade.listPage(new CompanyQuery(), pageParam);
    }

    /**
     * 保存客户报价层级。
     *
     * @param companyId 客户（公司）ID
     * @param level     客户层级(0-零售，1-批发1，2-批发2)
     */
    public void saveCustomerLevel(final Long companyId, final Integer level) {
        if (companyId == null) {
            throw new ServiceException("客户不能为空");
        }
        if (level == null || level < 0 || level > 2) {
            throw new ServiceException("客户层级必须为 0(零售)、1(批发1)、2(批发2)");
        }
        companyFacade.update(new CompanyParam().setId(companyId).setQuoteLevel(level),
                new CompanyQuery().setId(companyId));
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
     * 导出报价商品 Excel（品牌、品类、商品名、规格、最新报价三档价格）。
     *
     * @param query    查询条件
     * @param response HTTP 响应
     */
    public void exportProducts(final QuoteProductQuery query, final HttpServletResponse response)
            throws IOException {
        List<QuoteProductBO> products = quoteProductFacade.list(query);
        List<List<String>> head = List.of(
                List.of("品牌"), List.of("品类"), List.of("商品名"), List.of("规格/型号"), List.of("商品编码"),
                List.of("零售价"), List.of("分销1价"), List.of("分销2价"));
        List<List<Object>> rows = new ArrayList<>();
        for (QuoteProductBO product : products) {
            QuotePriceHistoryBO latest = product.getLatestQuote();
            rows.add(List.of(product.getBrand(), product.getCategory(), product.getProductName(),
                    product.getSpecName(), product.getProductCode(),
                    latest == null ? null : latest.getRetailPrice(),
                    latest == null ? null : latest.getDistributor1Price(),
                    latest == null ? null : latest.getDistributor2Price()));
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
                if (row.getPriceParam() != null) {
                    quotePriceHistoryFacade.saveQuote(row.getPriceParam());
                }
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
}
