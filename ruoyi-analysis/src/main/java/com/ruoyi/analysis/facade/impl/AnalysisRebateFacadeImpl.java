package com.ruoyi.analysis.facade.impl;

import com.ruoyi.analysis.facade.AnalysisRebateFacade;
import com.ruoyi.analysis.model.bo.AnalysisRebateBO;
import com.ruoyi.analysis.model.param.AnalysisRebateParam;
import com.ruoyi.analysis.service.AnalysisRebateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 返利价保领域接口实现。
 */
@Component
public class AnalysisRebateFacadeImpl implements AnalysisRebateFacade {
    @Autowired
    private AnalysisRebateService rebateService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AnalysisRebateBO> list() {
        return rebateService.listActivities();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long save(AnalysisRebateParam param) {
        return rebateService.saveActivity(param);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
        rebateService.deleteActivity(id);
    }
}
