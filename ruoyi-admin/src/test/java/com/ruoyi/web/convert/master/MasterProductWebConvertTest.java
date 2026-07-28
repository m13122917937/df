package com.ruoyi.web.convert.master;

import com.ruoyi.product.model.bo.ProductSkuBO;
import com.ruoyi.product.model.query.ProductSkuQuery;
import com.ruoyi.web.vo.master.MasterProductQueryRequest;
import com.ruoyi.web.vo.master.MasterProductVO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 商品主数据 Web 转换器测试。
 */
class MasterProductWebConvertTest {

    /**
     * 验证查询和列表响应字段转换。
     */
    @Test
    void shouldConvertProductQueryAndResponse() {
        MasterProductQueryRequest request = new MasterProductQueryRequest();
        request.setBrand("荣耀");
        request.setSkuCode("SKU-001");

        ProductSkuQuery query = MasterProductWebConvert.INSTANCE.toQuery(request);
        ProductSkuBO product = new ProductSkuBO().setSkuCode("SKU-001").setProductName("测试商品");
        List<MasterProductVO> result = MasterProductWebConvert.INSTANCE.toVOList(List.of(product));

        assertEquals("荣耀", query.getBrand());
        assertEquals("SKU-001", query.getSkuCode());
        assertEquals("测试商品", result.get(0).getProductName());
    }
}
