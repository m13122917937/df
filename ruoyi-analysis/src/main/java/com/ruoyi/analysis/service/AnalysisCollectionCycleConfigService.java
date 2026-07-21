package com.ruoyi.analysis.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.analysis.convert.AnalysisConvert;
import com.ruoyi.analysis.domain.AnalysisCollectionCycleConfig;
import com.ruoyi.analysis.mapper.AnalysisCollectionCycleConfigMapper;
import com.ruoyi.analysis.model.param.AnalysisCollectionCycleConfigParam;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/** 回款周期配置服务，只访问 ana_collection_cycle_config。 */
@Service
public class AnalysisCollectionCycleConfigService extends ServiceImpl<AnalysisCollectionCycleConfigMapper, AnalysisCollectionCycleConfig> {
    /** 保存回款周期配置。 */
    @Transactional(rollbackFor = Exception.class)
    public Long saveConfig(AnalysisCollectionCycleConfigParam param) {
        validate(param);
        AnalysisCollectionCycleConfig domain = AnalysisConvert.INSTANCE.toDomain(param);
        LocalDateTime now = LocalDateTime.now();
        AnalysisCollectionCycleConfig existing = baseMapper.selectByPlatformAndShop(domain.getPlatform(), domain.getShopName());
        if (domain.getId() == null) {
            if (existing != null) { throw new ServiceException("该平台与店铺已维护回款周期，请直接编辑"); }
            domain.setCreatedBy(param.getOperatorId()); domain.setCreatedTime(now); domain.setUpdatedTime(now); save(domain);
        } else {
            if (existing != null && !existing.getId().equals(domain.getId())) { throw new ServiceException("该平台与店铺已维护回款周期，请直接编辑"); }
            domain.setUpdatedBy(param.getOperatorId()); domain.setUpdatedTime(now); updateById(domain);
        }
        return domain.getId();
    }

    private void validate(AnalysisCollectionCycleConfigParam param) {
        if (blank(param.getPlatform()) || blank(param.getShopName())) { throw new ServiceException("平台和店铺名称不能为空"); }
        validateDays(param.getGoodsCollectionDays(), "货款回款周期");
        validateDays(param.getSubsidyCollectionDays(), "补贴款回款周期");
        validateDays(param.getNationalSubsidyCollectionDays(), "国补回款周期");
    }

    private void validateDays(Integer value, String fieldName) {
        if (value == null || value < 0) { throw new ServiceException(fieldName + "必须为零或正整数"); }
    }

    private boolean blank(String value) { return value == null || value.trim().isEmpty(); }
}
