package com.ruoyi.biz.order;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.ruoyi.biz.company.CompanyCapitalBizService;
import com.ruoyi.capital.facade.ICompanyCapitalFacade;
import com.ruoyi.capital.model.consts.CompanyCapitalConsts;
import com.ruoyi.capital.model.param.CompanyCapitalLogParam;
import com.ruoyi.mapper.apply.ApplyConvert;
import com.ruoyi.order.facade.IApplyFacade;
import com.ruoyi.order.facade.IHangingOrderFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.facade.ITradeOrderFacade;
import com.ruoyi.order.model.bo.ApplyBO;
import com.ruoyi.order.model.bo.HangingOrderBO;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.bo.TradeOrderBO;
import com.ruoyi.order.model.consts.ApplyConsts;
import com.ruoyi.order.model.consts.HandingOrderConsts;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.consts.TradeOrderConsts;
import com.ruoyi.order.model.param.ApplyParam;
import com.ruoyi.order.model.param.HangingOrderParam;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.param.TradeOrderParam;
import com.ruoyi.order.model.query.ApplyQuery;
import com.ruoyi.order.model.query.HangingOrderQuery;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.order.model.query.TradeOrderQuery;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.web.vo.apply.ApplyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ApplyBizService {

    @Autowired
    IApplyFacade applyFacade;

    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    IHangingOrderFacade hangingOrderFacade;

    @Autowired
    ICompanyFacade companyFacade;

    @Autowired
    ITradeOrderFacade tradeOrderFacade;

    @Autowired
    ICompanyCapitalFacade companyCapitalFacade;

    @Autowired
    CompanyCapitalBizService companyCapitalBizService;


    public ApplyVO queryApply(String orderCode) {

        OrderBO orderBo = orderFacade.getOne(new OrderQuery().setOrderCode(orderCode));
        Assert.notNull(orderBo, "订单不存在");

        HangingOrderBO hangingOrderBO = hangingOrderFacade.getOne(new HangingOrderQuery().setOrderId(orderCode).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
        Assert.notNull(hangingOrderBO, "订单不存在");

        ApplyBO applyBO = applyFacade.getOne(new ApplyQuery().setOrderId(orderCode).setHangOrderId(hangingOrderBO.getId()));
        Assert.notNull(applyBO, "订单申请不存在");

        ApplyVO applyVO = ApplyConvert.INSTANCE.toVO(applyBO);
        CompanyBO companyBO = companyFacade.queryOne(new CompanyQuery().setId(applyBO.getCompanyId()));
        applyVO.setCompanyName(companyBO.getCompanyName());

        return applyVO;
    }


    /**
     * 同意订单毁单申请
     *
     * @param
     */
    @Transactional
    public void agreement(Long applyId) {

        ApplyBO applyBO = applyFacade.getOne(new ApplyQuery().setId(applyId));
        Assert.notNull(applyBO, "订单申请不存在");
        OrderBO orderBo = orderFacade.getOne(new OrderQuery().setOrderCode(applyBO.getOrderId()));
        Assert.notNull(orderBo, "订单不存在");
        HangingOrderBO hangingOrderBO = hangingOrderFacade.getOne(new HangingOrderQuery().setOrderId(orderBo.getOrderCode()).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
        Assert.notNull(hangingOrderBO, "挂单不存在");
        TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setOrderId(orderBo.getOrderCode()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
        Assert.notNull(tradeOrderBO, "交易单不存在");

        // 删除申请
        applyFacade.update(new ApplyParam().setStatus(ApplyConsts.ApplyStatus.AGREE.getCode()), new ApplyQuery().setOrderId(applyBO.getOrderId()).setHangOrderId(applyBO.getHangOrderId()));
        // 修改订单状态、 挂单状态 、 trade 状态
        tradeOrderFacade.update(new TradeOrderParam().setStatus(TradeOrderConsts.TradeStatus.TAKEN.getCode()),
                new TradeOrderQuery().setOrderId(applyBO.getOrderId()).setHangOrderId(applyBO.getHangOrderId()));
        // 修改挂单状态
        hangingOrderFacade.update(new HangingOrderParam().setStatus(HandingOrderConsts.Status.FAILURE.getCode()),
                new HangingOrderQuery().setOrderId(applyBO.getOrderId()).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
        // 修改订单状态
        orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.WAIT.getCode()).setHandleApply(OrderConsts.HandleApply.NO.getCode()).setSubStatus(OrderConsts.OrderSubStatus.WAIT_DESTRO.getCode()),
                new OrderQuery().setOrderCode(applyBO.getOrderId()));
        // 退保证金
        companyCapitalFacade.unFreeze(new CompanyCapitalLogParam().setCompanyId(tradeOrderBO.getTradeCompanyId()).setUpdateTime(DateUtil.date())
                .setAddAmount(companyCapitalBizService.calAmount(orderBo.getQuantity()))
                .setOrderNo(applyBO.getOrderId()).setType(CompanyCapitalConsts.LogTypes.ORDER.getCode()).setTradeId(tradeOrderBO.getId()));

    }

    /**
     * 拒绝毁单 申请
     *
     * @param applyId
     */
    public void fail(Long applyId, String refuseRemark) {

        ApplyBO applyBO = applyFacade.getOne(new ApplyQuery().setId(applyId));
        Assert.notNull(applyBO, "订单申请不存在");
        // 删除申请
        applyFacade.update(new ApplyParam().setStatus(ApplyConsts.ApplyStatus.REFUSE.getCode()).setRefuseRemark(refuseRemark)
                , new ApplyQuery().setId(applyId));

    }
}
