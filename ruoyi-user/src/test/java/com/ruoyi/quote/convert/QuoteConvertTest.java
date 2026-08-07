package com.ruoyi.quote.convert;

import com.ruoyi.quote.domain.QuotePriceTier;
import com.ruoyi.quote.domain.QuoteProduct;
import com.ruoyi.quote.domain.QuoteProductPrice;
import com.ruoyi.quote.model.bo.QuotePriceTierBO;
import com.ruoyi.quote.model.bo.QuoteProductBO;
import com.ruoyi.quote.model.bo.QuoteProductPriceBO;
import com.ruoyi.quote.model.param.QuotePriceTierParam;
import com.ruoyi.quote.model.param.QuoteProductParam;
import com.ruoyi.quote.model.param.QuoteProductPriceParam;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 报价领域对象转换测试。
 */
class QuoteConvertTest {

    /**
     * 档位参数应转换为档位实体。
     */
    @Test
    void shouldConvertTierParamToDomain() {
        QuotePriceTier tier = QuoteConvert.INSTANCE.toTierDomain(
                new QuotePriceTierParam().setId(1L).setTierName("批发价").setSortOrder(1));

        assertEquals(1L, tier.getId());
        assertEquals("批发价", tier.getTierName());
        assertEquals(1, tier.getSortOrder());
    }

    /**
     * 商品参数应转换为商品实体，价格明细应完整保留。
     */
    @Test
    void shouldConvertProductParamToDomain() {
        QuoteProductParam param = new QuoteProductParam()
                .setId(2L)
                .setBrand("华为")
                .setCategory("手机")
                .setProductName("Mate 60")
                .setSpecName("12G+512G");

        QuoteProduct product = QuoteConvert.INSTANCE.toProductDomain(param);

        assertEquals(2L, product.getId());
        assertEquals("华为", product.getBrand());
        assertEquals("Mate 60", product.getProductName());
    }

    /**
     * 实体应转换为业务对象，价格明细应完整保留。
     */
    @Test
    void shouldConvertDomainToBo() {
        QuoteProductPrice price = new QuoteProductPrice();
        price.setProductId(2L);
        price.setTierId(3L);
        price.setPrice(new BigDecimal("1288.00"));

        QuoteProductPriceBO priceBO = QuoteConvert.INSTANCE.toPriceBO(price);
        QuotePriceTier tier = new QuotePriceTier();
        tier.setId(3L);
        tier.setTierName("批发价");
        QuotePriceTierBO tierBO = QuoteConvert.INSTANCE.toTierBO(tier);

        assertNotNull(priceBO);
        assertEquals(3L, priceBO.getTierId());
        assertEquals(0, new BigDecimal("1288.00").compareTo(priceBO.getPrice()));
        assertEquals("批发价", tierBO.getTierName());
    }
}
