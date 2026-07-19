package com.ruoyi.analysis.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.analysis.domain.AnalysisRefundFact;
import com.ruoyi.analysis.mapper.AnalysisRefundFactMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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
        AnalysisRefundFact existing = baseMapper.selectByRefundKey(fact.getRefundKey());
        if (existing == null) {
            save(fact);
            return true;
        }
        fact.setId(existing.getId());
        updateById(fact);
        return false;
    }

    /**
     * 查询指定日期的退款事实。
     *
     * @param date 退款日期
     * @return 退款事实列表
     */
    public List<AnalysisRefundFact> listByRefundDate(LocalDate date) {
        return baseMapper.selectByRefundDate(date);
    }
}
