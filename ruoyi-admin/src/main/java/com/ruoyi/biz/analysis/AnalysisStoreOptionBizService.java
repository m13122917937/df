package com.ruoyi.biz.analysis;

import com.ruoyi.master.facade.IMasterSalesChannelFacade;
import com.ruoyi.master.model.bo.MasterSalesChannelBO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 经营核算店铺选项应用编排。
 */
@Component
@RequiredArgsConstructor
public class AnalysisStoreOptionBizService {

    private final IMasterSalesChannelFacade masterSalesChannelFacade;

    /**
     * 查询经营核算可选择的平台与店铺。
     *
     * @return 销售渠道主数据集合
     */
    public List<MasterSalesChannelBO> listStoreOptions() {
        return masterSalesChannelFacade.listStoreOptions();
    }
}
