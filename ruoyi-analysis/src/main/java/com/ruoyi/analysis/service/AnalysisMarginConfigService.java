package com.ruoyi.analysis.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.analysis.convert.AnalysisConvert;
import com.ruoyi.analysis.domain.AnalysisMarginConfig;
import com.ruoyi.analysis.mapper.AnalysisMarginConfigMapper;
import com.ruoyi.analysis.model.param.AnalysisMarginConfigParam;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 保证金配置服务，只访问 ana_margin_config。
 */
@Service
public class AnalysisMarginConfigService extends ServiceImpl<AnalysisMarginConfigMapper, AnalysisMarginConfig> {

    /**
     * 保存保证金配置。
     *
     * @param param 保证金配置参数
     * @return 配置主键
     */
    @Transactional(rollbackFor = Exception.class)
    public Long saveConfig(AnalysisMarginConfigParam param) {
        validate(param);
        AnalysisMarginConfig domain = AnalysisConvert.INSTANCE.toDomain(param);
        LocalDateTime now = LocalDateTime.now();
        if (domain.getId() == null) {
            ensureNotDuplicate(domain);
            domain.setCreatedBy(param.getOperatorId());
            domain.setCreatedTime(now);
            domain.setUpdatedTime(now);
            save(domain);
        } else {
            ensureNotDuplicateOnUpdate(domain);
            domain.setUpdatedBy(param.getOperatorId());
            domain.setUpdatedTime(now);
            updateById(domain);
        }
        return domain.getId();
    }

    /**
     * 删除保证金配置。
     *
     * @param id 配置主键
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long id) {
        removeById(id);
    }

    private void validate(AnalysisMarginConfigParam param) {
        if (isBlank(param.getPlatform())) {
            throw new ServiceException("保证金必须选择平台");
        }
        if (isBlank(param.getShopName())) {
            throw new ServiceException("保证金必须选择店铺名称");
        }
        if (param.getMarginAmount() == null || param.getMarginAmount().signum() < 0) {
            throw new ServiceException("保证金金额必须大于或等于零");
        }
    }

    private void ensureNotDuplicate(AnalysisMarginConfig domain) {
        if (baseMapper.selectByPlatformAndShop(domain.getPlatform(), domain.getShopName()) != null) {
            throw new ServiceException("该平台与店铺已维护保证金，请直接修改");
        }
    }

    private void ensureNotDuplicateOnUpdate(AnalysisMarginConfig domain) {
        AnalysisMarginConfig existing = baseMapper.selectByPlatformAndShop(domain.getPlatform(), domain.getShopName());
        if (existing != null && !existing.getId().equals(domain.getId())) {
            throw new ServiceException("该平台与店铺已维护保证金，请直接修改");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
