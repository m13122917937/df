package com.ruoyi.quote.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.quote.domain.QuoteBrand;
import com.ruoyi.quote.domain.QuoteCategory;
import com.ruoyi.quote.domain.QuotePriceHistory;
import com.ruoyi.quote.domain.QuoteProduct;
import com.ruoyi.quote.mapper.QuoteBrandMapper;
import com.ruoyi.quote.mapper.QuoteCategoryMapper;
import com.ruoyi.quote.mapper.QuotePriceHistoryMapper;
import com.ruoyi.quote.mapper.QuoteProductMapper;
import com.ruoyi.quote.model.param.QuotePriceHistoryParam;
import com.ruoyi.quote.model.param.QuoteProductParam;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 报价服务事务与校验测试。
 */
@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {

    @Mock
    private QuoteProductMapper quoteProductMapper;

    @Mock
    private QuoteBrandMapper quoteBrandMapper;

    @Mock
    private QuoteCategoryMapper quoteCategoryMapper;

    @Mock
    private QuotePriceHistoryMapper quotePriceHistoryMapper;

    @InjectMocks
    private QuoteProductService quoteProductService;

    private QuotePriceHistoryService quotePriceHistoryService;

    /**
     * 初始化被测服务。
     */
    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), ""), QuoteProduct.class);
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), ""), QuoteBrand.class);
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), ""), QuoteCategory.class);
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), ""), QuotePriceHistory.class);
        ReflectionTestUtils.setField(quoteProductService, "baseMapper", quoteProductMapper);
        quotePriceHistoryService = new QuotePriceHistoryService(quoteProductMapper);
        ReflectionTestUtils.setField(quotePriceHistoryService, "baseMapper", quotePriceHistoryMapper);
    }

    /**
     * 商品库保存时未选择品牌应拒绝。
     */
    @Test
    void shouldRejectSaveWhenBrandMissing() {
        QuoteProductParam param = validProduct().setBrandId(null);

        assertThrows(ServiceException.class, () -> quoteProductService.saveProduct(param));
    }

    /**
     * 商品库保存时品牌不存在应拒绝。
     */
    @Test
    void shouldRejectSaveWhenBrandNotExist() {
        when(quoteBrandMapper.selectById(1L)).thenReturn(null);

        assertThrows(ServiceException.class, () -> quoteProductService.saveProduct(validProduct()));
    }

    /**
     * 商品库保存基础信息成功。
     */
    @Test
    void shouldSaveProductSucceeds() {
        when(quoteBrandMapper.selectById(1L)).thenReturn(createBrand());
        when(quoteCategoryMapper.selectById(2L)).thenReturn(createCategory());

        quoteProductService.saveProduct(validProduct());

        verify(quoteProductMapper).insert(any(QuoteProduct.class));
    }

    /**
     * 删除商品为逻辑删除。
     */
    @Test
    void shouldDeleteProduct() {
        quoteProductService.deleteProduct(10L);

        verify(quoteProductMapper).deleteById(10L);
    }

    /**
     * 保存当天报价时商品不存在应拒绝。
     */
    @Test
    void shouldRejectSaveQuoteWhenProductMissing() {
        when(quoteProductMapper.selectById(99L)).thenReturn(null);

        assertThrows(ServiceException.class, () -> quotePriceHistoryService.saveQuote(
                new QuotePriceHistoryParam().setProductId(99L).setRetailPrice(new BigDecimal("100"))));
    }

    /**
     * 保存当天报价时三档价格均为空应拒绝。
     */
    @Test
    void shouldRejectSaveQuoteWhenAllPricesEmpty() {
        when(quoteProductMapper.selectById(10L)).thenReturn(new QuoteProduct());

        assertThrows(ServiceException.class, () -> quotePriceHistoryService.saveQuote(
                new QuotePriceHistoryParam().setProductId(10L)));
    }

    /**
     * 保存当天报价成功时应幂等写入（upsert）。
     */
    @Test
    void shouldUpsertQuoteWhenSaveSucceeds() {
        when(quoteProductMapper.selectById(10L)).thenReturn(new QuoteProduct());
        QuotePriceHistoryParam param = new QuotePriceHistoryParam()
                .setProductId(10L)
                .setRetailPrice(new BigDecimal("200.00"))
                .setDistributor1Price(new BigDecimal("190.00"))
                .setDistributor2Price(new BigDecimal("180.00"));

        quotePriceHistoryService.saveQuote(param);

        verify(quotePriceHistoryMapper).upsertByProductAndDate(any(QuotePriceHistory.class));
        verify(quotePriceHistoryMapper, never()).insert(any(QuotePriceHistory.class));
    }

    private QuoteProductParam validProduct() {
        return new QuoteProductParam()
                .setBrandId(1L)
                .setCategoryId(2L)
                .setProductName("Mate 60")
                .setSpecName("12G+512G")
                .setSortOrder(1);
    }

    private QuoteBrand createBrand() {
        QuoteBrand brand = new QuoteBrand();
        brand.setId(1L);
        brand.setBrandName("华为");
        return brand;
    }

    private QuoteCategory createCategory() {
        QuoteCategory category = new QuoteCategory();
        category.setId(2L);
        category.setCategoryName("手机");
        return category;
    }
}
