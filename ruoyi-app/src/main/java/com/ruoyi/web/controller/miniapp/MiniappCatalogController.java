package com.ruoyi.web.controller.miniapp;

import cn.hutool.core.lang.Assert;
import com.ruoyi.biz.miniapp.MiniappCatalogBizService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.subsidy.model.bo.GbCategoryBO;
import com.ruoyi.subsidy.model.bo.GbProductBO;
import com.ruoyi.web.convert.miniapp.MiniappCatalogWebConvert;
import com.ruoyi.web.form.miniapp.MiniappProductQueryRequest;
import com.ruoyi.web.vo.miniapp.MiniappProductDetailVO;
import com.ruoyi.web.vo.miniapp.MiniappProductVO;
import com.ruoyi.web.vo.miniapp.MiniappHomeVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 小程序商城目录接口。
 */
@RestController
@RequestMapping("/miniapp")
public class MiniappCatalogController extends BaseController {

    private final MiniappCatalogBizService catalogBizService;

    public MiniappCatalogController(final MiniappCatalogBizService catalogBizService) {
        this.catalogBizService = catalogBizService;
    }

    /**
     * 查询首页分类。
     *
     * @return 分类列表
     */
    @Anonymous
    @GetMapping("/categories")
    public AjaxResult categories() {
        return AjaxResult.success(MiniappCatalogWebConvert.INSTANCE.toCategoryVOList(
                catalogBizService.listCategories()));
    }

    /** 查询首页轮播图。 */
    @Anonymous
    @GetMapping("/banners")
    public AjaxResult banners() {
        return AjaxResult.success(catalogBizService.listBanners());
    }

    /** 查询首页聚合数据。 */
    @Anonymous
    @GetMapping("/home")
    public AjaxResult home() {
        MiniappHomeVO home = catalogBizService.getHome();
        return AjaxResult.success(home);
    }

    /**
     * 分页查询上架商品。
     *
     * @param request 查询请求
     * @return 商品分页
     */
    @Anonymous
    @GetMapping("/products")
    public TableDataInfo products(final MiniappProductQueryRequest request) {
        PageBO<GbProductBO> page = catalogBizService.pageProducts(request.getCategoryId(), request.getProductName(),
                new PageParamV2(request.getPageNum(), request.getPageSize(), "sort_order desc, id desc"));
        List<MiniappProductVO> rows = MiniappCatalogWebConvert.INSTANCE.toProductVOList(page.getData());
        return getDataTable(rows, page.getTotal());
    }

    /**
     * 查询商品详情。
     *
     * @param productId 商品ID
     * @return 商品详情
     */
    @Anonymous
    @GetMapping("/products/{productId}")
    public AjaxResult product(@PathVariable final Long productId) {
        GbProductBO product = catalogBizService.getProduct(productId);
        Assert.notNull(product, "商品不存在或已下架");
        GbCategoryBO category = catalogBizService.getCategory(product.getCategoryId());
        Assert.notNull(category, "商品分类不存在或已停用");
        MiniappProductDetailVO result = new MiniappProductDetailVO();
        result.setId(product.getId());
        result.setCategoryId(product.getCategoryId());
        result.setProductName(product.getProductName());
        result.setSubtitle(product.getSubtitle());
        result.setMainImageUrl(product.getMainImageUrl());
        result.setImageUrls(catalogBizService.listProductImages(productId).stream()
                .map(image -> image.getImageUrl()).collect(Collectors.toList()));
        result.setDetailContent(product.getDetailContent());
        result.setDiscountRate(category.getDiscountRate());
        result.setDiscountCapAmount(category.getDiscountCapAmount());
        result.setSaleProvinces(category.getSaleProvinces());
        result.setSkus(MiniappCatalogWebConvert.INSTANCE.toSkuVOList(catalogBizService.listSkus(productId)));
        return AjaxResult.success(result);
    }
}
