package com.ruoyi.web.controller.quote;

import com.ruoyi.biz.quote.QuoteManageBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.quote.model.bo.QuoteBrandBO;
import com.ruoyi.quote.model.bo.QuoteCategoryBO;
import com.ruoyi.quote.model.bo.QuotePriceHistoryBO;
import com.ruoyi.quote.model.bo.QuoteProductBO;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.web.convert.quote.QuoteWebConvert;
import com.ruoyi.web.vo.quote.QuoteBrandQueryRequest;
import com.ruoyi.web.vo.quote.QuoteBrandSaveRequest;
import com.ruoyi.web.vo.quote.QuoteBrandVO;
import com.ruoyi.web.vo.quote.QuoteCategoryQueryRequest;
import com.ruoyi.web.vo.quote.QuoteCategorySaveRequest;
import com.ruoyi.web.vo.quote.QuoteCategoryVO;
import com.ruoyi.web.vo.quote.QuoteCustomerLevelSaveRequest;
import com.ruoyi.web.vo.quote.QuoteCustomerLevelVO;
import com.ruoyi.web.vo.quote.QuotePriceHistoryVO;
import com.ruoyi.web.vo.quote.QuoteProductQueryRequest;
import com.ruoyi.web.vo.quote.QuoteProductSaveRequest;
import com.ruoyi.web.vo.quote.QuoteProductVO;
import com.ruoyi.web.vo.quote.QuoteQuoteSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 报价后台管理接口。
 */
@RestController
@RequestMapping("/quote")
@RequiredArgsConstructor
public class QuoteManageController extends BaseController {

    private final QuoteManageBizService quoteManageBizService;

    /**
     * 分页查询报价商品。
     *
     * @param request 查询请求
     * @return 报价商品分页数据
     */
    @PostMapping("/product/page")
    @PreAuthorize("@ss.hasPermi('quote:product:list')")
    public TableDataInfo productPage(@RequestBody final QuoteProductQueryRequest request) {
        PageBO<QuoteProductBO> page = quoteManageBizService.pageProducts(
                QuoteWebConvert.INSTANCE.toProductQuery(request), startParamV2("sort_order asc, id asc"));
        List<QuoteProductVO> rows = QuoteWebConvert.INSTANCE.toProductVOList(page.getData());
        return getDataTable(rows, page.getTotal());
    }

    /**
     * 保存报价商品及其价格。
     *
     * @param request 保存请求
     * @return 操作结果
     */
    @PostMapping("/product/save")
    @PreAuthorize("@ss.hasPermi('quote:product:list')")
    public AjaxResult productSave(@RequestBody final QuoteProductSaveRequest request) {
        quoteManageBizService.saveProduct(QuoteWebConvert.INSTANCE.toProductParam(request));
        return AjaxResult.success();
    }

    /**
     * 保存当天报价（幂等覆盖当天）。
     *
     * @param request 报价保存请求
     * @return 操作结果
     */
    @PostMapping("/quote/save")
    @PreAuthorize("@ss.hasPermi('quote:product:list')")
    public AjaxResult quoteSave(@RequestBody final QuoteQuoteSaveRequest request) {
        quoteManageBizService.saveQuote(QuoteWebConvert.INSTANCE.toPriceHistoryParam(request));
        return AjaxResult.success();
    }

    /**
     * 查询商品历史报价。
     *
     * @param id 商品ID
     * @return 历史报价集合
     */
    @GetMapping("/quote/history/{id}")
    @PreAuthorize("@ss.hasPermi('quote:product:list')")
    public AjaxResult quoteHistory(@PathVariable("id") final Long id) {
        List<QuotePriceHistoryBO> histories = quoteManageBizService.listQuoteHistory(id);
        List<QuotePriceHistoryVO> rows = QuoteWebConvert.INSTANCE.toPriceHistoryVOList(histories);
        return AjaxResult.success(rows);
    }

    /**
     * 分页查询客户层级。
     *
     * @return 客户层级分页数据
     */
    @PostMapping("/customer-level/page")
    @PreAuthorize("@ss.hasPermi('quote:customerLevel:list')")
    public TableDataInfo customerLevelPage() {
        PageBO<CompanyBO> page = quoteManageBizService.pageCompanies(startParamV2());
        List<QuoteCustomerLevelVO> rows = QuoteWebConvert.INSTANCE.toCustomerLevelVOList(page.getData());
        return getDataTable(rows, page.getTotal());
    }

    /**
     * 保存客户层级。
     *
     * @param request 保存请求
     * @return 操作结果
     */
    @PostMapping("/customer-level/save")
    @PreAuthorize("@ss.hasPermi('quote:customerLevel:list')")
    public AjaxResult customerLevelSave(@RequestBody final QuoteCustomerLevelSaveRequest request) {
        quoteManageBizService.saveCustomerLevel(request.getCompanyId(), request.getLevel());
        return AjaxResult.success();
    }

