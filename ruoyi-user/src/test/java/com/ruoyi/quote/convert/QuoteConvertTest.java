package com.ruoyi.quote.convert;

import com.ruoyi.quote.domain.QuoteBrand;
import com.ruoyi.quote.domain.QuoteProduct;
import com.ruoyi.quote.model.bo.QuoteBrandBO;
import com.ruoyi.quote.model.param.QuoteBrandParam;
import com.ruoyi.quote.model.param.QuoteProductParam;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 报价领域对象转换测试。
 */
class QuoteConvertTest {

    /**
     * 品牌参数应转换为品牌实体。
     */
    @Test
    void shouldConvertBrandParamToDomain() {
        QuoteBrand brand = QuoteConvert.INSTANCE.toBrandDomain(
                new QuoteBrandParam().setId(1L).setBrandName("华为").setSortOrder(1));

        assertEquals(1L, brand.getId());
        assertEquals("华为", brand.getBrandName());
        assertEquals(1, brand.getSortOrder());
    }

    /**
     * 商品参数应转换为商品实体，三档价格应完整保留。
     */
    @Test
    void shouldConvertProductParamToDomain() {
        QuoteProductParam param = new QuoteProductParam()
                .setBrandId(1L)
                .setCategoryId(2L)
                .setBrand("华为")
                .setCategory("手机")
                .setProductName("Mate 60")
                .setSpecName("12G+512G")
                .setRetailPrice(new BigDecimal("199.00"))
                .setDistributor1Price(new BigDecimal("189.00"))
                .setDistributor2Price(new BigDecimal("179.00"));

        QuoteProduct product = QuoteConvert.INSTANCE.toProductDomain(param);

        assertEquals(1L, product.getBrandId());
        assertEquals("华为", product.getBrand());
        assertEquals("Mate 60", product.getProductName());
        assertEquals(0, new BigDecimal("199.00").compareTo(product.getRetailPrice()));
        assertEquals(0, new BigDecimal("189.00").compareTo(product.getDistributor1Price()));
        assertEquals(0, new BigDecimal("179.00").compareTo(product.getDistributor2Price()));
    }

    /**
     * 品牌实体应转换为业务对象。
     */
    @Test
    void shouldConvertBrandToBo() {
        QuoteBrand brand = new QuoteBrand();
        brand.setId(3L);
        brand.setBrandName("小米");

        QuoteBrandBO brandBO = QuoteConvert.INSTANCE.toBrandBOList(java.util.List.of(brand)).get(0);

        assertEquals(3L, brandBO.getId());
        assertEquals("小米", brandBO.getBrandName());
    }
}
