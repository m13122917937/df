package com.ruoyi.biz.subsidy;

import com.ruoyi.subsidy.facade.IGbOverviewFacade;
import com.ruoyi.subsidy.model.bo.GbOverviewBO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 国补后台概览应用编排。 */
@Component
@RequiredArgsConstructor
public class SubsidyOverviewBizService {
    private final IGbOverviewFacade overviewFacade;

    /** 查询国补商城概览。 */
    public GbOverviewBO getOverview() {
        return overviewFacade.getOverview();
    }
}
