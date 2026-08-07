package com.ruoyi.quote.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.quote.convert.QuoteConvert;
import com.ruoyi.quote.domain.QuoteBrand;
import com.ruoyi.quote.mapper.QuoteBrandMapper;
import com.ruoyi.quote.mapper.QuoteProductMapper;
import com.ruoyi.quote.model.param.QuoteBrandParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 报价品牌服务。
 */
@Service
@RequiredArgsConstructor
public class QuoteBrandService extends ServiceImpl<QuoteBrandMapper, QuoteBrand> {

    private final QuoteProductMapper quoteProductMapper;

    /**
     * 新增或更新报价品牌。
     *
     * @param param 品牌参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveBrand(final QuoteBrandParam param) {
        if (param.getBrandName() == null || param.getBrandName().isBlank()) {
            throw new ServiceException("品牌名称不能为空");
        }
        LocalDateTime now = LocalDateTime.now();
        QuoteBrand brand = QuoteConvert.INSTANCE.toBrandDomain(param);
        if (brand.getId() == null) {
            brand.setDeleted(0L);
            brand.setCreateTime(now);
            brand.setUpdateTime(now);
            save(brand);
        } else {
            brand.setUpdateTime(now);
            updateById(brand);
        }
    }

    /**
     * 删除报价品牌；已被商品引用时拒绝删除。
     *
     * @param id 品牌ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBrand(final Long id) {
        long referenced = quoteProductMapper.countByBrandId(id);
        if (referenced > 0) {
            throw new ServiceException("该品牌已被商品引用，无法删除");
        }
        removeById(id);
    }
}
