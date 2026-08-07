package com.ruoyi.quote.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.quote.convert.QuoteConvert;
import com.ruoyi.quote.domain.QuotePriceTier;
import com.ruoyi.quote.mapper.QuotePriceTierMapper;
import com.ruoyi.quote.mapper.QuoteProductPriceMapper;
import com.ruoyi.quote.model.param.QuotePriceTierParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 报价价格档位服务。
 */
@Service
@RequiredArgsConstructor
public class QuotePriceTierService extends ServiceImpl<QuotePriceTierMapper, QuotePriceTier> {

    private final QuoteProductPriceMapper quoteProductPriceMapper;

    /**
     * 新增或更新价格档位。
     *
     * @param param 价格档位参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveTier(final QuotePriceTierParam param) {
        if (param.getTierName() == null || param.getTierName().isBlank()) {
            throw new ServiceException("档位名称不能为空");
        }
        LocalDateTime now = LocalDateTime.now();
        QuotePriceTier tier = QuoteConvert.INSTANCE.toTierDomain(param);
        if (tier.getId() == null) {
            tier.setDeleted(0L);
            tier.setCreateTime(now);
            tier.setUpdateTime(now);
            save(tier);
        } else {
            tier.setUpdateTime(now);
            updateById(tier);
        }
    }

    /**
     * 删除价格档位；已被商品价格引用时拒绝删除。
     *
     * @param id 价格档位ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteTier(final Long id) {
        long referenced = quoteProductPriceMapper.countByTierId(id);
        if (referenced > 0) {
            throw new ServiceException("该档位已被商品价格引用，无法删除");
        }
        removeById(id);
    }
}
