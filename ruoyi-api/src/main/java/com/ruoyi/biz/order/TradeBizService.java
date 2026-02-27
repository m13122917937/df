package com.ruoyi.biz.order;

import com.ruoyi.order.facade.IApplyFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.facade.ITradeOrderFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TradeBizService {

    @Autowired
    IApplyFacade applyFacade;

    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    ITradeOrderFacade tradeFacade;




}
