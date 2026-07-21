package com.ruoyi.biz.analysis;

import com.ruoyi.analysis.facade.AnalysisMarginConfigFacade;
import com.ruoyi.analysis.model.bo.AnalysisMarginConfigBO;
import com.ruoyi.analysis.model.param.AnalysisMarginConfigParam;
import com.ruoyi.analysis.model.query.AnalysisMarginConfigQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 保证金配置业务编排。
 */
@Component
public class AnalysisMarginConfigBizService {
    @Autowired
    private AnalysisMarginConfigFacade marginConfigFacade;

    /**
     * 查询保证金配置。
     *
     * @param query 查询条件
     * @return 保证金配置列表
     */
    public List<AnalysisMarginConfigBO> list(AnalysisMarginConfigQuery query) {
        return marginConfigFacade.list(query);
    }

    /**
     * 保存保证金配置。
     *
     * @param param 保存参数
     * @param operatorId 操作人主键
     * @return 配置主键
     */
    public Long save(AnalysisMarginConfigParam param, Long operatorId) {
        param.setOperatorId(operatorId);
        return marginConfigFacade.save(param);
    }

    /**
     * 删除保证金配置。
     *
     * @param id 配置主键
     */
    public void delete(Long id) {
        marginConfigFacade.delete(id);
    }
}
