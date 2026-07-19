package com.ruoyi.analysis.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.analysis.domain.AnalysisOrderFact;
import com.ruoyi.analysis.mapper.AnalysisOrderFactMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 经营订单事实服务，只访问 ana_order_fact。
 */
@Service
public class AnalysisFactService extends ServiceImpl<AnalysisOrderFactMapper, AnalysisOrderFact> {

    /**
     * 按事实唯一键幂等新增或更新。
     *
     * @param fact 订单商品行事实
     * @return true 表示新增，false 表示更新
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean upsert(AnalysisOrderFact fact) {
        AnalysisOrderFact existing = baseMapper.selectByFactKey(fact.getFactKey());
        if (existing == null) {
            save(fact);
            return true;
        }
        fact.setId(existing.getId());
        updateById(fact);
        return false;
    }

    /**
     * 查询指定日期的经营事实。
     *
     * @param date 经营日期
     * @return 商品行事实列表
     */
    public List<AnalysisOrderFact> listByBusinessDate(LocalDate date) {
        return baseMapper.selectByBusinessDate(date);
    }

    /**
     * 按原订单和货品查找退款对应的最近商品行。
     *
     * @param originalOrderNo 原订单号
     * @param goodsNo 货品编码
     * @return 匹配的订单事实
     */
    public AnalysisOrderFact findRefundSource(String originalOrderNo, String goodsNo) {
        return baseMapper.selectRefundSource(originalOrderNo, goodsNo);
    }
}
