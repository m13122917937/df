package com.ruoyi.analysis.facade;

import com.ruoyi.analysis.model.bo.AnalysisPlatformFeeRateBO;
import com.ruoyi.analysis.model.param.AnalysisPlatformFeeRateParam;
import com.ruoyi.analysis.model.query.AnalysisPlatformFeeRateQuery;

import java.util.List;

/**
 * 平台服务费率配置领域接口。
 */
public interface AnalysisPlatformFeeRateFacade {

    /**
     * 查询费率配置列表。
     *
     * @param query 查询参数
     * @return 费率配置列表
     */
    List<AnalysisPlatformFeeRateBO> list(AnalysisPlatformFeeRateQuery query);

    /**
     * 保存费率配置。
     *
     * @param param 保存参数
     * @return 配置 ID
     */
    Long save(AnalysisPlatformFeeRateParam param);

    /**
     * 删除费率配置。
     *
     * @param id 配置 ID
     */
    void delete(Long id);

    /**
     * 查询所有费率配置（用于计算时全量加载）。
     *
     * @return 所有费率配置
     */
    List<AnalysisPlatformFeeRateBO> listAll();
}
