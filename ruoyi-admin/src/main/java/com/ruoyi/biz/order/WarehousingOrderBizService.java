package com.ruoyi.biz.order;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.biz.bill.BillBizService;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.Arith;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.express.model.consts.LogisticsCode;
import com.ruoyi.jky.JkyTemplate;
import com.ruoyi.jky.param.stock.StockCreateAndStockInParam;
import com.ruoyi.mapper.order.OrderConvert;
import com.ruoyi.mapper.order.WarehousingConvert;
import com.ruoyi.order.facade.IHangingOrderFacade;
import com.ruoyi.order.facade.IImeiFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.facade.ITradeOrderFacade;
import com.ruoyi.order.model.bo.*;
import com.ruoyi.order.model.consts.HandingOrderConsts;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.consts.TradeOrderConsts;
import com.ruoyi.order.model.param.HangingOrderParam;
import com.ruoyi.order.model.param.ImeiParam;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.param.TradeOrderParam;
import com.ruoyi.order.model.query.HangingOrderQuery;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.order.model.query.TradeOrderQuery;
import com.ruoyi.product.facade.IProductSkuFacade;
import com.ruoyi.product.model.bo.ProductSkuBO;
import com.ruoyi.product.model.query.ProductSkuQuery;
import com.ruoyi.system.facade.ISysUserFacade;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.facade.IMemberFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.bo.MemberBO;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.user.model.query.MemberQuery;
import com.ruoyi.wangdian.param.stock.StockInInfoGoodsList;
import com.ruoyi.wangdian.param.stock.StockInInfoParam;
import com.ruoyi.wangdian.utils.WdtClient;
import com.ruoyi.web.form.order.BrandForm;
import com.ruoyi.web.form.order.PickingOrderForm;
import com.ruoyi.web.form.order.TrackingForm;
import com.ruoyi.web.form.order.WarehousingOrderParam;
import com.ruoyi.web.form.order.WarehousingSaveParam;
import com.ruoyi.web.vo.order.BrandCountVO;
import com.ruoyi.web.vo.order.WarehousingOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WarehousingOrderBizService {

    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    ISysUserFacade sysUserFacade;

    @Autowired
    IMemberFacade memberFacade;

    @Autowired
    IHangingOrderFacade hangingOrderFacade;

    @Autowired
    ITradeOrderFacade tradeOrderFacade;

    @Autowired
    IProductSkuFacade productSkuFacade;

    @Autowired
    ICompanyFacade companyFacade;

    @Autowired
    BillBizService billBizService;

    @Autowired
    IImeiFacade imeiFacade;

    @Autowired
    WdtClient wdtClient;

    @Autowired
    JkyTemplate jkyTemplate;


    /**
     * 入仓订单列表
     *
     * @param warehousingOrderParam
     * @param pageParamV2
     * @return
     */
    public PageBO<WarehousingOrderVO> orderList(WarehousingOrderParam warehousingOrderParam, PageParamV2 pageParamV2) {
        DateTime dateTime = DateUtil.offsetDay(DateUtil.date(), -15);
        OrderQuery orderQuery = new OrderQuery().setProductNameLike(warehousingOrderParam.getProductName()).setStatusList(warehousingOrderParam.getStatusList())
                .setOrderType(OrderConsts.OrderType.PROCUREMENT.getCode()).setDeliveryCode(warehousingOrderParam.getDeliveryCode())
                .setCompanyId(warehousingOrderParam.getCompanyId()).setBrand(warehousingOrderParam.getBrand()).setCreateTime(dateTime).setOrderType(OrderConsts.OrderType.PROCUREMENT.getCode());
        PageBO<CompanyOrderBO> companyOrderBOPageBO = orderFacade.companyListPage(orderQuery, pageParamV2);
        List<WarehousingOrderVO> vo = WarehousingConvert.INSTANCE.toVO(companyOrderBOPageBO.getData());
        Set<Long> userIds = vo.stream().map(e -> Convert.toLong(e.getCreateBy())).collect(Collectors.toSet());
        List<SysUser> userBOS = sysUserFacade.selectUserByIds(userIds);
        Map<Long, String> userBOMap = userBOS.stream().collect(Collectors.toMap(userBO -> userBO.getUserId(), userBO -> userBO.getNickName()));
        for (WarehousingOrderVO warehousingOrderVO : vo) {
            warehousingOrderVO.setCreateBy(String.valueOf(userBOMap.get(Convert.toLong(warehousingOrderVO.getCreateBy()))));
        }
        return new PageBO<>(vo, companyOrderBOPageBO.getTotal());
    }

    /**
     * 保存入仓订单
     *
     * @param warehousingSaveParam
     */
    @Transactional
    public void save(WarehousingSaveParam warehousingSaveParam, LoginUser loginUser) {

        ProductSkuBO productSkuBO = productSkuFacade.getOne(new ProductSkuQuery().setSkuCode(warehousingSaveParam.getSkuCode()));
        Assert.notNull(productSkuBO, "商品编码不存在");

        PageBO<MemberBO> memberBOPageBO = memberFacade.memberList(new MemberQuery().setCompanyId(warehousingSaveParam.getCompanyId()), new PageParamV2());
        Assert.notNull(memberBOPageBO.getData(), "企业不存在主账号，请先添加主账号后在推单");
        MemberBO memberBO = memberBOPageBO.getData().get(0);

        CompanyBO companyBO = companyFacade.queryOne(new CompanyQuery().setId(warehousingSaveParam.getCompanyId()));
        Assert.notNull(companyBO, "企业不存在,请重新选择企业");


        String orderERPCode = IdUtil.objectId();
        DateTime now = DateUtil.date();
        // 构建订单
        OrderParam orderParam = new OrderParam().setErpOrderId(orderERPCode).setOriginalOrderId(orderERPCode).setOrderType(OrderConsts.OrderType.PROCUREMENT.getCode())
                .setShopName(OrderConsts.SHOP_NAME).setPlatform(OrderConsts.PLATFORM).setBrand(productSkuBO.getBrand()).setCategory(productSkuBO.getCategory())
                .setProductName(productSkuBO.getProductName()).setSkuName(productSkuBO.getSpecName()).setSkuCode(productSkuBO.getSkuCode()).setQuantity(warehousingSaveParam.getQuantity())
                .setStatus(OrderConsts.OrderStatus.DELIVERY_END.getCode()).setSendTime(now).setCreateTime(now).setUpdateTime(now).setRemark(warehousingSaveParam.getRemark());
        OrderBO orderBO = orderFacade.save(orderParam);
        // 构建挂单
        HangingOrderParam hangingOrderParam = new HangingOrderParam().setPriceHighest(warehousingSaveParam.getPrice()).setPriceHighestStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode());
        hangingOrderParam.setPriceHign(Arith.sub(warehousingSaveParam.getPrice(), new BigDecimal(1))).setPriceHignStatus(TradeOrderConsts.TradeStatus.CONFIRMED.getCode());
        hangingOrderParam.setPriceLow(Arith.sub(hangingOrderParam.getPriceHign(), new BigDecimal(1))).setPriceLowStatus(TradeOrderConsts.TradeStatus.CONFIRMED.getCode());
        hangingOrderParam.setPriceLowest(Arith.sub(hangingOrderParam.getPriceLow(), new BigDecimal(1))).setPriceLowestStatus(TradeOrderConsts.TradeStatus.CONFIRMED.getCode());
        hangingOrderParam.setQuotationInterval(5L).setAccountingPeriod(warehousingSaveParam.getAccountingPeriod()).setStatus(HandingOrderConsts.Status.NORMAL.getCode());
        hangingOrderParam.setLastCompeteUser(memberBO.getUserId()).setLastCompeteCompany(warehousingSaveParam.getCompanyId()).setLastCompeteTime(DateUtil.date()).setCreateBy(loginUser.getUserId());
        hangingOrderParam.setCreateTime(DateUtil.date()).setUpdateTime(DateUtil.date()).setUpdateBy(loginUser.getUserId()).setIntervalSpread(new BigDecimal(10));
        hangingOrderParam.setCodeOptions(HandingOrderConsts.CodeOptions.SEND_BEFORE_NEED.getCode()).setMerchantCompanyId(warehousingSaveParam.getCompanyId());
        hangingOrderParam.setDeliveryTime(0).setDeliveryDeadline(DateUtil.offsetDay(DateUtil.endOfDay(DateUtil.date()), 0));
        hangingOrderParam.setOrderId(orderBO.getOrderCode());
        HangingOrderBO hangingOrderBO = hangingOrderFacade.save(hangingOrderParam);

        // 构建trade 对象
        TradeOrderParam tradeOrderParam = new TradeOrderParam().setOrderType(OrderConsts.OrderType.PROCUREMENT.getCode()).setTradeCompanyId(warehousingSaveParam.getCompanyId());
        tradeOrderParam.setTradeUserId(memberBO.getUserId()).setTradeUserPhone(memberBO.getPhone()).setTradeUserName(memberBO.getNickName()).setAccountingPeriod(warehousingSaveParam.getAccountingPeriod());
        tradeOrderParam.setTradeCompanyId(companyBO.getId()).setTradeCompanyName(companyBO.getCompanyName()).setTradeNickName(companyBO.getNickName()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode());
        tradeOrderParam.setUpdateTime(DateUtil.date()).setUpdateBy(loginUser.getUserId()).setDeliveryCode(RandomUtil.randomInt(100000, 1000000));
        tradeOrderParam.setOrderId(orderBO.getOrderCode()).setTradePrice(warehousingSaveParam.getPrice()).setBrand(orderBO.getBrand()).setProductName(orderBO.getProductName());
        tradeOrderParam.setSkuName(orderBO.getSkuName()).setSkuCode(orderBO.getSkuCode()).setProvince(orderBO.getProvince()).setQuantity(0);
        tradeOrderParam.setOrderType(orderBO.getOrderType()).setTradeIndex(4).setHangOrderId(hangingOrderBO.getId()).setOrderId(orderBO.getOrderCode());
        tradeOrderParam.setTrackingCompany(LogisticsCode.ZS.getMsg());
        tradeOrderFacade.save(tradeOrderParam);
        log.info("保存入仓订单成功,{}", orderBO.getOrderCode());

    }

    /**
     * 撤销订单
     */
    public void revoke(String orderCode, LoginUser loginUser) {

        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(orderCode));
        Assert.notNull(orderBO, "订单不存在");

        TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setOrderId(orderBO.getOrderCode()));
        Assert.notNull(tradeOrderBO, "订单不存在");

        Assert.isTrue(tradeOrderBO.getQuantity() == 0, "订单已经部分入仓");

        orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.REVOKE.getCode()).setUpdateTime(DateUtil.date()),
                new OrderQuery().setOrderCode(orderCode));
        log.info("入仓订单：{}，被用户{}撤销", orderCode, loginUser.getUsername());
    }

    public List<BrandCountVO> brandCount(BrandForm provinceForm) {

        DateTime dateTime = DateUtil.offsetDay(DateUtil.date(), -15);

        List<BrandCountBO> brandCountBOS = orderFacade.brandCount(new OrderQuery().setStatus(provinceForm.getStatus()).setStatusList(provinceForm.getStatusList())
                .setCreateTimeStart(dateTime).setOrderType(OrderConsts.OrderType.PROCUREMENT.getCode()));

        return OrderConvert.INSTANCE.toBrandCountVO(brandCountBOS);


    }

    /**
     * 完成拣货
     *
     * @param orderCode
     * @param loginUser
     */
    public void confirm(String orderCode, LoginUser loginUser) {
        log.info("完成拣货,{},操作用户：{}", orderCode, loginUser.getUsername());
        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(orderCode));
        Assert.notNull(orderBO, "订单不存在");
        List<Integer> statusList = List.of(OrderConsts.OrderStatus.DELIVERY_END.getCode(), OrderConsts.OrderStatus.TRANSIT.getCode());
        Assert.isTrue(statusList.contains(orderBO.getStatus()), "订单状态异常");
        // 设置订单完成状态
        DateTime date = DateUtil.date();
        orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.ENDING.getCode()).setUpdateTime(date).setSignedTime(date),
                new OrderQuery().setOrderCode(orderCode));
        // 生成财务账单
        billBizService.generateBill(orderBO);
        // 
    }


    /**
     * 拣货入仓
     *
     * @param pickingOrderForm
     * @param loginUser
     */
    @Transactional
    public void picking(PickingOrderForm pickingOrderForm, LoginUser loginUser) throws IOException {

        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(pickingOrderForm.getOrderCode()));
        Assert.notNull(orderBO, "订单不存在");
        HangingOrderBO hangingOrderBO = hangingOrderFacade.getOne(new HangingOrderQuery().setOrderId(pickingOrderForm.getOrderCode()).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
        Assert.notNull(hangingOrderBO, "订单挂单不存在");
        TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setOrderId(pickingOrderForm.getOrderCode()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
        Assert.notNull(tradeOrderBO, "订单成交记录不存在");

        log.info("开始拣货,{},操作用户：{}, 拣货信息：{}", pickingOrderForm.getOrderCode(), loginUser.getUsername(), JacksonUtil.toJson(pickingOrderForm));

        Integer quantity = null;
        if (Objects.isNull(pickingOrderForm.getQuantity())) {
            quantity = pickingOrderForm.getSnList().size() + tradeOrderBO.getQuantity();
            Assert.isFalse(quantity > orderBO.getQuantity(), "订单入库数量大于成交数量");

            // sn 入库, 保存sn ， 回传旺店通
            ImeiParam imeiParam = new ImeiParam().setCreateTime(DateUtil.date()).setProductName(orderBO.getProductName()).setSkuName(orderBO.getSkuName())
                    .setSkuCode(orderBO.getSkuCode()).setHangingOrderId(hangingOrderBO.getId()).setTradeNo(tradeOrderBO.getId());
            for (String sn : pickingOrderForm.getSnList()) {
                imeiParam.setImel(sn);
                imeiFacade.save(imeiParam);
            }

            //  回传旺店通
            StockInInfoParam stockInInfoParam = builderStockIn(orderBO, pickingOrderForm.getSnList().size(), pickingOrderForm.getWarehouseCode(), pickingOrderForm.getSnList(), pickingOrderForm.getRemark());
            wdtClient.stockInPush(stockInInfoParam);
            createJkyStockIn(orderBO, pickingOrderForm.getWarehouseCode(), pickingOrderForm.getSnList().size());

            tradeOrderFacade.update(new TradeOrderParam().setQuantity(quantity).setUpdateTime(DateUtil.date()),
                    new TradeOrderQuery().setOrderId(pickingOrderForm.getOrderCode()));
        } else {
            quantity = pickingOrderForm.getQuantity() + tradeOrderBO.getQuantity();
            Assert.isFalse(quantity > orderBO.getQuantity(), "订单入库数量大于成交数量");

            // 非 sn 管理
            StockInInfoParam stockInInfoParam = builderStockIn(orderBO, quantity, pickingOrderForm.getWarehouseCode(), pickingOrderForm.getSnList(), pickingOrderForm.getRemark());
            wdtClient.stockInPush(stockInInfoParam);
            createJkyStockIn(orderBO, pickingOrderForm.getWarehouseCode(), pickingOrderForm.getQuantity());
            // 更新成交记录
            tradeOrderFacade.update(new TradeOrderParam().setQuantity(quantity).setUpdateTime(DateUtil.date()),
                    new TradeOrderQuery().setOrderId(pickingOrderForm.getOrderCode()));
        }

        // 订单数量全部拣货完成
        if (Objects.equals(quantity, orderBO.getQuantity())) {
            DateTime date = DateUtil.date();
            orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.ENDING.getCode()).setUpdateTime(date).setSignedTime(date),
                    new OrderQuery().setOrderCode(pickingOrderForm.getOrderCode()));
            // 生成财务账单
            billBizService.generateBill(orderBO);
        }

    }


    /**
     * 修改物流信息
     *
     * @param trackingForm
     */
    public void updateTracking(TrackingForm trackingForm) {
        tradeOrderFacade.update(new TradeOrderParam().setTrackingCompany(trackingForm.getTrackingCompany())
                .setTrackingNumber(trackingForm.getTrackingNumber()).setUpdateTime(DateUtil.date()),
                new TradeOrderQuery().setOrderId(trackingForm.getOrderCode()));
    }

    private void createJkyStockIn(OrderBO orderBO, String warehouseNo, Integer quantity) {
        try {
            jkyTemplate.createAndStockIn(builderJkyStockIn(orderBO, warehouseNo, quantity));
        } catch (Exception e) {
            log.error("订单号：{}，创建吉客云入库单失败：{}", orderBO.getOrderCode(), e.getMessage(), e);
        }
    }

    private StockCreateAndStockInParam builderJkyStockIn(OrderBO orderBO, String warehouseNo, Integer quantity) {
        return new StockCreateAndStockInParam().setWarehouseCode(warehouseNo).setGoodsCode(orderBO.getSkuCode()).setQuantity(quantity).setBatchNo(orderBO.getOrderCode());
    }

    private StockInInfoParam builderStockIn(OrderBO orderBO, Integer quantity, String warehouseNo, List<String> snList, String remark) {
        TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setOrderId(orderBO.getOrderCode()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
        String companyName = tradeOrderBO.getTradeNickName();

        StockInInfoGoodsList stockInInfoGoodsList = StockInInfoGoodsList.builder().spec_no(orderBO.getSkuCode()).num(new BigDecimal(quantity))
                .remark(StrUtil.isEmpty(remark) ? "" : remark + "," + String.format("入仓订单号:%s,供应商名称:%s", orderBO.getOrderCode(), companyName)).stockin_price(tradeOrderBO.getTradePrice()).sn_list(snList).build();
        return StockInInfoParam.builder().outer_no(orderBO.getOrderCode()).warehouse_no(warehouseNo)
                .is_check(1).goods_list(Collections.singletonList(stockInInfoGoodsList)).build();
    }

}


