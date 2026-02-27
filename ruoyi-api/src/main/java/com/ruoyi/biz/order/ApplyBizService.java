package com.ruoyi.biz.order;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.ruoyi.biz.company.CompanyCapitalBizService;
import com.ruoyi.capital.facade.ICompanyCapitalFacade;
import com.ruoyi.capital.model.consts.CompanyCapitalConsts;
import com.ruoyi.capital.model.param.CompanyCapitalLogParam;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.mapper.order.ApplyConvert;
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
import com.ruoyi.web.form.order.CancelApplyForm;
import com.ruoyi.web.vo.order.ApplyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Component
public class ApplyBizService {


    @Autowired
    IApplyFacade applyFacade;

    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    IHangingOrderFacade hangingOrderFacade;

    @Autowired
    ITradeOrderFacade tradeFacade;

    @Autowired
    ICompanyFacade companyFacade;

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
     * 处理毁单申请
     *
     * @param cancelApplyForm
     */
    @Transactional
    public void applyCancel(CancelApplyForm cancelApplyForm, Long companyId, Long userId) {

        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(cancelApplyForm.getOrderCode()));
        Assert.notNull(orderBO, "订单不存在");
        Assert.equals(orderBO.getStatus(), OrderConsts.OrderStatus.DELIVERY_ING.getCode(), "订单状态异常");

        HangingOrderBO hangingOrderBO = hangingOrderFacade.getOne(new HangingOrderQuery().setOrderId(cancelApplyForm.getOrderCode()).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
        Assert.notNull(hangingOrderBO, "订单不存在");

        TradeOrderBO tradeOrderBO = tradeFacade.getOne(new TradeOrderQuery().setOrderId(cancelApplyForm.getOrderCode()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));

        if (Objects.equals(cancelApplyForm.getApplyType(), ApplyConsts.Type.AT_ONCE_CANCEL.getCode())) {
            // 设置抢单失效
            tradeFacade.update(new TradeOrderParam().setStatus(TradeOrderConsts.TradeStatus.APPLY.getCode()), new TradeOrderQuery().setOrderId(cancelApplyForm.getOrderCode()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
            // 设置挂单失效
            hangingOrderFacade.update(new HangingOrderParam().setStatus(HandingOrderConsts.Status.FAILURE.getCode()), new HangingOrderQuery().setOrderId(cancelApplyForm.getOrderCode()).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
            // 设置订单失效
            orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.WAIT.getCode()).setSubStatus(OrderConsts.OrderSubStatus.WAIT_DESTRO.getCode()),
                    new OrderQuery().setOrderCode(cancelApplyForm.getOrderCode()));
            //   返回保证金, 80%
            CompanyCapitalLogParam companyCapitalLogParam = new CompanyCapitalLogParam().setCompanyId(hangingOrderBO.getLastCompeteCompany()).setUpdateTime(DateUtil.date()).setAddAmount(companyCapitalBizService.calAmountRate(orderBO.getQuantity()))
                    .setOrderNo(orderBO.getOrderCode()).setType(CompanyCapitalConsts.LogTypes.ORDER.getCode()).setTradeId(tradeOrderBO.getId());
            companyCapitalFacade.unFreeze(companyCapitalLogParam);
            // 保存毁单申请记录
            applyFacade.save(new ApplyParam().setOrderId(cancelApplyForm.getOrderCode()).setType(ApplyConsts.Type.AT_ONCE_CANCEL.getCode()).setCompanyId(companyId).setCreateBy(userId));
            return;
        } else if (Objects.equals(cancelApplyForm.getApplyType(), ApplyConsts.Type.APPLY_CANCEL.getCode())) {
            // 保存毁单申请记录
            applyFacade.save(new ApplyParam().setOrderId(cancelApplyForm.getOrderCode()).setHangOrderId(hangingOrderBO.getId()).setRemark(cancelApplyForm.getCancelReason())
                    .setType(ApplyConsts.Type.AT_ONCE_CANCEL.getCode()).setCompanyId(companyId).setCreateBy(userId));
            // 设置订单需要处理申请
            orderFacade.update(new OrderParam().setHandleApply(OrderConsts.HandleApply.NEED.getCode()), new OrderQuery().setOrderCode(cancelApplyForm.getOrderCode()));
            return;
        }
        throw new ServiceException("暂不支持此申请类型");

    }
}
