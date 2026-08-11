package com.ruoyi.subsidy.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.subsidy.domain.GbCategory;
import com.ruoyi.subsidy.mapper.GbCategoryMapper;
import org.springframework.stereotype.Service;

/**
 * 国补分类领域服务。
 */
@Service
public class GbCategoryService extends ServiceImpl<GbCategoryMapper, GbCategory> {

    /**
     * 保存分类。
     *
     * @param category 分类实体
     * @return 保存后的分类
     */
    public GbCategory saveCategory(final GbCategory category) {
        category.setCreateTime(DateUtil.date()).setUpdateTime(DateUtil.date());
        save(category);
        return category;
    }

    /**
     * 更新分类。
     *
     * @param category 分类实体
     * @return 是否更新成功
     */
    public boolean updateCategory(final GbCategory category) {
        category.setUpdateTime(DateUtil.date());
        return updateById(category);
    }
}
