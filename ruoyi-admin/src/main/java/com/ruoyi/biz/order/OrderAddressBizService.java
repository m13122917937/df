package com.ruoyi.biz.order;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.biz.address.SmartParse;
import com.ruoyi.biz.address.domain.AddressInfo;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.web.form.order.AddressCompletedParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class OrderAddressBizService {

    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    SmartParse smartParse;

    public void parse(AddressCompletedParams params) {

        List<AddressCompletedParams.AddressCompletedParam> completedParams = params.getAddressCompletedParams();
        if (CollectionUtil.isEmpty(completedParams)) {
            return;
        }
        for (AddressCompletedParams.AddressCompletedParam completedParam : completedParams) {
            try {
                String erpOrderId = completedParam.getErpOrderId();
                if (completedParam.getPhone().length() < 8) {
                    log.info("【{}】补全的手机号小于8位", completedParam.getErpOrderId());
                    return;
                }
                String receivingAddress = completedParam.getReceivingAddress();
                if (StrUtil.isEmpty(receivingAddress)) {
                    log.info("【{}】详细地址为空", erpOrderId);
                    return;
                }

                AddressInfo addressInfo = smartParse.parseAddressInfo(StrUtil.cleanBlank(receivingAddress));
                String province = addressInfo.getProvinceCode();
                String city = addressInfo.getCityCode();
                if (StrUtil.isEmpty(province) || StrUtil.isEmpty(city)) {
                    log.info("【{}】省:{}，市:{}为空：", erpOrderId, province, city);
                    return;
                }
                log.info("补全订单地址：{}", erpOrderId);
                // 修改地址信息
                OrderBO orderBO = orderFacade.getOne(new OrderQuery().setErpOrderId(erpOrderId));
                if (Objects.isNull(orderBO)) {
                    return;
                }
                OrderParam orderParam = new OrderParam().setProvince(Long.valueOf(province)).setCity(Long.valueOf(city))
                        .setAddressStatus(OrderConsts.AddressStatus.SUCCESS.getCode()).setPhone(completedParam.getPhone())
                        .setReceivingAddress(receivingAddress).setAddressee(completedParam.getAddressee());
//                if (Objects.equals(orderBO.getStatus(), OrderConsts.OrderStatus.WAIT.getCode())) {
//                    orderParam.setStatus(OrderConsts.OrderStatus.TRADING.getCode());
//                }
                orderFacade.update(orderParam, new OrderQuery().setErpOrderId(erpOrderId));

            } catch (Exception e) {
                log.error("订单地址补全失败", e);
            }
        }


    }
}
