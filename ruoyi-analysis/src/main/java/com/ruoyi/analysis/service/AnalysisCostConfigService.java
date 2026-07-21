package com.ruoyi.analysis.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.analysis.constant.AnalysisConstants;
import com.ruoyi.analysis.convert.AnalysisConvert;
import com.ruoyi.analysis.domain.AnalysisCostConfig;
import com.ruoyi.analysis.mapper.AnalysisCostConfigMapper;
import com.ruoyi.analysis.model.param.AnalysisCostConfigParam;
import com.ruoyi.common.exception.ServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.ruoyi.common.utils.JacksonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 核算配置服务，只访问 ana_cost_config。
 */
@Service
public class AnalysisCostConfigService extends ServiceImpl<AnalysisCostConfigMapper, AnalysisCostConfig> {
    private static final Set<String> AMOUNT_REQUIRED_TYPES = new HashSet<>(Arrays.asList(
            "CASHBACK", "PLATFORM_FEE", "LOGISTICS", "PROMOTION", "PENALTY", "IMPAIRMENT", "TAX",
            "OTHER_ADJUSTMENT", "MARGIN", "INTERNAL_COST", "WAREHOUSE_COST"));
    private static final Set<String> MONTHLY_TYPES = new HashSet<>(Arrays.asList(
            "INTERNAL_COST", "WAREHOUSE_COST"));

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
        validate(param);
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
        for (int index = 0; index < params.size(); index++) {
            AnalysisCostConfigParam param = params.get(index);
            validateImportParam(param, index + 2);
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

    private void validateImportParam(AnalysisCostConfigParam param, int rowNumber) {
        try {
            validate(param);
        } catch (ServiceException exception) {
            throw new ServiceException("第" + rowNumber + "行：" + exception.getMessage());
        }
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

    private void validate(AnalysisCostConfigParam param) {
        validateType(param.getConfigType());
        if ("MARGIN".equals(param.getConfigType()) || "COLLECTION_DAYS".equals(param.getConfigType())) {
            throw new ServiceException("该配置请使用对应的独立配置页面");
        }
        validateAmount(param);
        validateEffectiveDate(param);
        JsonNode extraData = parseExtraData(param.getExtraData());
        validateTypeFields(param, extraData);
    }

    private void validateAmount(AnalysisCostConfigParam param) {
        if (AMOUNT_REQUIRED_TYPES.contains(param.getConfigType()) && param.getAmount() == null) {
            throw new ServiceException("该核算项目必须填写金额");
        }
        if ("COLLECTION_DAYS".equals(param.getConfigType()) && param.getCoefficient() == null) {
            throw new ServiceException("回款周期必须填写回款天数");
        }
        if ("FIXED_COEFFICIENT".equals(param.getConfigType())
                && param.getAmount() == null && param.getCoefficient() == null) {
            throw new ServiceException("成本系数至少填写固定金额或系数");
        }
        if ("REBATE".equals(param.getConfigType())
                && param.getAmount() == null && param.getCoefficient() == null) {
            throw new ServiceException("返利与价保至少填写金额或点位");
        }
    }

    private void validateEffectiveDate(AnalysisCostConfigParam param) {
        if ("MARGIN".equals(param.getConfigType())) {
            return;
        }
        if (param.getBusinessDate() == null && isBlank(param.getMonthValue()) && param.getStartDate() == null) {
            throw new ServiceException("发生日期、月份或生效开始日期至少填写一项");
        }
        if ((param.getStartDate() == null) != (param.getEndDate() == null)) {
            throw new ServiceException("生效开始日期和结束日期必须同时填写");
        }
        if (param.getStartDate() != null && param.getStartDate().isAfter(param.getEndDate())) {
            throw new ServiceException("生效开始日期不能晚于结束日期");
        }
        if (!isBlank(param.getMonthValue()) && !param.getMonthValue().matches("\\d{4}-(0[1-9]|1[0-2])")) {
            throw new ServiceException("月份必须使用 yyyy-MM 格式");
        }
        if (MONTHLY_TYPES.contains(param.getConfigType()) && isBlank(param.getMonthValue())) {
            throw new ServiceException("该核算项目必须按月份填写");
        }
    }

    private JsonNode parseExtraData(String extraData) {
        if (isBlank(extraData)) {
            return null;
        }
        try {
            JsonNode node = JacksonUtil.readTree(extraData);
            if (!node.isObject()) {
                throw new ServiceException("扩展参数必须是 JSON 对象");
            }
            return node;
        } catch (ServiceException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new ServiceException("扩展参数不是合法 JSON 对象");
        }
    }

    private void validateTypeFields(AnalysisCostConfigParam param, JsonNode extraData) {
        String type = param.getConfigType();
        if ("MARGIN".equals(type)) {
            validateMargin(param);
        }
        if ("COLLECTION_DAYS".equals(type) && decimal(extraData, AnalysisConstants.EXTRA_ANNUAL_RATE) == null) {
            throw new ServiceException("回款周期必须填写年化资金成本率");
        }
        if (("INTERNAL_COST".equals(type) || "WAREHOUSE_COST".equals(type))
                && isBlank(text(extraData, AnalysisConstants.EXTRA_COST_SCOPE))) {
            throw new ServiceException("人员成本和仓配成本必须选择成本归属");
        }
        if ("INTERNAL_COST".equals(type) && decimal(extraData, AnalysisConstants.EXTRA_HEADCOUNT) == null) {
            throw new ServiceException("人员成本必须填写人员数量");
        }
    }

    private void validateMargin(AnalysisCostConfigParam param) {
        if (isBlank(param.getPlatform())) {
            throw new ServiceException("保证金必须选择平台");
        }
        if (isBlank(param.getShopName())) {
            throw new ServiceException("保证金必须选择店铺名称");
        }
    }

    private java.math.BigDecimal decimal(JsonNode node, String field) {
        if (node == null || !node.hasNonNull(field) || !node.get(field).isNumber()) {
            return null;
        }
        return node.get(field).decimalValue();
    }

    private String text(JsonNode node, String field) {
        if (node == null || !node.hasNonNull(field)) {
            return null;
        }
        return node.get(field).asText();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
