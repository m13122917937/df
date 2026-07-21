package com.ruoyi.analysis.facade;

import com.ruoyi.analysis.model.bo.AnalysisMarginConfigBO;
import com.ruoyi.analysis.model.param.AnalysisMarginConfigParam;
import com.ruoyi.analysis.model.query.AnalysisMarginConfigQuery;

import java.util.List;

/**
 * 保证金配置领域对外接口。
 */
public interface AnalysisMarginConfigFacade {
    /**
     * 查询保证金配置。
     *
     * @param query 查询条件
     * @return 保证金配置列表
     */
    List<AnalysisMarginConfigBO> list(AnalysisMarginConfigQuery query);

    /**
     * 保存保证金配置。
     *
     * @param param 保存参数
     * @return 配置主键
     */
    Long save(AnalysisMarginConfigParam param);

    /**
     * 删除保证金配置。
     *
     * @param id 配置主键
     */
    void delete(Long id);
}
