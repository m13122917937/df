package com.ruoyi.analysis.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.analysis.domain.AnalysisPlatformFeeRate;
import com.ruoyi.analysis.mapper.AnalysisPlatformFeeRateMapper;
import com.ruoyi.analysis.model.param.AnalysisPlatformFeeRateParam;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 平台服务费率配置服务。
 */
@Service
public class AnalysisPlatformFeeRateService
        extends ServiceImpl<AnalysisPlatformFeeRateMapper, AnalysisPlatformFeeRate> {

    /**
     * 保存费率配置（新增或更新）。
     *
     * @param param 保存参数
     * @return 配置 ID
     */
    public Long saveConfig(AnalysisPlatformFeeRateParam param) {
        // 校验
        if (param.getPlatform() == null || param.getPlatform().isBlank()) {
            throw new ServiceException("平台不能为空");
        }
        if (param.getFeeRate() == null || param.getFeeRate().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new ServiceException("费率必须填写且大于等于0");
        }

        // 唯一性检查
        Long existed = checkDuplicate(param.getPlatform(), param.getBusinessType(),
                param.getCategory(), param.getId());
        if (existed != null) {
            throw new ServiceException("相同平台+业态+品类的费率配置已存在");
        }

        AnalysisPlatformFeeRate entity = new AnalysisPlatformFeeRate();
        entity.setId(param.getId());
        entity.setPlatform(param.getPlatform());
        entity.setBusinessType(param.getBusinessType());
        entity.setCategory(param.getCategory());
        entity.setFeeRate(param.getFeeRate());
        entity.setRemark(param.getRemark());

        if (param.getId() != null) {
            entity.setUpdatedBy(param.getOperatorId());
            entity.setUpdatedTime(LocalDateTime.now());
            baseMapper.updateById(entity);
            return param.getId();
        } else {
            entity.setCreatedBy(param.getOperatorId());
            entity.setCreatedTime(LocalDateTime.now());
            entity.setUpdatedBy(param.getOperatorId());
            entity.setUpdatedTime(LocalDateTime.now());
            baseMapper.insert(entity);
            return entity.getId();
        }
    }

    /**
     * 删除费率配置。
     *
     * @param id 配置 ID
     */
    public void deleteConfig(Long id) {
        baseMapper.deleteById(id);
    }

    /**
     * 检查唯一性约束。
     *
     * @param platform     平台
     * @param businessType 业态
     * @param category     品类
     * @param excludeId    排除的 ID（更新时使用）
     * @return 已存在的 ID，无则 null
     */
    private Long checkDuplicate(String platform, Integer businessType, String category, Long excludeId) {
        List<AnalysisPlatformFeeRate> list = lambdaQuery()
                .eq(AnalysisPlatformFeeRate::getPlatform, platform)
                .apply("(business_type IS NULL AND {0} IS NULL) OR business_type = {0}",
                        businessType == null ? java.math.BigDecimal.ZERO : businessType)
                .apply("(category IS NULL AND {0} IS NULL) OR category = {0}",
                        category == null ? "" : category)
                .list();
        // 简化：用内存判断精确重复
        for (AnalysisPlatformFeeRate r : list) {
            boolean btMatch = (r.getBusinessType() == null && businessType == null)
                    || (r.getBusinessType() != null && r.getBusinessType().equals(businessType));
            boolean catMatch = (r.getCategory() == null && category == null)
                    || (r.getCategory() != null && r.getCategory().equals(category));
            if (btMatch && catMatch) {
                if (excludeId == null || !excludeId.equals(r.getId())) {
                    return r.getId();
                }
            }
        }
        return null;
    }
}
