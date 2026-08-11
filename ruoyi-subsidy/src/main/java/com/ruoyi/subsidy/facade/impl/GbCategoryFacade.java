package com.ruoyi.subsidy.facade.impl;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.subsidy.convert.GbCatalogConvert;
import com.ruoyi.subsidy.domain.GbCategory;
import com.ruoyi.subsidy.facade.IGbCategoryFacade;
import com.ruoyi.subsidy.model.bo.GbCategoryBO;
import com.ruoyi.subsidy.model.param.GbCategoryParam;
import com.ruoyi.subsidy.model.query.GbCategoryQuery;
import com.ruoyi.subsidy.service.GbCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 国补分类领域实现。
 */
@Component
@RequiredArgsConstructor
public class GbCategoryFacade implements IGbCategoryFacade {

    private final GbCategoryService categoryService;

    @Override
    public List<GbCategoryBO> list(final GbCategoryQuery query) {
        return GbCatalogConvert.INSTANCE.toCategoryBOList(categoryService.list(DynamicCondition.toWrapper(query)));
    }

    @Override
    public GbCategoryBO getOne(final GbCategoryQuery query) {
        return GbCatalogConvert.INSTANCE.toCategoryBO(categoryService.getOne(DynamicCondition.toWrapper(query)));
    }

    @Override
    public GbCategoryBO save(final GbCategoryParam param) {
        GbCategory category = GbCatalogConvert.INSTANCE.toCategoryEntity(param);
        return GbCatalogConvert.INSTANCE.toCategoryBO(categoryService.saveCategory(category));
    }

    @Override
    public boolean update(final Long id, final GbCategoryParam param) {
        GbCategory category = GbCatalogConvert.INSTANCE.toCategoryEntity(param).setId(id);
        return categoryService.updateCategory(category);
    }
}
