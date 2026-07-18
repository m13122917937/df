package com.ruoyi.analysis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.analysis.constant.AnalysisConstants;
import com.ruoyi.analysis.convert.AnalysisConvert;
import com.ruoyi.analysis.domain.AnalysisCostConfig;
import com.ruoyi.analysis.mapper.AnalysisCostConfigMapper;
import com.ruoyi.analysis.model.bo.AnalysisCostConfigBO;
import com.ruoyi.analysis.model.param.AnalysisCostConfigParam;
import com.ruoyi.analysis.model.query.AnalysisQuery;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 核算配置服务，只访问 ana_cost_config。
 */
@Service
public class AnalysisCostConfigService extends ServiceImpl<AnalysisCostConfigMapper, AnalysisCostConfig> {

    /**
     * 查询核算配置。
     *
     * @param query 查询条件
     * @return 配置列表
     */
    public List<AnalysisCostConfigBO> listConfigs(AnalysisQuery query) {
        LambdaQueryWrapper<AnalysisCostConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getConfigType() != null, AnalysisCostConfig::getConfigType, query.getConfigType())
                .ge(query.getStartDate() != null, AnalysisCostConfig::getBusinessDate, query.getStartDate())
                .le(query.getEndDate() != null, AnalysisCostConfig::getBusinessDate, query.getEndDate())
                .eq(query.getPlatform() != null, AnalysisCostConfig::getPlatform, query.getPlatform())
                .eq(query.getShopName() != null, AnalysisCostConfig::getShopName, query.getShopName())
                .eq(query.getGoodsNo() != null, AnalysisCostConfig::getGoodsNo, query.getGoodsNo())
                .eq(query.getBrand() != null, AnalysisCostConfig::getBrand, query.getBrand())
                .eq(query.getCategory() != null, AnalysisCostConfig::getCategory, query.getCategory())
                .orderByDesc(AnalysisCostConfig::getBusinessDate, AnalysisCostConfig::getId);
        return AnalysisConvert.INSTANCE.toConfigBOList(list(wrapper));
    }

    /**
     * 保存或更新核算配置。
     *
     * @param param 配置参数
     * @return 配置主键
     */
    @Transactional(rollbackFor = Exception.class)
    public Long saveConfig(AnalysisCostConfigParam param) {
        validateType(param.getConfigType());
        AnalysisCostConfig domain = AnalysisConvert.INSTANCE.toDomain(param);
        LocalDateTime now = LocalDateTime.now();
        if (domain.getId() == null) {
            domain.setCreatedBy(param.getOperatorId());
            domain.setCreatedTime(now);
            domain.setUpdatedTime(now);
            save(domain);
        } else {
            domain.setUpdatedBy(param.getOperatorId());
            domain.setUpdatedTime(now);
            updateById(domain);
        }
        return domain.getId();
    }

    /**
     * 删除核算配置。
     *
     * @param id 配置主键
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long id) {
        removeById(id);
    }

    private void validateType(String configType) {
        if (!AnalysisConstants.CONFIG_TYPES.contains(configType)) {
            throw new ServiceException("不支持的核算配置类型：" + configType);
        }
    }
}
