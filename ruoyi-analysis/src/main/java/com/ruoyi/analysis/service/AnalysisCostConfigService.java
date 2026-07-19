package com.ruoyi.analysis.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.analysis.constant.AnalysisConstants;
import com.ruoyi.analysis.convert.AnalysisConvert;
import com.ruoyi.analysis.domain.AnalysisCostConfig;
import com.ruoyi.analysis.mapper.AnalysisCostConfigMapper;
import com.ruoyi.analysis.model.param.AnalysisCostConfigParam;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 核算配置服务，只访问 ana_cost_config。
 */
@Service
public class AnalysisCostConfigService extends ServiceImpl<AnalysisCostConfigMapper, AnalysisCostConfig> {

    /**
     * 查询指定日期生效的核算配置。
     *
     * @param date 经营日期
     * @param month 月份
     * @return 生效配置
     */
    public List<AnalysisCostConfig> listEffectiveByDate(LocalDate date, String month) {
        return baseMapper.selectEffectiveByDate(date, month);
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

    /**
     * 批量导入核算配置，重复记录按参数决定是否覆盖。
     *
     * @param params 配置集合
     * @param overwrite 是否覆盖重复数据
     * @return 保存数量
     */
    @Transactional(rollbackFor = Exception.class)
    public int importConfigs(List<AnalysisCostConfigParam> params, boolean overwrite) {
        int count = 0;
        for (AnalysisCostConfigParam param : params) {
            validateType(param.getConfigType());
            AnalysisCostConfig existing = findDuplicate(param);
            if (existing != null && !overwrite) {
                throw new ServiceException("存在重复配置，请确认覆盖：" + duplicateDescription(param));
            }
            if (existing != null) {
                param.setId(existing.getId());
            }
            saveConfig(param);
            count++;
        }
        return count;
    }

    private AnalysisCostConfig findDuplicate(AnalysisCostConfigParam param) {
        return baseMapper.selectDuplicate(param);
    }

    private String duplicateDescription(AnalysisCostConfigParam param) {
        return String.join("/", param.getConfigType(), String.valueOf(param.getBusinessDate()),
                String.valueOf(param.getPlatform()), String.valueOf(param.getShopName()),
                String.valueOf(param.getGoodsNo()));
    }

    private void validateType(String configType) {
        if (!AnalysisConstants.CONFIG_TYPES.contains(configType)) {
            throw new ServiceException("不支持的核算配置类型：" + configType);
        }
    }
}
