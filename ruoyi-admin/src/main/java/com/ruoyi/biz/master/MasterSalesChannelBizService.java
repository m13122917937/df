package com.ruoyi.biz.master;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.master.facade.IMasterSalesChannelFacade;
import com.ruoyi.master.model.bo.MasterSalesChannelBO;
import com.ruoyi.master.model.query.MasterSalesChannelQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 销售渠道应用编排服务。
 */
@Component
@RequiredArgsConstructor
public class MasterSalesChannelBizService {

    private final IMasterSalesChannelFacade masterSalesChannelFacade;

    /**
     * 分页查询销售渠道。
     *
     * @param query 查询条件
     * @param pageParam 分页参数
     * @return 销售渠道分页数据
     */
    public PageBO<MasterSalesChannelBO> page(final MasterSalesChannelQuery query,
                                             final PageParamV2 pageParam) {
        return masterSalesChannelFacade.page(query, pageParam);
    }

}
