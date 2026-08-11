package com.ruoyi.subsidy.facade;

import com.ruoyi.subsidy.model.bo.GbCategoryBO;
import com.ruoyi.subsidy.model.param.GbCategoryParam;
import com.ruoyi.subsidy.model.query.GbCategoryQuery;

import java.util.List;

/**
 * 国补分类领域出口。
 */
public interface IGbCategoryFacade {

    /**
     * 查询分类列表。
     *
     * @param query 查询条件
     * @return 分类列表
     */
    List<GbCategoryBO> list(GbCategoryQuery query);

    /**
     * 查询分类详情。
     *
     * @param query 查询条件
     * @return 分类详情
     */
    GbCategoryBO getOne(GbCategoryQuery query);

    /**
     * 保存分类。
     *
     * @param param 保存参数
     * @return 保存后的分类
     */
    GbCategoryBO save(GbCategoryParam param);

    /**
     * 更新分类。
     *
     * @param id 分类ID
     * @param param 更新参数
     * @return 是否更新成功
     */
    boolean update(Long id, GbCategoryParam param);
}
