package com.ruoyi.analysis;

import com.ruoyi.analysis.mapper.AnalysisCostConfigMapper;
import com.ruoyi.analysis.model.param.AnalysisCostConfigParam;
import com.ruoyi.analysis.service.AnalysisCostConfigService;
import com.ruoyi.common.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 核算配置业务口径校验测试。
 */
class AnalysisCostConfigValidationTest {

    @Test
    void shouldAllowMarginWithPlatformShopAndAmountOnly() {
        AnalysisCostConfigParam param = new AnalysisCostConfigParam();
        param.setConfigType("MARGIN");
        param.setPlatform("拼多多");
        param.setShopName("测试店铺");
        param.setAmount(new BigDecimal("1000"));
        AnalysisCostConfigService service = new AnalysisCostConfigService();
        ReflectionTestUtils.setField(service, "baseMapper", Mockito.mock(AnalysisCostConfigMapper.class));

        assertDoesNotThrow(() -> service.saveConfig(param));
    }

    @Test
    void shouldRejectInternalCostWithoutHeadcount() {
        AnalysisCostConfigParam param = monthlyParam("INTERNAL_COST");
        param.setExtraData("{\"costScope\":\"DIRECT\"}");

        assertThrows(ServiceException.class, () -> new AnalysisCostConfigService().saveConfig(param));
    }

    private AnalysisCostConfigParam monthlyParam(String configType) {
        AnalysisCostConfigParam param = new AnalysisCostConfigParam();
        param.setConfigType(configType);
        param.setMonthValue("2026-07");
        param.setAmount(new BigDecimal("1000"));
        return param;
    }
}
