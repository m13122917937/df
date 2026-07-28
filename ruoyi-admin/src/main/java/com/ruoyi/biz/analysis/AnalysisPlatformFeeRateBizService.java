package com.ruoyi.biz.analysis;

import com.ruoyi.analysis.facade.AnalysisPlatformFeeRateFacade;
import com.ruoyi.analysis.model.bo.AnalysisPlatformFeeRateBO;
import com.ruoyi.analysis.model.param.AnalysisPlatformFeeRateParam;
import com.ruoyi.analysis.model.query.AnalysisPlatformFeeRateQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 平台服务费率业务编排。
 */
@Component
public class AnalysisPlatformFeeRateBizService {
    @Autowired
    private AnalysisPlatformFeeRateFacade platformFeeRateFacade;

    /**
     * 查询平台服务费率列表。
     *
     * @param query 查询条件
     * @return 费率配置列表
     */
    public List<AnalysisPlatformFeeRateBO> list(AnalysisPlatformFeeRateQuery query) {
        return platformFeeRateFacade.list(query);
    }

    /**
     * 保存平台服务费率。
     *
     * @param param      保存参数
     * @param operatorId 操作人主键
     * @return 配置主键
     */
    public Long save(AnalysisPlatformFeeRateParam param, Long operatorId) {
        param.setOperatorId(operatorId);
        return platformFeeRateFacade.save(param);
    }

    /**
     * 删除平台服务费率。
     *
     * @param id 配置主键
     */
    public void delete(Long id) {
        platformFeeRateFacade.delete(id);
    }
}
