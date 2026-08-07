package com.ruoyi.quote.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.quote.domain.QuotePriceTier;
import com.ruoyi.quote.domain.QuoteProduct;
import com.ruoyi.quote.mapper.QuotePriceTierMapper;
import com.ruoyi.quote.mapper.QuoteProductMapper;
import com.ruoyi.quote.mapper.QuoteProductPriceMapper;
import com.ruoyi.quote.model.param.QuotePriceTierParam;
import com.ruoyi.quote.model.param.QuoteProductParam;
import com.ruoyi.quote.model.param.QuoteProductPriceParam;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 报价服务事务与删除约束测试。
 */
@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {

    @Mock
    private QuoteProductMapper quoteProductMapper;

    @Mock
    private QuoteProductPriceMapper quoteProductPriceMapper;

    @Mock
    private QuotePriceTierMapper quotePriceTierMapper;

    @InjectMocks
    private QuoteProductService quoteProductService;

    private QuotePriceTierService quotePriceTierService;

    /**
     * 初始化被测服务。
     */
    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), ""), QuoteProduct.class);
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), ""), QuotePriceTier.class);
        ReflectionTestUtils.setField(quoteProductService, "baseMapper", quoteProductMapper);
        quotePriceTierService = new QuotePriceTierService(quoteProductPriceMapper);
        ReflectionTestUtils.setField(quotePriceTierService, "baseMapper", quotePriceTierMapper);
    }

    /**
     * 保存商品时价格列表为空应拒绝。
     */
    @Test
    void shouldRejectSaveWhenPricesEmpty() {
        QuoteProductParam param = validProduct().setPrices(List.of());

        assertThrows(ServiceException.class, () -> quoteProductService.saveWithPrices(param));
        verify(quoteProductPriceMapper, never()).deleteByProductId(any());
    }

    /**
     * 保存商品时价格为空或负数应拒绝。
     */
    @Test
    void shouldRejectSaveWhenPriceInvalid() {
        QuoteProductParam negativePrice = validProduct()
                .setPrices(List.of(new QuoteProductPriceParam().setTierId(1L).setPrice(new BigDecimal("-1"))));

        assertThrows(ServiceException.class, () -> quoteProductService.saveWithPrices(negativePrice));

        QuoteProductParam nullPrice = validProduct()
                .setPrices(List.of(new QuoteProductPriceParam().setTierId(1L).setPrice(null)));
        assertThrows(ServiceException.class, () -> quoteProductService.saveWithPrices(nullPrice));
    }

    /**
     * 保存商品成功后应先删除旧价格，再写入全部档位价格。
     */
    @Test
    void shouldRebuildPricesWhenSaveSucceeds() {
        QuoteProductParam param = validProduct().setId(10L).setPrices(List.of(
                new QuoteProductPriceParam().setTierId(1L).setPrice(new BigDecimal("100.00")),
                new QuoteProductPriceParam().setTierId(2L).setPrice(new BigDecimal("95.00"))));

        quoteProductService.saveWithPrices(param);

        verify(quoteProductMapper).updateById(any(QuoteProduct.class));
        verify(quoteProductPriceMapper).deleteByProductId(10L);
        verify(quoteProductPriceMapper, times(2)).insert(any());
    }

    /**
     * 删除商品时应同时物理删除其价格明细。
     */
    @Test
    void shouldDeleteProductWithPrices() {
        quoteProductService.deleteWithPrices(10L);

        verify(quoteProductMapper).deleteById(10L);
        verify(quoteProductPriceMapper).deleteByProductId(10L);
    }

    /**
     * 档位已被商品价格引用时删除应被拒绝。
     */
    @Test
    void shouldRejectDeleteTierWhenReferenced() {
        when(quoteProductPriceMapper.countByTierId(3L)).thenReturn(2L);

        assertThrows(ServiceException.class, () -> quotePriceTierService.deleteTier(3L));
        verify(quotePriceTierMapper, never()).deleteById(3L);
    }

    /**
     * 档位未被引用时可正常逻辑删除。
     */
    @Test
    void shouldDeleteTierWhenNotReferenced() {
        when(quoteProductPriceMapper.countByTierId(3L)).thenReturn(0L);

        assertDoesNotThrow(() -> quotePriceTierService.deleteTier(3L));
        verify(quotePriceTierMapper).deleteById(3L);
    }

    private QuoteProductParam validProduct() {
        return new QuoteProductParam()
                .setBrand("华为")
                .setCategory("手机")
                .setProductName("Mate 60")
                .setSpecName("12G+512G")
                .setSortOrder(1);
    }
}
