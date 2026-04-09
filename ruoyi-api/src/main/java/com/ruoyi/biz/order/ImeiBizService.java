package com.ruoyi.biz.order;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.mapper.order.ImeiConvert;
import com.ruoyi.order.facade.*;
import com.ruoyi.order.model.bo.*;
import com.ruoyi.order.model.consts.*;
import com.ruoyi.order.model.param.ImeiParam;
import com.ruoyi.order.model.param.ImeiSkuRelParam;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.*;
import com.ruoyi.sn.SnQueryClient;
import com.ruoyi.sn.model.WarrantyResult;
import com.ruoyi.web.form.order.ImeiOrderParam;
import com.ruoyi.web.vo.order.ImeiVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class ImeiBizService {

    @Autowired
    IImeiFacade imeiFacade;

    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    ITradeOrderFacade tradeOrderFacade;

    @Autowired
    IHangingOrderFacade hangingOrderFacade;

    @Autowired
    SnQueryClient snQueryClient;

    @Autowired
    IImeiSkuRelFacade imeiSkuRelFacade;


    /**
     *
     * @param imeiOrderParam
     */
    @Transactional
    public List<ImeiVO> verifyImei(ImeiOrderParam imeiOrderParam) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        //防止重复
        ImeiBO imeiBO = imeiFacade.getOne(new ImeiQuery().setGtCreateTime(DateUtil.offsetHour(DateUtil.date(), -3)).setOrderId(imeiOrderParam.getOrderCode()).setSn(imeiOrderParam.getSn()).setImel(imeiOrderParam.getImeiCode()));
        if (imeiBO != null) {
            return ImeiConvert.INSTANCE.listvo(new ArrayList<>() {{
                add(imeiBO);
            }});
        }

        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(imeiOrderParam.getOrderCode()));
        Assert.notNull(orderBO, "订单不存在");
        Assert.equals(orderBO.getStatus(), OrderConsts.OrderStatus.DELIVERY_ING.getCode(), "订单状态异常");

        TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setOrderId(orderBO.getOrderCode()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
        Assert.notNull(tradeOrderBO, "抢单记录不存在");
        imeiFacade.delete(new ImeiQuery().setOrderId(imeiOrderParam.getOrderCode()).setTradeNo(tradeOrderBO.getId()));
        List<ImeiBO> list = new ArrayList<>();
        list.add(getImeiBO(imeiOrderParam.getImeiCode(), imeiOrderParam.getSn(), orderBO, tradeOrderBO));

        //修改订单子状态
        orderFacade.update(new OrderParam().setSubStatus(OrderConsts.OrderSubStatus.WAIT_EXPRESS.getCode()), new OrderQuery().setOrderCode(orderBO.getOrderCode()));
        return ImeiConvert.INSTANCE.listvo(list);
    }

    private ImeiBO getImeiBO(String imei, String sn, OrderBO orderBO, TradeOrderBO tradeOrderBO) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if (imeiFacade.count(new ImeiQuery().setImel(imei).setSn(sn).setNoActivated(ImeiConsts.Activated.CANCEL.getCode()).setNotEqOrderId(orderBO.getOrderCode()).setGtCreateTime(DateUtil.beginOfDay(DateUtil.date()))) > 0) {
            ImeiBO imeiBO = new ImeiBO().setOrderId(orderBO.getOrderCode()).setTradeNo(tradeOrderBO.getId()).setProductName(orderBO.getProductName()).setSkuName(orderBO.getSkuName())
                    .setSkuCode(orderBO.getSkuCode()).setImel(imei).setHangingOrderId(tradeOrderBO.getHangOrderId());
            imeiBO.setActivated(ImeiConsts.Activated.EXITS.getCode());
            return imeiBO;
        }

        ImeiParam imeiParam = new ImeiParam().setOrderId(orderBO.getOrderCode()).setTradeNo(tradeOrderBO.getId()).setProductName(orderBO.getProductName()).setSkuName(orderBO.getSkuName())
                .setSkuCode(orderBO.getSkuCode()).setImel(imei).setSn(sn).setHangingOrderId(tradeOrderBO.getHangOrderId()).setCreateTime(DateUtil.date());
        ImeiBO imeiBO = imeiFacade.save(imeiParam);
        // 判断是否激活
        String snQ = BrandConsts.VIVO.equalsIgnoreCase(orderBO.getBrand()) ? imeiBO.getImel() : imeiBO.getSn();
        WarrantyResult warranty = snQueryClient.query(snQ, orderBO.getBrand());
        imeiBO.setActivatedTime(DateUtil.date());
        imeiBO.setActivated(warranty.getExits() ? (warranty.isActivated() ? ImeiConsts.Activated.ACTIVATED.getCode() : ImeiConsts.Activated.NOT_ACTIVATED.getCode())
                : ImeiConsts.Activated.NOT_EXITS.getCode());
        // 判断串码型号是否一致
        if (warranty.getExits()) {
            ImeiSkuRelBO imeiSkuRelBO = imeiSkuRelFacade.getOne(new ImeiSkuRelQuery().setLimit(1).setStatus(ImeiConsts.ImeiRel.OK.getCode()).setSnModel(warranty.getModel()));
            // if imeiSkuRelBO
            if (Objects.isNull(imeiSkuRelBO)) {
                ImeiSkuRelParam imeiSkuRelParam = new ImeiSkuRelParam().setBrand(orderBO.getBrand()).setCategory(orderBO.getCategory()).setProductName(orderBO.getProductName())
                        .setSkuName(orderBO.getSkuName()).setSnModel(warranty.getModel()).setCreateTime(DateUtil.date()).setUpdateTime(DateUtil.date()).setStatus(ImeiConsts.ImeiRel.WAIT.getCode());
                imeiSkuRelFacade.save(imeiSkuRelParam);
            } else {
                // 已经有留存,判断串码型号是否一致
                if (Objects.equals(imeiSkuRelBO.getStatus(), ImeiConsts.ImeiRel.OK.getCode())
                        && Objects.equals(imeiSkuRelBO.getSkuName(), orderBO.getSkuName())
                        && Objects.equals(imeiSkuRelBO.getProductName(), orderBO.getProductName())) {
                    imeiBO.setActivated(ImeiConsts.Activated.SUCCESS.getCode());
                } else {
                    imeiBO.setActivated(ImeiConsts.Activated.MODEL_NOT_CONSISTENT.getCode());
                    // 设置识别出来的商品名和型号，返回给前端提示
                    imeiBO.setRecognizedProductName(imeiSkuRelBO.getProductName());
                    imeiBO.setRecognizedSkuName(imeiSkuRelBO.getSkuName());
                }
            }
        }
        imeiFacade.update(new ImeiParam().setActivated(imeiBO.getActivated()), new ImeiQuery().setId(imeiBO.getId()));
        return imeiBO;
    }


    /**
     * 查询订单串码
     *
     * @param orderCode
     * @return
     */

    public List<ImeiVO> list(String orderCode) {

        HangingOrderBO hangingOrderBO = hangingOrderFacade.getOne(new HangingOrderQuery().setOrderId(orderCode).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
        List<ImeiBO> list = imeiFacade.list(new ImeiQuery().setOrderId(orderCode).setHangingOrderId(hangingOrderBO.getId()));
        return ImeiConvert.INSTANCE.toVo(list);

    }
}
