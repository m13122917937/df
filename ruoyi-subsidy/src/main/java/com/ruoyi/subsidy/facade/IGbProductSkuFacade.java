package com.ruoyi.subsidy.facade;

import com.ruoyi.subsidy.model.bo.GbProductSkuBO;
import com.ruoyi.subsidy.model.param.GbProductSkuParam;
import com.ruoyi.subsidy.model.query.GbProductSkuQuery;

import java.util.List;

/**
 * 国补 SKU 领域出口。
 */
public interface IGbProductSkuFacade {

    /**
     * 查询 SKU 列表。
     *
     * @param query 查询条件
     * @return SKU 列表
     */
    List<GbProductSkuBO> list(GbProductSkuQuery query);

    /**
     * 查询 SKU 详情。
     *
     * @param query 查询条件
     * @return SKU 详情
     */
    GbProductSkuBO getOne(GbProductSkuQuery query);

    /**
     * 保存 SKU。
     *
     * @param param 保存参数
     * @return 保存后的 SKU
     */
    GbProductSkuBO save(GbProductSkuParam param);

    /**
     * 更新 SKU。
     *
     * @param id SKU ID
     * @param param 更新参数
     * @return 是否更新成功
     */
    boolean update(Long id, GbProductSkuParam param);

    /** 后台调整 SKU 库存。 */
    void adjustInventory(Long skuId, Integer delta, String remark);
}
