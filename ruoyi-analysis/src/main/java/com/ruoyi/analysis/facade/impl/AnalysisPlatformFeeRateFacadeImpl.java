package com.ruoyi.analysis.facade.impl;

import com.ruoyi.analysis.convert.AnalysisConvert;
import com.ruoyi.analysis.domain.AnalysisPlatformFeeRate;
import com.ruoyi.analysis.facade.AnalysisPlatformFeeRateFacade;
import com.ruoyi.analysis.model.bo.AnalysisPlatformFeeRateBO;
import com.ruoyi.analysis.model.param.AnalysisPlatformFeeRateParam;
import com.ruoyi.analysis.model.query.AnalysisPlatformFeeRateQuery;
import com.ruoyi.analysis.service.AnalysisPlatformFeeRateService;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.framework.mybatis.DynamicCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 平台服务费率配置领域接口实现。
 */
@Component
public class AnalysisPlatformFeeRateFacadeImpl implements AnalysisPlatformFeeRateFacade {

    @Autowired
    private AnalysisPlatformFeeRateService rateService;

    @Override
    public List<AnalysisPlatformFeeRateBO> list(AnalysisPlatformFeeRateQuery query) {
        List<AnalysisPlatformFeeRate> rates = rateService.list(
                DynamicCondition.toWrapper(query, SortBy.of("-updated_time,-id")));
        return AnalysisConvert.INSTANCE.toPlatformFeeRateBOList(rates);
    }

    @Override
    public Long save(AnalysisPlatformFeeRateParam param) {
        return rateService.saveConfig(param);
    }

    @Override
    public void delete(Long id) {
        rateService.deleteConfig(id);
    }

    @Override
    public List<AnalysisPlatformFeeRateBO> listAll() {
        List<AnalysisPlatformFeeRate> rates = rateService.list();
        return AnalysisConvert.INSTANCE.toPlatformFeeRateBOList(rates);
    }
}
