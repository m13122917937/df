package com.ruoyi.biz.open;

import com.ruoyi.mapper.order.OpenConvert;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.web.vo.open.PddShopVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OpenBizService {


    @Autowired
    private IOrderFacade orderFacade;

    /**
     * 查询平台下面待补全地址的订单
     *
     * @param platform
     * @return
     */
    public Set<String> list(String platform) {

        List<OrderBO> list = orderFacade.list(new OrderQuery().setPlatform(platform).setAddressStatus(OrderConsts.AddressStatus.NOT_SUPPLEMENTED.getCode())
                .setStatusList(List.of(OrderConsts.OrderStatus.TRADING.getCode(), OrderConsts.OrderStatus.WAIT.getCode(), OrderConsts.OrderStatus.NEW.getCode())), null);

        return list.stream().map(OrderBO::getErpOrderId).collect(Collectors.toSet());
    }

    public List<PddShopVO> pddAddress(String shopName) {

        List<OrderBO> list = orderFacade.list(new OrderQuery().setPlatform("拼多多").setShopName(shopName).setAddressStatus(OrderConsts.AddressStatus.NOT_SUPPLEMENTED.getCode())
                .setStatusList(List.of(OrderConsts.OrderStatus.TRADING.getCode(), OrderConsts.OrderStatus.WAIT.getCode(), OrderConsts.OrderStatus.NEW.getCode())), null);

        return OpenConvert.INSTANCE.toOpens(list);
    }
}
