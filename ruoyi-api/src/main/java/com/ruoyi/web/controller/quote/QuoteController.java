package com.ruoyi.web.controller.quote;

import com.ruoyi.biz.quote.QuoteBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.quote.model.bo.QuoteBrandBO;
import com.ruoyi.quote.model.bo.QuoteCategoryBO;
import com.ruoyi.quote.model.bo.QuoteProductBO;
import com.ruoyi.web.convert.quote.QuoteWebConvert;
import com.ruoyi.web.vo.quote.QuoteBrandVO;
import com.ruoyi.web.vo.quote.QuoteCategoryVO;
import com.ruoyi.web.vo.quote.QuoteProductListRequest;
import com.ruoyi.web.vo.quote.QuoteProductListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 批发报价展示接口。
 */
@RestController
@RequestMapping("/quote/web")
@RequiredArgsConstructor
public class QuoteController extends BaseController {

    private final QuoteBizService quoteBizService;

    /**
     * 分页查询批发报价商品（含各档位价格）。
     *
     * @param request 查询请求
     * @return 报价商品分页数据
     */
    @PostMapping("/product/list")
    public TableDataInfo productList(@RequestBody final QuoteProductListRequest request) {
        PageBO<QuoteProductBO> page = quoteBizService.pageProducts(
                QuoteWebConvert.INSTANCE.toProductQuery(request), startParamV2("sort_order asc, id asc"));
        List<QuoteProductListVO> rows = QuoteWebConvert.INSTANCE.toProductVOList(page.getData());
        return getDataTable(rows, page.getTotal());
    }

    /**
     * 查询品牌列表。
     *
     * @return 品牌列表
     */
    @GetMapping("/brand/list")
    public AjaxResult brandList() {
        List<QuoteBrandBO> brands = quoteBizService.listBrands();
        return AjaxResult.success(QuoteWebConvert.INSTANCE.toBrandVOList(brands));
    }

    /**
     * 查询品类列表。
     *
     * @return 品类列表
     */
    @GetMapping("/category/list")
    public AjaxResult categoryList() {
        List<QuoteCategoryBO> categories = quoteBizService.listCategories();
        return AjaxResult.success(QuoteWebConvert.INSTANCE.toCategoryVOList(categories));
    }
}
