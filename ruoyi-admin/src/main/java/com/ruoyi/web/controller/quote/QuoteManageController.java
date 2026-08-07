package com.ruoyi.web.controller.quote;

import com.ruoyi.biz.quote.QuoteManageBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.quote.model.bo.QuotePriceTierBO;
import com.ruoyi.quote.model.bo.QuoteProductBO;
import com.ruoyi.web.convert.quote.QuoteWebConvert;
import com.ruoyi.web.vo.quote.QuotePriceTierQueryRequest;
import com.ruoyi.web.vo.quote.QuotePriceTierSaveRequest;
import com.ruoyi.web.vo.quote.QuotePriceTierVO;
import com.ruoyi.web.vo.quote.QuoteProductQueryRequest;
import com.ruoyi.web.vo.quote.QuoteProductSaveRequest;
import com.ruoyi.web.vo.quote.QuoteProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * 分页查询价格档位。
     *
     * @param request 查询请求
     * @return 价格档位分页数据
     */
    @PostMapping("/tier/list")
    @PreAuthorize("@ss.hasPermi('quote:tier:list')")
    public TableDataInfo tierList(@RequestBody final QuotePriceTierQueryRequest request) {
        PageBO<QuotePriceTierBO> page = quoteManageBizService.pageTiers(
                QuoteWebConvert.INSTANCE.toTierQuery(request), startParamV2("sort_order asc, id asc"));
        List<QuotePriceTierVO> rows = QuoteWebConvert.INSTANCE.toTierVOList(page.getData());
        return getDataTable(rows, page.getTotal());
    }

    /**
     * 查询全部价格档位（商品编辑下拉用）。
     *
     * @return 价格档位集合
     */
    @GetMapping("/tier/options")
    @PreAuthorize("@ss.hasPermi('quote:tier:list')")
    public AjaxResult tierOptions() {
        List<QuotePriceTierVO> rows = QuoteWebConvert.INSTANCE.toTierVOList(
                quoteManageBizService.listTiers());
        return AjaxResult.success(rows);
    }

    /**
     * 保存价格档位。
     *
     * @param request 保存请求
     * @return 操作结果
     */
    @PostMapping("/tier/save")
    @PreAuthorize("@ss.hasPermi('quote:tier:list')")
    public AjaxResult tierSave(@RequestBody final QuotePriceTierSaveRequest request) {
        quoteManageBizService.saveTier(QuoteWebConvert.INSTANCE.toTierParam(request));
        return AjaxResult.success();
    }

    /**
     * 删除价格档位。
     *
     * @param id 价格档位ID
     * @return 操作结果
     */
    @DeleteMapping("/tier/{id}")
    @PreAuthorize("@ss.hasPermi('quote:tier:list')")
    public AjaxResult tierDelete(@PathVariable("id") final Long id) {
        quoteManageBizService.deleteTier(id);
        return AjaxResult.success();
    }

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
     * 查询品牌列表。
     *
     * @return 品牌列表
     */
    @GetMapping("/product/brands")
    @PreAuthorize("@ss.hasPermi('quote:product:list')")
    public AjaxResult brands() {
        return AjaxResult.success(quoteManageBizService.listBrands());
    }

    /**
     * 查询品类列表。
     *
     * @return 品类列表
     */
    @GetMapping("/product/categories")
    @PreAuthorize("@ss.hasPermi('quote:product:list')")
    public AjaxResult categories() {
        return AjaxResult.success(quoteManageBizService.listCategories());
    }
}
