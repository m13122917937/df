package com.ruoyi.biz.order;

import com.ruoyi.bill.facade.IPayerConfigFacade;
import com.ruoyi.bill.model.bo.PayerConfigBO;
import com.ruoyi.bill.model.query.PayerConfigQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 订单筛选条件业务编排。
 */
@Component
public class OrderFilterBizService {
    @Autowired
    private IPayerConfigFacade payerConfigFacade;

    /**
     * 查询已配置的店铺名称。
     *
     * @return 去重并排序后的店铺名称
     */
    public List<String> listShopNames() {
        List<PayerConfigBO> configs = payerConfigFacade.list(new PayerConfigQuery(), null);
        return configs.stream().map(PayerConfigBO::getKeyWord).filter(Objects::nonNull)
                .distinct().sorted().collect(Collectors.toList());
    }
}
