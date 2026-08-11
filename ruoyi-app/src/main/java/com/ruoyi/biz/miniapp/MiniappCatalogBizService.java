package com.ruoyi.biz.miniapp;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.subsidy.facade.IGbCategoryFacade;
import com.ruoyi.subsidy.facade.IGbBannerFacade;
import com.ruoyi.subsidy.domain.GbBanner;
import com.ruoyi.subsidy.domain.GbProductImage;
import com.ruoyi.subsidy.facade.IGbProductFacade;
import com.ruoyi.subsidy.facade.IGbProductSkuFacade;
import com.ruoyi.subsidy.model.bo.GbCategoryBO;
import com.ruoyi.subsidy.model.bo.GbProductBO;
import com.ruoyi.subsidy.model.bo.GbProductSkuBO;
import com.ruoyi.subsidy.model.query.GbCategoryQuery;
import com.ruoyi.subsidy.model.query.GbProductQuery;
import com.ruoyi.subsidy.model.query.GbProductSkuQuery;
import com.ruoyi.web.convert.miniapp.MiniappCatalogWebConvert;
import com.ruoyi.web.vo.miniapp.MiniappHomeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 小程序商城目录应用编排。
 */
@Component
@RequiredArgsConstructor
public class MiniappCatalogBizService {

    private final IGbCategoryFacade categoryFacade;
    private final IGbBannerFacade bannerFacade;
    private final IGbProductFacade productFacade;
    private final IGbProductSkuFacade skuFacade;

    /**
     * 查询启用分类。
     *
     * @return 分类列表
     */
    public List<GbCategoryBO> listCategories() {
        return categoryFacade.list(new GbCategoryQuery().setStatus(1));
    }

    /** 查询首页轮播图。 */
    public List<GbBanner> listBanners() {
        return bannerFacade.listEnabled();
    }

    /** 查询商品轮播图。 */
    public List<GbProductImage> listProductImages(final Long productId) {
        return productFacade.listImages(productId);
    }

    /** 聚合首页轮播、分类和推荐商品。 */
    public MiniappHomeVO getHome() {
        MiniappHomeVO home = new MiniappHomeVO();
        home.setBanners(listBanners());
        home.setCategories(MiniappCatalogWebConvert.INSTANCE.toCategoryVOList(listCategories()));
        home.setRecommendedProducts(MiniappCatalogWebConvert.INSTANCE.toProductVOList(productFacade.listRecommended()));
        return home;
    }

    /**
     * 分页查询上架商品。
     *
     * @param categoryId 分类ID
     * @param productName 商品名称
     * @param pageParam 分页参数
     * @return 商品分页
     */
    public PageBO<GbProductBO> pageProducts(final Long categoryId, final String productName,
                                             final PageParamV2 pageParam) {
        GbProductQuery query = new GbProductQuery().setCategoryId(categoryId).setStatus(1)
                .setProductNameLike(productName);
        return productFacade.page(query, pageParam);
    }

    /**
     * 查询商品详情。
     *
     * @param productId 商品ID
     * @return 商品详情
     */
    public GbProductBO getProduct(final Long productId) {
        return productFacade.getOne(new GbProductQuery().setId(productId).setStatus(1));
    }

    /**
     * 查询商品可售 SKU。
     *
     * @param productId 商品ID
     * @return SKU 列表
     */
    public List<GbProductSkuBO> listSkus(final Long productId) {
        return skuFacade.list(new GbProductSkuQuery().setProductId(productId).setStatus(1));
    }

    /**
     * 查询商品所属分类。
     *
     * @param categoryId 分类ID
     * @return 分类详情
     */
    public GbCategoryBO getCategory(final Long categoryId) {
        return categoryFacade.getOne(new GbCategoryQuery().setId(categoryId).setStatus(1));
    }
}
