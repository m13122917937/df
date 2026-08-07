package com.ruoyi.quote.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.quote.convert.QuoteConvert;
import com.ruoyi.quote.domain.QuotePriceHistory;
import com.ruoyi.quote.mapper.QuotePriceHistoryMapper;
import com.ruoyi.quote.mapper.QuoteProductMapper;
import com.ruoyi.quote.model.param.QuotePriceHistoryParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 报价流水服务。
 */
@Service
@RequiredArgsConstructor
public class QuotePriceHistoryService extends ServiceImpl<QuotePriceHistoryMapper, QuotePriceHistory> {

    private final QuoteProductMapper quoteProductMapper;

    /**
     * 保存当天报价（商品 × 日期 幂等覆盖）。
     *
     * @param param 报价流水参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveQuote(final QuotePriceHistoryParam param) {
        if (param.getProductId() == null) {
            throw new ServiceException("商品ID不能为空");
        }
        if (quoteProductMapper.selectById(param.getProductId()) == null) {
            throw new ServiceException("商品不存在或已删除");
        }
        BigDecimal retail = param.getRetailPrice();
        BigDecimal distributor1 = param.getDistributor1Price();
        BigDecimal distributor2 = param.getDistributor2Price();
        if (retail == null && distributor1 == null && distributor2 == null) {
            throw new ServiceException("至少需要填写一个价格");
        }
        if (isNegative(retail) || isNegative(distributor1) || isNegative(distributor2)) {
            throw new ServiceException("价格必须为不小于 0 的数字");
        }
        LocalDateTime now = LocalDateTime.now();
        QuotePriceHistory history = QuoteConvert.INSTANCE.toPriceHistoryDomain(param);
        history.setQuoteDate(param.getQuoteDate() != null ? param.getQuoteDate() : LocalDate.now());
        history.setCreateTime(now);
        history.setUpdateTime(now);
        baseMapper.upsertByProductAndDate(history);
    }

    /**
     * 查询多个商品各自最新一条报价。
     *
     * @param productIds 商品ID集合
     * @return 最新报价集合
     */
    public List<QuotePriceHistory> listLatestByProductIds(final List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }
        return baseMapper.selectLatestByProductIds(productIds);
    }

    private boolean isNegative(final BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) < 0;
    }
}
