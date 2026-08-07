package com.ruoyi.quote.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.quote.convert.QuoteConvert;
import com.ruoyi.quote.domain.QuoteCategory;
import com.ruoyi.quote.mapper.QuoteCategoryMapper;
import com.ruoyi.quote.mapper.QuoteProductMapper;
import com.ruoyi.quote.model.param.QuoteCategoryParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 报价品类服务。
 */
@Service
@RequiredArgsConstructor
public class QuoteCategoryService extends ServiceImpl<QuoteCategoryMapper, QuoteCategory> {

    private final QuoteProductMapper quoteProductMapper;

    /**
     * 新增或更新报价品类。
     *
     * @param param 品类参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveCategory(final QuoteCategoryParam param) {
        if (param.getCategoryName() == null || param.getCategoryName().isBlank()) {
            throw new ServiceException("品类名称不能为空");
        }
        LocalDateTime now = LocalDateTime.now();
        QuoteCategory category = QuoteConvert.INSTANCE.toCategoryDomain(param);
        if (category.getId() == null) {
            category.setDeleted(0L);
            category.setCreateTime(now);
            category.setUpdateTime(now);
            save(category);
        } else {
            category.setUpdateTime(now);
            updateById(category);
        }
    }

    /**
     * 删除报价品类；已被商品引用时拒绝删除。
     *
     * @param id 品类ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(final Long id) {
        long referenced = quoteProductMapper.countByCategoryId(id);
        if (referenced > 0) {
            throw new ServiceException("该品类已被商品引用，无法删除");
        }
        removeById(id);
    }
}
