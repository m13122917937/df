package com.ruoyi.biz.analysis;

import com.ruoyi.analysis.model.bo.AnalysisCostConfigBO;
import com.ruoyi.analysis.model.param.AnalysisCostConfigParam;
import com.ruoyi.analysis.model.query.AnalysisQuery;
import com.ruoyi.analysis.service.AnalysisCostConfigService;
import com.ruoyi.common.core.domain.user.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 经营核算配置业务编排。
 */
@Component
public class AnalysisConfigBizService {
    @Autowired
    private AnalysisCostConfigService configService;

    /**
     * 查询核算配置。
     *
     * @param query 查询条件
     * @return 配置列表
     */
    public List<AnalysisCostConfigBO> list(AnalysisQuery query) {
        return configService.listConfigs(query);
    }

    /**
     * 保存核算配置。
     *
     * @param param 配置参数
     * @param loginUser 当前用户
     * @return 配置主键
     */
    public Long save(AnalysisCostConfigParam param, LoginUser loginUser) {
        param.setOperatorId(loginUser.getUserId());
        return configService.saveConfig(param);
    }

    /**
     * 删除核算配置。
     *
     * @param id 配置主键
     */
    public void delete(Long id) {
        configService.deleteConfig(id);
    }
}
