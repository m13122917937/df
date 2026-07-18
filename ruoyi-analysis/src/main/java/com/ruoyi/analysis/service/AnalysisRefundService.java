package com.ruoyi.analysis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.analysis.domain.AnalysisRefundFact;
import com.ruoyi.analysis.mapper.AnalysisRefundFactMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 经营退款事实服务，只访问 ana_refund_fact。
 */
@Service
public class AnalysisRefundService extends ServiceImpl<AnalysisRefundFactMapper, AnalysisRefundFact> {

    /**
     * 按退款事实唯一键幂等新增或更新。
     *
     * @param fact 退款事实
     * @return true 表示新增，false 表示更新
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean upsert(AnalysisRefundFact fact) {
        AnalysisRefundFact existing = getOne(new LambdaQueryWrapper<AnalysisRefundFact>()
                .eq(AnalysisRefundFact::getRefundKey, fact.getRefundKey())
                .last("limit 1"));
        if (existing == null) {
            save(fact);
            return true;
        }
        fact.setId(existing.getId());
        updateById(fact);
        return false;
    }
}
