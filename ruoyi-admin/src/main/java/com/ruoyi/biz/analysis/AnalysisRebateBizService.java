package com.ruoyi.biz.analysis;

import com.ruoyi.analysis.facade.AnalysisRebateFacade;
import com.ruoyi.analysis.model.bo.AnalysisRebateBO;
import com.ruoyi.analysis.model.param.AnalysisRebateParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 返利价保业务编排。
 */
@Component
public class AnalysisRebateBizService {
    @Autowired
    private AnalysisRebateFacade rebateFacade;

    /**
     * 查询活动列表。
     */
    public List<AnalysisRebateBO> list() {
        return rebateFacade.list();
    }

    /**
     * 保存活动。
     */
    public Long save(AnalysisRebateParam param, Long operatorId) {
        param.setOperatorId(operatorId);
        return rebateFacade.save(param);
    }

    /**
     * 删除活动。
     */
    public void delete(Long id) {
        rebateFacade.delete(id);
    }
}
