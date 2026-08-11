package com.ruoyi.subsidy.facade;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.subsidy.model.bo.GbProductBO;
import com.ruoyi.subsidy.model.param.GbProductParam;
import com.ruoyi.subsidy.model.query.GbProductQuery;

import java.util.List;

/**
 * 国补商品领域出口。
 */
public interface IGbProductFacade {

    /**
     * 查询商品分页。
     *
     * @param query 查询条件
     * @param pageParam 分页参数
     * @return 商品分页
     */
    PageBO<GbProductBO> page(GbProductQuery query, PageParamV2 pageParam);

    /**
     * 查询商品详情。
     *
     * @param query 查询条件
     * @return 商品详情
     */
    GbProductBO getOne(GbProductQuery query);

    /**
     * 保存商品。
     *
     * @param param 保存参数
     * @return 保存后的商品
     */
    GbProductBO save(GbProductParam param);

    /**
     * 更新商品。
     *
     * @param id 商品ID
     * @param param 更新参数
     * @return 是否更新成功
     */
    boolean update(Long id, GbProductParam param);

    /**
     * 查询商品 SKU。
     *
     * @param productId 商品ID
     * @return SKU 列表
     */
    List<GbProductBO> listRecommended();

    /** 查询商品轮播图。 */
    java.util.List<com.ruoyi.subsidy.domain.GbProductImage> listImages(Long productId);
}
