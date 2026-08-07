package com.ruoyi.web.convert.quote;

import com.ruoyi.quote.model.param.QuotePriceTierParam;
import com.ruoyi.quote.model.param.QuoteProductParam;
import com.ruoyi.web.vo.quote.QuotePriceTierSaveRequest;
import com.ruoyi.web.vo.quote.QuoteProductPriceSaveRequest;
import com.ruoyi.web.vo.quote.QuoteProductSaveRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 报价 Web 层对象转换测试。
 */
class QuoteWebConvertTest {

    /**
     * 档位保存请求应转换为领域参数。
     */
    @Test
    void shouldConvertTierSaveRequest() {
        QuotePriceTierSaveRequest request = new QuotePriceTierSaveRequest();
        request.setId(1L);
        request.setTierName("批发价");
        request.setSortOrder(2);

        QuotePriceTierParam param = QuoteWebConvert.INSTANCE.toTierParam(request);

        assertEquals(1L, param.getId());
        assertEquals("批发价", param.getTierName());
        assertEquals(2, param.getSortOrder());
    }

    /**
     * 商品保存请求应转换为领域参数，价格明细应完整保留。
     */
    @Test
    void shouldConvertProductSaveRequest() {
        QuoteProductPriceSaveRequest priceRequest = new QuoteProductPriceSaveRequest();
        priceRequest.setTierId(1L);
        priceRequest.setPrice(new BigDecimal("199.90"));

        QuoteProductSaveRequest request = new QuoteProductSaveRequest();
        request.setBrand("华为");
        request.setCategory("手机");
        request.setProductName("Mate 60");
        request.setSpecName("12G+512G");
        request.setPrices(List.of(priceRequest));

        QuoteProductParam param = QuoteWebConvert.INSTANCE.toProductParam(request);

        assertEquals("华为", param.getBrand());
        assertNotNull(param.getPrices());
        assertEquals(1, param.getPrices().size());
        assertEquals(1L, param.getPrices().get(0).getTierId());
        assertEquals(0, new BigDecimal("199.90").compareTo(param.getPrices().get(0).getPrice()));
    }
}
