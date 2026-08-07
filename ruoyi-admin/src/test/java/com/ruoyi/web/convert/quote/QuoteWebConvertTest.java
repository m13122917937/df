package com.ruoyi.web.convert.quote;

import com.ruoyi.quote.model.param.QuoteProductParam;
import com.ruoyi.web.vo.quote.QuoteProductSaveRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 报价 Web 层对象转换测试。
 */
class QuoteWebConvertTest {

    /**
     * 商品保存请求应转换为领域参数，三档价格应完整保留。
     */
    @Test
    void shouldConvertProductSaveRequest() {
        QuoteProductSaveRequest request = new QuoteProductSaveRequest();
        request.setBrand("华为");
        request.setCategory("手机");
        request.setProductName("Mate 60");
        request.setSpecName("12G+512G");
        request.setRetailPrice(new BigDecimal("199.90"));
        request.setDistributor1Price(new BigDecimal("189.90"));
        request.setDistributor2Price(new BigDecimal("179.90"));

        QuoteProductParam param = QuoteWebConvert.INSTANCE.toProductParam(request);

        assertEquals("华为", param.getBrand());
        assertEquals(0, new BigDecimal("199.90").compareTo(param.getRetailPrice()));
        assertEquals(0, new BigDecimal("189.90").compareTo(param.getDistributor1Price()));
        assertEquals(0, new BigDecimal("179.90").compareTo(param.getDistributor2Price()));
    }
}
