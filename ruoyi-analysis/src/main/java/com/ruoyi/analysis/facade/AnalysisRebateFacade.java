package com.ruoyi.analysis.facade;

import com.ruoyi.analysis.model.bo.AnalysisRebateBO;
import com.ruoyi.analysis.model.param.AnalysisRebateParam;

import java.util.List;

/**
 * 返利价保领域对外接口。
 */
public interface AnalysisRebateFacade {

    /**
     * 查询返利价保活动。
     *
     * @return 活动列表
     */
    List<AnalysisRebateBO> list();

    /**
     * 保存返利价保活动。
     *
     * @param param 活动参数
     * @return 活动主键
     */
    Long save(AnalysisRebateParam param);

    /**
     * 删除返利价保活动。
     *
     * @param id 活动主键
     */
    void delete(Long id);
}
