package com.ruoyi.biz.order;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.biz.sys.IDictDistrictBizService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.mapper.order.HangingOrderConvert;
import com.ruoyi.mapper.rule.RuleConvert;
import com.ruoyi.order.facade.IHangingOrderFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.consts.HandingOrderConsts;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.consts.TradeOrderConsts;
import com.ruoyi.order.model.param.HangingOrderParam;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.HangingOrderQuery;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.product.facade.IProductSkuFacade;
import com.ruoyi.product.model.bo.ProductSkuBO;
import com.ruoyi.product.model.query.ProductSkuQuery;
import com.ruoyi.rule.facade.IRuleFacade;
import com.ruoyi.rule.model.bo.RuleBO;
import com.ruoyi.rule.model.consts.RuleConsts;
import com.ruoyi.rule.model.param.RuleParam;
import com.ruoyi.rule.model.query.RuleQuery;
import com.ruoyi.system.model.bo.DictDistrictBO;
import com.ruoyi.web.form.rule.RuleQueryForm;
import com.ruoyi.web.vo.order.RuleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RuleBizService {

    @Autowired
    public IRuleFacade ruleFacade;

    @Autowired
    private IOrderFacade orderFacade;

    @Autowired
    private IHangingOrderFacade hangingOrderFacade;

    @Autowired
    private IProductSkuFacade productSkuFacade;

    @Autowired
    private IDictDistrictBizService dictDistrictBizService;


    public PageBO<RuleVO> listPage(RuleQueryForm ruleQueryForm, PageParamV2 pageParamV2) {
        PageBO<RuleBO> ruleBOPageBO = ruleFacade.listPage(RuleConvert.INSTANCE.toQuery(ruleQueryForm), pageParamV2);

        List<RuleVO> ruleVOList = RuleConvert.INSTANCE.toVOList(ruleBOPageBO.getData());

        Map<Long, String> provinceMap = dictDistrictBizService.listProvince().stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
        for (RuleVO ruleVO : ruleVOList) {
            ruleVO.setProvinceName(provinceMap.get(ruleVO.getProvince()));
        }
        return new PageBO(ruleVOList, ruleBOPageBO.getTotal());

    }

    /**
     * @param param
     */
    public RuleBO saveOrUpdate(final RuleParam param) {

        ruleFacade.delete(new RuleQuery().setSkuCode(param.getSkuCode()).setRuleRange(param.getRuleRange())
                .setStatus(RuleConsts.Status.NORMAL.getCode()).setProvince(param.getProvince()));

        ProductSkuBO productSkuBO = productSkuFacade.getOne(new ProductSkuQuery().setSkuCode(param.getSkuCode()));
        if (Objects.isNull(productSkuBO)) {
            throw new ServiceException("请求商品参数异常");
        }
        param.setSkuName(productSkuBO.getSpecName()).setBrand(productSkuBO.getBrand()).setCategory(productSkuBO.getCategory())
                .setProductName(productSkuBO.getProductName()).setSkuCode(productSkuBO.getSkuCode());
        return ruleFacade.save(param);
    }

    /**
     * 执行规则
     *
     * @param ruleBO
     */
    @Async
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void execute(RuleBO ruleBO) {
        OrderQuery orderQuery = new OrderQuery().setStatusList(List.of(OrderConsts.OrderStatus.NEW.getCode(), OrderConsts.OrderStatus.WAIT.getCode())).setSkuCode(ruleBO.getSkuCode());
        if (ruleBO.getRuleRange() == RuleConsts.Range.PROVINCE.getCode()) {
            orderQuery.setProvince(ruleBO.getProvince());
        }
        List<OrderBO> list = orderFacade.list(orderQuery);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        for (OrderBO orderBO : list) {
            long count = hangingOrderFacade.count(new HangingOrderQuery().setOrderId(orderBO.getOrderCode()).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
            if (count > 0) {
                continue;
            }
            log.info("执行规则：{}", ruleBO);
            //保存挂单
            HangingOrderParam hangingOrderParam = HangingOrderConvert.INSTANCE.ruleToHangingParam(ruleBO).setId(null).setPriceHignStatus(TradeOrderConsts.TradeStatus.CONFIRMED.getCode())
                    .setPriceHighestStatus(TradeOrderConsts.TradeStatus.CONFIRMED.getCode()).setPriceLowestStatus(TradeOrderConsts.TradeStatus.CONFIRMED.getCode()).setRuleId(ruleBO.getId())
                    .setPriceLowStatus(TradeOrderConsts.TradeStatus.CONFIRMED.getCode()).setStatus(HandingOrderConsts.Status.NORMAL.getCode()).setOrderId(orderBO.getOrderCode())
                    .setAccountingPeriod(ruleBO.getAccountingPeriod()).setDeliveryTime(ruleBO.getDeliveryTime())
                    .setDeliveryDeadline(DateUtil.offsetDay(DateUtil.endOfDay(DateTime.now()), ruleBO.getDeliveryTime()));
            log.info("保存挂单：{}", hangingOrderParam);
            hangingOrderFacade.save(hangingOrderParam);

            // 修改订单状态
            OrderParam orderParam = new OrderParam();
            if (Objects.equals(orderBO.getAddressStatus(), OrderConsts.AddressStatus.NOT_SUPPLEMENTED.getCode())) {
                orderParam.setStatus(OrderConsts.OrderStatus.WAIT.getCode());
            } else {
                orderParam.setStatus(OrderConsts.OrderStatus.TRADING.getCode());
            }
            orderFacade.update(orderParam, new OrderQuery().setOrderCode(orderBO.getOrderCode()));
        }
    }

}
