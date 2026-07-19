package com.ruoyi.analysis.facade;

import com.ruoyi.analysis.model.bo.AnalysisCostConfigBO;
import com.ruoyi.analysis.model.param.AnalysisCostConfigParam;
import com.ruoyi.analysis.model.query.AnalysisQuery;

import java.util.List;

/**
 * 经营核算配置领域对外接口。
 */
public interface AnalysisConfigFacade {

    /**
     * 查询核算配置。
     *
     * @param query 查询条件
     * @return 配置列表
     */
    List<AnalysisCostConfigBO> list(AnalysisQuery query);

    /**
     * 保存核算配置。
     *
     * @param param 配置参数
     * @return 配置主键
     */
    Long save(AnalysisCostConfigParam param);

    /**
     * 删除核算配置。
     *
     * @param id 配置主键
     */
    void delete(Long id);

    /**
     * 批量导入核算配置。
     *
     * @param params 配置参数
     * @param overwrite 是否覆盖
     * @return 保存数量
     */
    int importConfigs(List<AnalysisCostConfigParam> params, boolean overwrite);
}
