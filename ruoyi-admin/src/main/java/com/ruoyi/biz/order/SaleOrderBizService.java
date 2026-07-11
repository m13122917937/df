package com.ruoyi.biz.order;

import com.ruoyi.order.model.consts.OrderConsts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 销售订单业务服务
 *
 * @author ruoyi
 */
@Slf4j
@Service
public class SaleOrderBizService extends WarehousingOrderBizService {

    @Override
    protected Integer getOrderType() {
        return OrderConsts.OrderType.SALES.getCode();
    }
}