    /**
     * 删除报价商品。
     *
     * @param id 商品ID
     * @return 操作结果
     */
    @DeleteMapping("/product/{id}")
    @PreAuthorize("@ss.hasPermi('quote:product:list')")
    public AjaxResult productDelete(@PathVariable("id") final Long id) {
        quoteManageBizService.deleteProduct(id);
        return AjaxResult.success();
    }

    /**
     * 分页查询品牌。
     *
     * @param request 查询请求
     * @return 品牌分页数据
     */
    @PostMapping("/brand/page")
    @PreAuthorize("@ss.hasPermi('quote:brand:list')")
    public TableDataInfo brandPage(@RequestBody final QuoteBrandQueryRequest request) {
        PageBO<QuoteBrandBO> page = quoteManageBizService.pageBrands(
                QuoteWebConvert.INSTANCE.toBrandQuery(request), startParamV2("sort_order asc, id asc"));
        List<QuoteBrandVO> rows = QuoteWebConvert.INSTANCE.toBrandVOList(page.getData());
        return getDataTable(rows, page.getTotal());
    }

    /**
     * 查询全部品牌（商品编辑下拉用）。
     *
     * @return 品牌集合
     */
    @GetMapping("/brand/options")
    @PreAuthorize("@ss.hasPermi('quote:brand:list')")
    public AjaxResult brandOptions() {
        List<QuoteBrandVO> rows = QuoteWebConvert.INSTANCE.toBrandVOList(
                quoteManageBizService.listBrands());
        return AjaxResult.success(rows);
    }

    /**
     * 保存品牌。
     *
     * @param request 保存请求
     * @return 操作结果
     */
    @PostMapping("/brand/save")
    @PreAuthorize("@ss.hasPermi('quote:brand:list')")
    public AjaxResult brandSave(@RequestBody final QuoteBrandSaveRequest request) {
        quoteManageBizService.saveBrand(QuoteWebConvert.INSTANCE.toBrandParam(request));
        return AjaxResult.success();
    }

    /**
     * 删除品牌。
     *
     * @param id 品牌ID
     * @return 操作结果
     */
    @DeleteMapping("/brand/{id}")
    @PreAuthorize("@ss.hasPermi('quote:brand:list')")
    public AjaxResult brandDelete(@PathVariable("id") final Long id) {
        quoteManageBizService.deleteBrand(id);
        return AjaxResult.success();
    }

    /**
     * 分页查询品类。
     *
     * @param request 查询请求
     * @return 品类分页数据
     */
    @PostMapping("/category/page")
    @PreAuthorize("@ss.hasPermi('quote:category:list')")
    public TableDataInfo categoryPage(@RequestBody final QuoteCategoryQueryRequest request) {
        PageBO<QuoteCategoryBO> page = quoteManageBizService.pageCategories(
                QuoteWebConvert.INSTANCE.toCategoryQuery(request), startParamV2("sort_order asc, id asc"));
        List<QuoteCategoryVO> rows = QuoteWebConvert.INSTANCE.toCategoryVOList(page.getData());
        return getDataTable(rows, page.getTotal());
    }

    /**
     * 查询全部品类（商品编辑下拉用）。
     *
     * @return 品类集合
     */
    @GetMapping("/category/options")
    @PreAuthorize("@ss.hasPermi('quote:category:list')")
    public AjaxResult categoryOptions() {
        List<QuoteCategoryVO> rows = QuoteWebConvert.INSTANCE.toCategoryVOList(
                quoteManageBizService.listCategories());
        return AjaxResult.success(rows);
    }

    /**
     * 保存品类。
     *
     * @param request 保存请求
     * @return 操作结果
     */
    @PostMapping("/category/save")
    @PreAuthorize("@ss.hasPermi('quote:category:list')")
    public AjaxResult categorySave(@RequestBody final QuoteCategorySaveRequest request) {
        quoteManageBizService.saveCategory(QuoteWebConvert.INSTANCE.toCategoryParam(request));
        return AjaxResult.success();
    }

    /**
     * 删除品类。
     *
     * @param id 品类ID
     * @return 操作结果
     */
    @DeleteMapping("/category/{id}")
    @PreAuthorize("@ss.hasPermi('quote:category:list')")
    public AjaxResult categoryDelete(@PathVariable("id") final Long id) {
        quoteManageBizService.deleteCategory(id);
        return AjaxResult.success();
    }

    /**
     * 导出报价商品 Excel。
     *
     * @param request  查询请求
     * @param response HTTP 响应
     */
    @PostMapping("/product/export")
    @PreAuthorize("@ss.hasPermi('quote:product:list')")
    public void productExport(final QuoteProductQueryRequest request,
                              final HttpServletResponse response) throws IOException {
        quoteManageBizService.exportProducts(
                QuoteWebConvert.INSTANCE.toProductQuery(request), response);
    }

    /**
     * 导入报价商品 Excel。
     *
     * @param file Excel 文件
     * @return 导入统计
     */
    @PostMapping("/product/import")
    @PreAuthorize("@ss.hasPermi('quote:product:list')")
    public AjaxResult productImport(@RequestParam("file") final MultipartFile file) {
        checkExcel(file);
        return AjaxResult.success(quoteManageBizService.importProducts(file));
    }
}
