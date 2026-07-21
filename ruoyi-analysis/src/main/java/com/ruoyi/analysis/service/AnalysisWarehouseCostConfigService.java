package com.ruoyi.analysis.service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.analysis.convert.AnalysisConvert;
import com.ruoyi.analysis.domain.AnalysisWarehouseCostConfig;
import com.ruoyi.analysis.mapper.AnalysisWarehouseCostConfigMapper;
import com.ruoyi.analysis.model.param.AnalysisWarehouseCostConfigParam;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
/** 仓储售后成本服务，只访问 ana_warehouse_cost_config。 */
@Service public class AnalysisWarehouseCostConfigService extends ServiceImpl<AnalysisWarehouseCostConfigMapper, AnalysisWarehouseCostConfig> {
    @Transactional(rollbackFor = Exception.class)
    public Long saveConfig(AnalysisWarehouseCostConfigParam param) {
        if (param.getMonthValue() == null || !param.getMonthValue().matches("\\d{4}-(0[1-9]|1[0-2])")) { throw new ServiceException("月份格式必须为 yyyy-MM"); }
        if (param.getAfterSalesHeadcount() == null || param.getAfterSalesHeadcount().signum() < 0 || param.getAfterSalesLaborCost() == null || param.getAfterSalesLaborCost().signum() < 0) { throw new ServiceException("售后人力和售后人力成本必须为非负数"); }
        AnalysisWarehouseCostConfig domain = AnalysisConvert.INSTANCE.toDomain(param);
        LocalDateTime now = LocalDateTime.now();
        if (domain.getId() == null) { domain.setCreatedBy(param.getOperatorId()); domain.setCreatedTime(now); domain.setUpdatedTime(now); save(domain); }
        else { domain.setUpdatedBy(param.getOperatorId()); domain.setUpdatedTime(now); updateById(domain); }
        return domain.getId();
    }
}
