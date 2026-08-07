package com.ruoyi.quote.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.quote.convert.QuoteConvert;
import com.ruoyi.quote.domain.QuoteProduct;
import com.ruoyi.quote.domain.QuoteProductPrice;
import com.ruoyi.quote.mapper.QuoteProductMapper;
import com.ruoyi.quote.mapper.QuoteProductPriceMapper;
import com.ruoyi.quote.model.param.QuoteProductParam;
import com.ruoyi.quote.model.param.QuoteProductPriceParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 报价商品服务。
 */
@Service
@RequiredArgsConstructor
public class QuoteProductService extends ServiceImpl<QuoteProductMapper, QuoteProduct> {

    private final QuoteProductPriceMapper quoteProductPriceMapper;

    /**
     * 新增或更新报价商品，并整体重建各档位价格。
     *
     * @param param 商品参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveWithPrices(final QuoteProductParam param) {
        if (param.getBrand() == null || param.getBrand().isBlank()) {
            throw new ServiceException("品牌不能为空");
        }
        if (param.getProductName() == null || param.getProductName().isBlank()) {
            throw new ServiceException("商品名不能为空");
        }
        if (param.getPrices() == null || param.getPrices().isEmpty()) {
            throw new ServiceException("至少需要填写一个档位价格");
        }
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
        rebuildPrices(product.getId(), param.getPrices(), now);
    }

    /**
     * 删除报价商品（逻辑删除）及其价格明细（物理删除）。
     *
     * @param id 商品ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteWithPrices(final Long id) {
        removeById(id);
        quoteProductPriceMapper.deleteByProductId(id);
    }

    /**
     * 查询去重后的品牌列表。
     *
     * @return 品牌列表
     */
    public List<String> listBrands() {
        return baseMapper.selectBrands();
    }

    /**
     * 查询去重后的品类列表。
     *
     * @return 品类列表
     */
    public List<String> listCategories() {
        return baseMapper.selectCategories();
    }

    private void rebuildPrices(final Long productId, final List<QuoteProductPriceParam> priceParams,
                               final LocalDateTime now) {
        quoteProductPriceMapper.deleteByProductId(productId);
        for (QuoteProductPriceParam priceParam : priceParams) {
            if (priceParam.getTierId() == null) {
                throw new ServiceException("价格档位不能为空");
            }
            BigDecimal price = priceParam.getPrice();
            if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
                throw new ServiceException("价格必须为不小于 0 的数字");
            }
            QuoteProductPrice priceDomain = QuoteConvert.INSTANCE.toPriceDomain(priceParam);
            priceDomain.setProductId(productId);
            priceDomain.setCreateTime(now);
            priceDomain.setUpdateTime(now);
            quoteProductPriceMapper.insert(priceDomain);
        }
    }
}
