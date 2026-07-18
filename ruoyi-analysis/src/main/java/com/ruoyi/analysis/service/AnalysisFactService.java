package com.ruoyi.analysis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.analysis.domain.AnalysisOrderFact;
import com.ruoyi.analysis.mapper.AnalysisOrderFactMapper;
import com.ruoyi.analysis.model.query.AnalysisQuery;
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
        AnalysisOrderFact existing = getOne(new LambdaQueryWrapper<AnalysisOrderFact>()
                .eq(AnalysisOrderFact::getFactKey, fact.getFactKey())
                .last("limit 1"));
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
        return list(new LambdaQueryWrapper<AnalysisOrderFact>()
                .eq(AnalysisOrderFact::getBusinessDate, date));
    }

    /**
     * 按统一条件查询订单明细。
     *
     * @param query 查询条件
     * @return 商品行事实列表
     */
    public List<AnalysisOrderFact> listFacts(AnalysisQuery query) {
        LambdaQueryWrapper<AnalysisOrderFact> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(query.getStartDate() != null, AnalysisOrderFact::getBusinessDate, query.getStartDate())
                .le(query.getEndDate() != null, AnalysisOrderFact::getBusinessDate, query.getEndDate())
                .eq(query.getSubjectName() != null, AnalysisOrderFact::getSubjectName, query.getSubjectName())
                .eq(query.getPlatform() != null, AnalysisOrderFact::getPlatform, query.getPlatform())
                .eq(query.getShopName() != null, AnalysisOrderFact::getShopName, query.getShopName())
                .eq(query.getBrand() != null, AnalysisOrderFact::getBrand, query.getBrand())
                .eq(query.getCategory() != null, AnalysisOrderFact::getCategory, query.getCategory())
                .eq(query.getGoodsNo() != null, AnalysisOrderFact::getGoodsNo, query.getGoodsNo())
                .eq(query.getOrderType() != null, AnalysisOrderFact::getOrderType, query.getOrderType())
                .eq(query.getCalcStatus() != null, AnalysisOrderFact::getCalcStatus, query.getCalcStatus())
                .orderByDesc(AnalysisOrderFact::getBusinessDate, AnalysisOrderFact::getId);
        return list(wrapper);
    }
}
