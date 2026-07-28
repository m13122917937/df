package com.ruoyi.biz.analysis;

import com.ruoyi.analysis.facade.AnalysisDashboardFacade;
import com.ruoyi.analysis.model.bo.AnalysisDashboardBO;
import com.ruoyi.analysis.model.bo.AnalysisDashboardFilterOptionsBO;
import com.ruoyi.analysis.model.bo.AnalysisOrderFactBO;
import com.ruoyi.analysis.model.query.AnalysisQuery;
import com.ruoyi.biz.analysis.convert.AnalysisDashboardBizConvert;
import com.ruoyi.master.facade.IMasterSalesChannelFacade;
import com.ruoyi.master.model.bo.MasterSalesChannelBO;
import com.ruoyi.product.facade.IProductSkuFacade;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 经营分析看板业务编排。
 */
@Component
@RequiredArgsConstructor
public class AnalysisDashboardBizService {
    private final AnalysisDashboardFacade dashboardFacade;

    private final IMasterSalesChannelFacade masterSalesChannelFacade;

    private final IProductSkuFacade productSkuFacade;

    /**
     * 查询经营看板。
     *
     * @param query 查询条件
     * @return 看板数据
     */
    public AnalysisDashboardBO dashboard(AnalysisQuery query) {
        return dashboardFacade.dashboard(query);
    }

    /**
     * 查询绩效汇总。
     *
     * @param query 查询条件
     * @return 绩效汇总看板
     */
    public AnalysisDashboardBO performanceRollup(AnalysisQuery query) {
        return dashboardFacade.performanceRollup(query);
    }

    /**
     * 查询缺失成本的数据质量明细。
     *
     * @param query 查询条件
     * @return 未完成核算的订单商品行
     */
    public List<AnalysisOrderFactBO> dataQuality(AnalysisQuery query) {
        return dashboardFacade.dataQuality(query);
    }

    /**
     * 查询经营统计使用的基础数据筛选项。
     *
     * @return 平台、店铺、品牌与品类筛选项
     */
    public AnalysisDashboardFilterOptionsBO listFilterOptions() {
        List<MasterSalesChannelBO> stores = masterSalesChannelFacade.listStoreOptions();
        AnalysisDashboardFilterOptionsBO options = new AnalysisDashboardFilterOptionsBO();
        options.setStores(AnalysisDashboardBizConvert.INSTANCE.toStoreOptionList(stores));
        options.setPlatforms(distinctOptions(stores.stream()
                .map(MasterSalesChannelBO::getPlatformName).collect(Collectors.toList())));
        options.setBrands(distinctOptions(productSkuFacade.listBrandOptions()));
        options.setCategories(distinctOptions(productSkuFacade.listCategoryOptions()));
        return options;
    }

    private List<String> distinctOptions(final List<String> values) {
        return values.stream().filter(StringUtils::isNotBlank).distinct().sorted().collect(Collectors.toList());
    }
}
