package com.ruoyi.quote.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.quote.domain.QuoteBrand;
import com.ruoyi.quote.domain.QuoteCategory;
import com.ruoyi.quote.domain.QuoteProduct;
import com.ruoyi.quote.mapper.QuoteBrandMapper;
import com.ruoyi.quote.mapper.QuoteCategoryMapper;
import com.ruoyi.quote.mapper.QuoteProductMapper;
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

    @InjectMocks
    private QuoteProductService quoteProductService;

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
        ReflectionTestUtils.setField(quoteProductService, "baseMapper", quoteProductMapper);
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
     * 商品库保存时三档价格均为空应拒绝。
     */
    @Test
    void shouldRejectSaveWhenAllPricesEmpty() {
        when(quoteBrandMapper.selectById(1L)).thenReturn(createBrand());
        when(quoteCategoryMapper.selectById(2L)).thenReturn(createCategory());

        assertThrows(ServiceException.class, () -> quoteProductService.saveProduct(validProduct()));
    }

    /**
     * 商品保存时价格为负数应拒绝。
     */
    @Test
    void shouldRejectSaveWhenPriceNegative() {
        when(quoteBrandMapper.selectById(1L)).thenReturn(createBrand());
        when(quoteCategoryMapper.selectById(2L)).thenReturn(createCategory());
        QuoteProductParam param = validProduct().setRetailPrice(new BigDecimal("-1"));

        assertThrows(ServiceException.class, () -> quoteProductService.saveProduct(param));
    }

    /**
     * 商品库保存基础信息并携带价格时应成功保存。
     */
    @Test
    void shouldSaveProductWithPrices() {
        when(quoteBrandMapper.selectById(1L)).thenReturn(createBrand());
        when(quoteCategoryMapper.selectById(2L)).thenReturn(createCategory());
        QuoteProductParam param = validProduct()
                .setRetailPrice(new BigDecimal("199.00"))
                .setDistributor1Price(new BigDecimal("189.00"))
                .setDistributor2Price(new BigDecimal("179.00"));

        quoteProductService.saveProduct(param);

        verify(quoteProductMapper).insert(any(QuoteProduct.class));
    }

    /**
     * 报价更新时商品不存在应拒绝。
     */
    @Test
    void shouldRejectSavePricesWhenProductMissing() {
        when(quoteProductMapper.selectById(99L)).thenReturn(null);
        QuoteProductParam param = validProduct().setId(99L).setRetailPrice(new BigDecimal("100"));

        assertThrows(ServiceException.class, () -> quoteProductService.savePrices(param));
    }

    /**
     * 报价更新时三档价格均为空应拒绝。
     */
    @Test
    void shouldRejectSavePricesWhenAllPricesEmpty() {
        QuoteProductParam param = validProduct().setId(10L);

        assertThrows(ServiceException.class, () -> quoteProductService.savePrices(param));
    }

    /**
     * 报价更新成功时应仅更新价格字段。
     */
    @Test
    void shouldUpdatePricesWhenSavePricesSucceeds() {
        when(quoteProductMapper.selectById(10L)).thenReturn(new QuoteProduct());
        QuoteProductParam param = validProduct().setId(10L)
                .setRetailPrice(new BigDecimal("200.00"))
                .setDistributor1Price(new BigDecimal("190.00"))
                .setDistributor2Price(new BigDecimal("180.00"));

        quoteProductService.savePrices(param);

        verify(quoteProductMapper).updateById(any(QuoteProduct.class));
        verify(quoteProductMapper, never()).insert(any(QuoteProduct.class));
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
