package com.ruoyi.biz.order;

import com.ruoyi.order.facade.IOrderFacade;
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
    private IOrderFacade orderFacade;

    /**
     * 查询已存在的店铺名称（来源：订单 shop_name 去重）。
     *
     * @return 去重并排序后的店铺名称
     */
    public List<String> listShopNames() {
        List<String> shopNames = orderFacade.listShopNames();
        return shopNames.stream().filter(Objects::nonNull).filter(s -> !s.isEmpty())
                .distinct().sorted().collect(Collectors.toList());
    }
}
