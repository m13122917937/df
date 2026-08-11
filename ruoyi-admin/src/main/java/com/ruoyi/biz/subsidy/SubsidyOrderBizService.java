package com.ruoyi.biz.subsidy;

import com.ruoyi.subsidy.facade.IGbOrderFacade;
import com.ruoyi.subsidy.model.param.GbShipmentParam;
import com.ruoyi.web.form.subsidy.SubsidyShipmentForm;
import com.ruoyi.subsidy.model.bo.GbOrderBO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/** 国补后台订单应用编排。 */
@Component
@RequiredArgsConstructor
public class SubsidyOrderBizService {
    private final IGbOrderFacade orderFacade;

    /** 对已支付订单人工发货。 */
    public void ship(final String orderNo, final SubsidyShipmentForm form) {
        orderFacade.ship(new GbShipmentParam().setOrderNo(orderNo).setLogisticsCompany(form.getLogisticsCompany())
                .setTrackingNo(form.getTrackingNo()));
    }

    /** 查询后台订单列表。 */
    public List<GbOrderBO> list(final String orderStatus) {
        return orderFacade.listForAdmin(orderStatus);
    }
}
