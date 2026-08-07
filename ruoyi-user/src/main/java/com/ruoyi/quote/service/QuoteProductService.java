package com.ruoyi.quote.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.quote.convert.QuoteConvert;
import com.ruoyi.quote.domain.QuoteBrand;
import com.ruoyi.quote.domain.QuoteCategory;
import com.ruoyi.quote.domain.QuoteProduct;
import com.ruoyi.quote.mapper.QuoteBrandMapper;
import com.ruoyi.quote.mapper.QuoteCategoryMapper;
import com.ruoyi.quote.mapper.QuoteProductMapper;
import com.ruoyi.quote.model.param.QuoteProductParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 报价商品服务。
 */
@Service
@RequiredArgsConstructor
public class QuoteProductService extends ServiceImpl<QuoteProductMapper, QuoteProduct> {

    private final QuoteBrandMapper quoteBrandMapper;
    private final QuoteCategoryMapper quoteCategoryMapper;

    /**
     * 新增或更新报价商品（含零售、分销1、分销2 三档价格，价格可留空）。
     *
     * @param param 商品参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveProduct(final QuoteProductParam param) {
        validateBrandAndCategory(param);
        LocalDateTime now = LocalDateTime.now();
        QuoteProduct product = QuoteConvert.INSTANCE.toProductDomain(param);
        if (product.getId() == null) {
            product.setDeleted(0L);
            product.setCreateTime(now);
            product.setUpdateTime(now);
            save(product);
        } else {
            product.setUpdateTime(now);
            updateById(product);
        }
    }

    /**
     * 删除报价商品（逻辑删除）。
     *
     * @param id 商品ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(final Long id) {
        removeById(id);
    }

    /**
     * 查询去重后的品牌列表（兼容旧接口，暂保留）。
     *
     * @return 品牌列表
     */
    public List<String> listBrands() {
        return baseMapper.selectBrands();
    }

    /**
     * 查询去重后的品类列表（兼容旧接口，暂保留）。
     *
     * @return 品类列表
     */
    public List<String> listCategories() {
        return baseMapper.selectCategories();
    }

    private void validateBrandAndCategory(final QuoteProductParam param) {
        if (param.getBrandId() == null) {
            throw new ServiceException("请选择品牌");
        }
        QuoteBrand brand = quoteBrandMapper.selectById(param.getBrandId());
        if (brand == null) {
            throw new ServiceException("品牌不存在或已删除，请重新选择");
        }
        if (param.getCategoryId() == null) {
            throw new ServiceException("请选择品类");
        }
        QuoteCategory category = quoteCategoryMapper.selectById(param.getCategoryId());
        if (category == null) {
            throw new ServiceException("品类不存在或已删除，请重新选择");
        }
        param.setBrand(brand.getBrandName());
        param.setCategory(category.getCategoryName());
    }

}
