package com.ruoyi.biz.order;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.DateTime;
import com.ruoyi.biz.express.JkyStockInAndDeliveryBizService;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.mapper.order.ImeiConvert;
import com.ruoyi.order.facade.IHangingOrderFacade;
import com.ruoyi.order.facade.IImeiFacade;
import com.ruoyi.order.facade.IImeiSkuRelFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.model.bo.*;
import com.ruoyi.order.model.consts.HandingOrderConsts;
import com.ruoyi.order.model.consts.ImeiConsts;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.param.ImeiParam;
import com.ruoyi.order.model.param.ImeiSkuRelParam;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.*;
import com.ruoyi.web.form.order.ActivatedImeiForm;
import com.ruoyi.web.form.order.ImeiForm;
import com.ruoyi.web.form.order.ExcelForm;
import com.ruoyi.web.form.order.PlatformImeiForm;
import com.ruoyi.web.vo.order.ImeiQueryVO;
import com.ruoyi.web.vo.order.ImeiRelVO;
import com.ruoyi.web.vo.order.ImeiVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImeiBizService {

    @Autowired
    IImeiFacade imeiFacade;

    @Autowired
    IImeiSkuRelFacade imeiSkuRelFacade;

    @Autowired
    IHangingOrderFacade hangingOrderFacade;

    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    IRouteSubscribeFacade routeSubscribeFacade;

    @Autowired
    JkyStockInAndDeliveryBizService jkyStockInAndDeliveryBizService;


    public List<ImeiVO> list(final String orderCode) {

        HangingOrderBO hangingOrderBO = hangingOrderFacade.getOne(new HangingOrderQuery().setOrderId(orderCode).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
        List<ImeiBO> list = imeiFacade.list(new ImeiQuery().setOrderId(orderCode).setHangingOrderId(hangingOrderBO.getId()));
        return ImeiConvert.INSTANCE.toVo(list);


    }

    public PageBO<ImeiRelVO> listRel(ImeiForm imeiForm, PageParamV2 pageParamV2) {
        ImeiSkuRelQuery query = ImeiConvert.INSTANCE.toQuery(imeiForm);
        PageBO<ImeiSkuRelBO> imeiSkuRelBOPageBO = imeiSkuRelFacade.listPage(query, pageParamV2);
        return new PageBO<ImeiRelVO>().setData(ImeiConvert.INSTANCE.toRelVo(imeiSkuRelBOPageBO.getData())).setTotal(imeiSkuRelBOPageBO.getTotal());

    }


    public void refuse(Long id) {
        ImeiSkuRelBO imeiSkuRelBO = imeiSkuRelFacade.getOne(new ImeiSkuRelQuery().setId(id));
        Assert.notNull(imeiSkuRelBO, "订单不存在");
        imeiSkuRelFacade.update(new ImeiSkuRelParam().setStatus(ImeiConsts.ImeiRel.CANCEL.getCode()), new ImeiSkuRelQuery().setId(id));
        List<ImeiBO> imeiBOS = imeiFacade.list(new ImeiQuery().setActivated(ImeiConsts.Activated.NOT_ACTIVATED.getCode()).setProductName(imeiSkuRelBO.getProductName()).setSkuName(imeiSkuRelBO.getSkuName()));
        for (ImeiBO imeiBO : imeiBOS) {
            imeiFacade.update(new ImeiParam().setActivated(ImeiConsts.Activated.MODEL_NOT_CONSISTENT.getCode()), new ImeiQuery().setId(imeiBO.getId()));
        }
    }

    @Transactional
    public void agree(Long id, Long userId, String userName) {
        ImeiSkuRelBO imeiSkuRelBO = imeiSkuRelFacade.getOne(new ImeiSkuRelQuery().setId(id));
        Assert.notNull(imeiSkuRelBO, "订单不存在");
        ImeiSkuRelParam param = new ImeiSkuRelParam()
                .setStatus(ImeiConsts.ImeiRel.OK.getCode())
                .setConfirmBy(userId)
                .setConfirmName(userName);
        imeiSkuRelFacade.update(param, new ImeiSkuRelQuery().setId(id));
        List<ImeiBO> imeiBOS = imeiFacade.list(new ImeiQuery().setActivated(ImeiConsts.Activated.NOT_ACTIVATED.getCode()).setProductName(imeiSkuRelBO.getProductName()).setSkuName(imeiSkuRelBO.getSkuName()));
        for (ImeiBO imeiBO : imeiBOS) {
            imeiFacade.update(new ImeiParam().setActivated(ImeiConsts.Activated.SUCCESS.getCode()), new ImeiQuery().setId(imeiBO.getId()));
        }
    }

    public void del(Long id) {

        imeiSkuRelFacade.del(new ImeiSkuRelQuery().setId(id));

    }

    public Long count(Integer status) {

        Long count = imeiSkuRelFacade.count(new ImeiSkuRelQuery().setStatus(status));
        return count;
    }

    /**
     * 通过平台+品牌+品类查询待发货订单的sn、imei
     *
     * @param excelForm
     * @return
     */
    public List<ImeiQueryVO> listImeiInfo(ExcelForm excelForm) {
        DateTime dateTime = DateUtil.beginOfDay(DateUtil.offsetDay(DateUtil.date(), -15));
        List<OrderBO> orderBOList = orderFacade.list(new OrderQuery().setStatus(OrderConsts.OrderStatus.DELIVERY_ING.getCode()).setCreateTimeStart(dateTime)
                .setCategoryList(excelForm.getCategoryList()).setBrandSet(excelForm.getBrandSet()).setPlatformList(excelForm.getPlatformList()));
        if (CollectionUtil.isEmpty(orderBOList)) {
            return Collections.emptyList();
        }
        List<ImeiQueryVO> list = new ArrayList<>();
        for (OrderBO orderBO : orderBOList) {
            List<ImeiBO> imeiBOS = imeiFacade.list(new ImeiQuery().setOrderId(orderBO.getOrderCode()).setPlatformImei(ImeiConsts.PlatformImei.WAIT_QUERY.getCode()).setActivated(ImeiConsts.Activated.SUCCESS.getCode()));
            if (CollectionUtil.isEmpty(imeiBOS)) {
                continue;
            }
            for (ImeiBO imeiBO : imeiBOS) {
                ImeiQueryVO vo = new ImeiQueryVO();
                vo.setOrderCode(orderBO.getOrderCode());
                vo.setSn(imeiBO.getSn());
                vo.setImei(imeiBO.getImel());
                list.add(vo);
            }
        }
        return list;
    }

    /**
     * 修改平台校验状态并更新订单状态
     *
     * @param form
     */
    @Transactional
    public void updatePlatformImei(PlatformImeiForm form) {
        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(form.getOrderCode()));
        Assert.notNull(orderBO, "订单不存在");
        Assert.isTrue(Objects.equals(orderBO.getStatus(), OrderConsts.OrderStatus.DELIVERY_ING.getCode()), "只有发货中订单可以更新平台校验状态");
        // 更新 o_imei 的 platform_imei 字段
        boolean update = imeiFacade.update(new ImeiParam().setPlatformImei(form.getStatus()).setPlatformTime(DateUtil.date()),
                new ImeiQuery().setOrderId(form.getOrderCode()).setSn(form.getSn()).setImel(form.getImei()));
        if (!update) {
            return;
        }

        if (Objects.equals(form.getStatus(), ImeiConsts.PlatformImei.NORMAL.getCode())) {
            RouteSubscribeBO routeSubscribeBO = routeSubscribeFacade.getOne(new RouteSubscribeQuery().setOrderCode(orderBO.getOrderCode()));
            if (Objects.nonNull(routeSubscribeBO)) {
                orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.DELIVERY_END.getCode()).setUpdateTime(DateUtil.date()),
                        new OrderQuery().setOrderCode(form.getOrderCode()));

                log.info("订单号：{}，平台校验正常触发吉客云入库(admin:updatePlatformImei)", orderBO.getOrderCode());
                jkyStockInAndDeliveryBizService.createJkyStockIn(orderBO, routeSubscribeBO);
            } else {
                orderFacade.update(new OrderParam().setSubStatus(OrderConsts.OrderSubStatus.WAIT_EXPRESS.getCode()).setUpdateTime(DateUtil.date()),
                        new OrderQuery().setOrderCode(form.getOrderCode()));
            }
        } else {
            orderFacade.update(new OrderParam().setSubStatus(OrderConsts.OrderSubStatus.WAIT_SALES_ERROR.getCode()).setUpdateTime(DateUtil.date()),
                    new OrderQuery().setOrderCode(form.getOrderCode()));
        }
    }

    /**
     * 人工放行串码激活状态：将处于 NOT_EXITS（06api 查询失败）的串码改为 SUCCESS。
     * 仅允许该状态转换；其它失败状态（如型号不一致）不允许通过本接口放行。
     *
     * @param form 表单（订单号 + sn/imei）
     */
    public void manualPassActivated(ActivatedImeiForm form) {
        ImeiBO imeiBO = imeiFacade.getOne(new ImeiQuery()
                .setOrderId(form.getOrderCode())
                .setSn(form.getSn())
                .setImel(form.getImei()));
        Assert.notNull(imeiBO, "串码记录不存在");
        Assert.isTrue(Objects.equals(imeiBO.getActivated(), ImeiConsts.Activated.NOT_EXITS.getCode()),
                "只有验证失败（NOT_EXITS）的串码可人工放行");

        boolean updated = imeiFacade.update(
                new ImeiParam().setActivated(ImeiConsts.Activated.SUCCESS.getCode()),
                new ImeiQuery()
                        .setId(imeiBO.getId())
                        .setActivated(ImeiConsts.Activated.NOT_EXITS.getCode()));
        Assert.isTrue(updated, "串码状态已被他人变更，请刷新后重试");

        LoginUser loginUser = SecurityUtils.getLoginUser();
        log.warn("人工放行串码激活状态成功，operatorId={}, operatorName={}, orderCode={}, sn={}, imei={}, oldActivated={}, newActivated={}",
                loginUser.getUserId(), loginUser.getUsername(),
                form.getOrderCode(), form.getSn(), form.getImei(),
                ImeiConsts.Activated.NOT_EXITS.getCode(),
                ImeiConsts.Activated.SUCCESS.getCode());
    }

}
