package com.ruoyi.subsidy.facade.impl;

import com.ruoyi.subsidy.facade.IGbOverviewFacade;
import com.ruoyi.subsidy.model.bo.GbOverviewBO;
import com.ruoyi.subsidy.service.GbOrderService;
import com.ruoyi.subsidy.service.GbProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 国补商城概览领域实现。 */
@Component
@RequiredArgsConstructor
public class GbOverviewFacade implements IGbOverviewFacade {
    private final GbProductService productService;
    private final GbOrderService orderService;

    @Override
    public GbOverviewBO getOverview() {
        GbOverviewBO result = new GbOverviewBO();
        result.setProductCount(productService.countOnSale());
        result.setPendingShipmentCount(orderService.countPendingShipment());
        result.setPendingRefundCount(0L);
        return result;
    }
}
