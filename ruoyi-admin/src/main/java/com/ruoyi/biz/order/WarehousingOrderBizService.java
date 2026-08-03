package com.ruoyi.biz.order;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.master.constant.MasterSubjectBankConsts;
import com.ruoyi.master.facade.IMasterSubjectBankFacade;
import com.ruoyi.master.model.bo.MasterSubjectBankBO;
import com.ruoyi.master.model.query.MasterSubjectBankQuery;
import com.ruoyi.biz.bill.BillBizService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.Arith;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.express.model.consts.LogisticsCode;
import com.ruoyi.jky.JkyTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.jky.model.JkyResponse;
import com.ruoyi.jky.param.stock.StockCreateAndStockInParam;
import com.ruoyi.jky.rep.stock.StockCreateAndStockInRep;
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

import com.alibaba.excel.EasyExcel;
import com.ruoyi.web.form.order.WarehousingImportResult;
import com.ruoyi.web.form.order.WarehousingImportRowResult;
import com.ruoyi.web.form.order.WarehousingImportVO;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
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
    JkyTemplate jkyTemplate;

    @Autowired
    IMasterSubjectBankFacade payerFacade;

    @Autowired
    RuoYiConfig ruoYiConfig;

    protected Integer getOrderType() {
        return OrderConsts.OrderType.PROCUREMENT.getCode();
    }

    /**
     * 入仓订单列表
     *
     * @param warehousingOrderParam
     * @param pageParamV2
     * @return
     */
    public PageBO<WarehousingOrderVO> orderList(WarehousingOrderParam warehousingOrderParam, PageParamV2 pageParamV2) {
        DateTime dateTime = DateUtil.offsetDay(DateUtil.date(), -15);
        OrderQuery orderQuery = new OrderQuery().setProductName(warehousingOrderParam.getProductName()).setStatusList(warehousingOrderParam.getStatusList())
                .setDeliveryCode(warehousingOrderParam.getDeliveryCode())
                .setOrderTypeList(warehousingOrderParam.getOrderTypeList())
                .setOrderType(CollectionUtil.isEmpty(warehousingOrderParam.getOrderTypeList()) ? getOrderType() : null)
                .setCompanyId(warehousingOrderParam.getCompanyId()).setBrand(warehousingOrderParam.getBrand()).setCreateTime(dateTime)
                .setPayerName(warehousingOrderParam.getPayerName()).setCreateBy(warehousingOrderParam.getCreateBy());
        PageBO<CompanyOrderBO> companyOrderBOPageBO = orderFacade.companyListPage(orderQuery, pageParamV2);
        List<WarehousingOrderVO> vo = WarehousingConvert.INSTANCE.toVO(companyOrderBOPageBO.getData());
        Set<Long> userIds = vo.stream().map(e -> Convert.toLong(e.getCreateBy())).collect(Collectors.toSet());
        List<SysUser> userBOS = sysUserFacade.selectUserByIds(userIds);
        Map<Long, String> userBOMap = userBOS.stream().collect(Collectors.toMap(userBO -> userBO.getUserId(), userBO -> userBO.getNickName()));
        for (WarehousingOrderVO warehousingOrderVO : vo) {
            Long userId = Convert.toLong(warehousingOrderVO.getCreateBy());
            String nickName = userBOMap.get(userId);
            warehousingOrderVO.setCreateBy(nickName);
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
        MasterSubjectBankBO payerBO = payerFacade.getOne(new MasterSubjectBankQuery().setId(warehousingSaveParam.getPayerId()).setActived(MasterSubjectBankConsts.Activated.ACTIVATED.getCode()));
        Assert.notNull(payerBO, "付款主体不存在或已弃用");


        String orderERPCode = IdUtil.objectId();
        DateTime now = DateUtil.date();
        // 构建订单
        OrderParam orderParam = new OrderParam().setErpOrderId(orderERPCode).setOriginalOrderId(orderERPCode).setOrderType(getOrderType())
                .setShopName(OrderConsts.SHOP_NAME).setPlatform(OrderConsts.PLATFORM).setBrand(productSkuBO.getBrand()).setCategory(productSkuBO.getCategory())
                .setProductName(productSkuBO.getProductName()).setSkuName(productSkuBO.getSpecName()).setSkuCode(productSkuBO.getSkuCode()).setQuantity(warehousingSaveParam.getQuantity())
                .setPayerId(payerBO.getId()).setPayerName(payerBO.getNickName())
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
        TradeOrderParam tradeOrderParam = new TradeOrderParam().setOrderType(getOrderType()).setTradeCompanyId(warehousingSaveParam.getCompanyId());
        tradeOrderParam.setTradeUserId(memberBO.getUserId()).setTradeUserPhone(memberBO.getPhone()).setTradeUserName(memberBO.getNickName()).setAccountingPeriod(warehousingSaveParam.getAccountingPeriod());
        tradeOrderParam.setTradeCompanyId(companyBO.getId()).setTradeCompanyName(companyBO.getCompanyName()).setTradeNickName(companyBO.getNickName()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode());
        tradeOrderParam.setUpdateTime(DateUtil.date()).setUpdateBy(loginUser.getUserId()).setDeliveryCode(RandomUtil.randomInt(100000, 1000000));
        tradeOrderParam.setOrderId(orderBO.getOrderCode()).setTradePrice(warehousingSaveParam.getPrice()).setBrand(orderBO.getBrand()).setProductName(orderBO.getProductName());
        tradeOrderParam.setSkuName(orderBO.getSkuName()).setSkuCode(orderBO.getSkuCode()).setProvince(orderBO.getProvince()).setQuantity(0);
        tradeOrderParam.setOrderType(orderBO.getOrderType()).setTradeIndex(4).setHangOrderId(hangingOrderBO.getId()).setOrderId(orderBO.getOrderCode());
        tradeOrderParam.setTrackingNumber(warehousingSaveParam.getTrackingNumber());
        tradeOrderParam.setTrackingCompany(StrUtil.blankToDefault(warehousingSaveParam.getTrackingCompany(), LogisticsCode.ZS.getMsg()));
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
                .setCreateTimeStart(dateTime).setOrderType(getOrderType()));

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
        OrderBO orderBO = queryPickingOrder(pickingOrderForm.getOrderCode());
        HangingOrderBO hangingOrderBO = queryPickingHangingOrder(pickingOrderForm.getOrderCode());
        TradeOrderBO tradeOrderBO = queryPickingTradeOrder(pickingOrderForm.getOrderCode());
        boolean skipJkyAndSettlement = shouldSkipJkyAndSettlement(pickingOrderForm);
        log.info("开始拣货,{},操作用户：{}, 拣货信息：{}", pickingOrderForm.getOrderCode(), loginUser.getUsername(), JacksonUtil.toJson(pickingOrderForm));

        Integer quantity = Objects.isNull(pickingOrderForm.getQuantity())
                ? pickingWithSn(pickingOrderForm, orderBO, hangingOrderBO, tradeOrderBO, loginUser, skipJkyAndSettlement)
                : pickingWithoutSn(pickingOrderForm, orderBO, hangingOrderBO, tradeOrderBO, loginUser, skipJkyAndSettlement);
        finishPickingOrder(pickingOrderForm.getOrderCode(), orderBO, quantity, skipJkyAndSettlement);
    }

    /**
     * 判断批量入库是否需要跳过吉客云及结算处理。
     */
    private boolean shouldSkipJkyAndSettlement(PickingOrderForm pickingOrderForm) {
        return Boolean.TRUE.equals(pickingOrderForm.getBatchInbound())
                && StrUtil.equals(pickingOrderForm.getWarehouseCode(), ruoYiConfig.getInternalWarehouseCode());
    }


    /**
     * 查询拣货订单。
     */
    private OrderBO queryPickingOrder(String orderCode) {
        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(orderCode));
        Assert.notNull(orderBO, "订单不存在");
        return orderBO;
    }

    /**
     * 查询拣货挂单。
     */
    private HangingOrderBO queryPickingHangingOrder(String orderCode) {
        HangingOrderBO hangingOrderBO = hangingOrderFacade.getOne(new HangingOrderQuery().setOrderId(orderCode).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
        Assert.notNull(hangingOrderBO, "订单挂单不存在");
        return hangingOrderBO;
    }

    /**
     * 查询拣货成交记录。
     */
    private TradeOrderBO queryPickingTradeOrder(String orderCode) {
        TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setOrderId(orderCode).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
        Assert.notNull(tradeOrderBO, "订单成交记录不存在");
        return tradeOrderBO;
    }

    /**
     * 处理 SN 管理商品拣货入仓。
     */
    private Integer pickingWithSn(PickingOrderForm pickingOrderForm, OrderBO orderBO, HangingOrderBO hangingOrderBO, TradeOrderBO tradeOrderBO, LoginUser loginUser, boolean skipJkyAndSettlement) throws IOException {
        Integer quantity = pickingOrderForm.getSnList().size() + tradeOrderBO.getQuantity();
        Assert.isFalse(quantity > orderBO.getQuantity(), "订单入库数量大于成交数量");
        savePickingSn(pickingOrderForm, orderBO, hangingOrderBO, tradeOrderBO);
        if (!skipJkyAndSettlement) {
            createJkyStockIn(orderBO, hangingOrderBO, tradeOrderBO, loginUser, pickingOrderForm.getWarehouseCode(), pickingOrderForm.getSnList().size(), pickingOrderForm.getSnList(), pickingOrderForm.getRemark());
        }
        updatePickingQuantity(pickingOrderForm.getOrderCode(), quantity);
        return quantity;
    }

    /**
     * 处理非 SN 管理商品拣货入仓。
     */
    private Integer pickingWithoutSn(PickingOrderForm pickingOrderForm, OrderBO orderBO, HangingOrderBO hangingOrderBO, TradeOrderBO tradeOrderBO, LoginUser loginUser, boolean skipJkyAndSettlement) throws IOException {
        Integer quantity = pickingOrderForm.getQuantity() + tradeOrderBO.getQuantity();
        Assert.isFalse(quantity > orderBO.getQuantity(), "订单入库数量大于成交数量");
        // 已切换至吉客云，原 createStockIn 调用已删除
        if (!skipJkyAndSettlement) {
            createJkyStockIn(orderBO, hangingOrderBO, tradeOrderBO, loginUser, pickingOrderForm.getWarehouseCode(), pickingOrderForm.getQuantity(), pickingOrderForm.getSnList(), pickingOrderForm.getRemark());
        }
        updatePickingQuantity(pickingOrderForm.getOrderCode(), quantity);
        return quantity;
    }

    /**
     * 保存拣货串码。
     */
    private void savePickingSn(PickingOrderForm pickingOrderForm, OrderBO orderBO, HangingOrderBO hangingOrderBO, TradeOrderBO tradeOrderBO) {
        DateTime createTime = DateUtil.date();
        List<ImeiParam> imeiParams = pickingOrderForm.getSnList().stream().map(sn -> new ImeiParam().setCreateTime(createTime).setProductName(orderBO.getProductName())
                .setSkuName(orderBO.getSkuName()).setSkuCode(orderBO.getSkuCode()).setHangingOrderId(hangingOrderBO.getId()).setTradeNo(tradeOrderBO.getId()).setImel(sn)).collect(Collectors.toList());
        imeiFacade.saveBatch(imeiParams);
    }

    /**
     * 更新拣货入仓数量。
     */
    private void updatePickingQuantity(String orderCode, Integer quantity) {
        tradeOrderFacade.update(new TradeOrderParam().setQuantity(quantity).setUpdateTime(DateUtil.date()), new TradeOrderQuery().setOrderId(orderCode));
    }

    /**
     * 完成全部拣货并生成账单。
     */
    private void finishPickingOrder(String orderCode, OrderBO orderBO, Integer quantity, boolean skipJkyAndSettlement) {
        if (!Objects.equals(quantity, orderBO.getQuantity())) {
            return;
        }
        DateTime date = DateUtil.date();
        orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.ENDING.getCode()).setUpdateTime(date).setSignedTime(date), new OrderQuery().setOrderCode(orderCode));
        if (!skipJkyAndSettlement) {
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

    private void createJkyStockIn(OrderBO orderBO, HangingOrderBO hangingOrderBO, TradeOrderBO tradeOrderBO, LoginUser loginUser, String warehouseNo, Integer quantity, List<String> snList, String remark) {
        try {
            StockCreateAndStockInParam param = builderJkyStockIn(orderBO, hangingOrderBO, tradeOrderBO, loginUser, warehouseNo, quantity, snList, remark);
            log.info("订单号：{}，拣货入仓触发吉客云入库(admin:warehousing)", orderBO.getOrderCode());
            JkyResponse<StockCreateAndStockInRep> response = jkyTemplate.createAndStockIn(param);
            if (response == null || !Objects.equals(response.getCode(), 200)) {
                throw new ServiceException(StrUtil.blankToDefault(response == null ? null : response.getMsg(), "吉客云创建库存并入库失败"));
            }
            StockCreateAndStockInRep data = response.getResult() == null ? null : response.getResult().getData();
            if (data == null || StrUtil.isBlank(data.getInNo())) {
                throw new ServiceException("吉客云创建库存并入库返回结果为空");
            }
        } catch (Exception e) {
            log.error("订单号：{}，创建吉客云入库单失败：{}", orderBO.getOrderCode(), e.getMessage(), e);
        }
    }

    private StockCreateAndStockInParam builderJkyStockIn(OrderBO orderBO, HangingOrderBO hangingOrderBO, TradeOrderBO tradeOrderBO, LoginUser loginUser, String warehouseNo, Integer quantity, List<String> snList, String remark) {
        ProductSkuBO productSkuBO = productSkuFacade.getOne(new ProductSkuQuery().setSkuCode(orderBO.getSkuCode()));
        Assert.notNull(productSkuBO, "吉客云入库商品不存在");
        MasterSubjectBankBO payerBO = payerFacade.getOne(new MasterSubjectBankQuery().setId(orderBO.getPayerId()).setActived(MasterSubjectBankConsts.Activated.ACTIVATED.getCode()));
        Assert.notNull(payerBO, "吉客云入库付款主体不存在或已弃用");
        Assert.notBlank(payerBO.getOutCode(), "吉客云入库付款主体吉客云编号不能为空");
        CompanyBO companyBO = companyFacade.queryOne(new CompanyQuery().setId(tradeOrderBO.getTradeCompanyId()));
        Assert.notNull(companyBO, "吉客云入库供应商不存在");
        Assert.notBlank(companyBO.getOutNo(), "吉客云入库供应商编码不能为空");
        Assert.notBlank(loginUser.getUsername(), "吉客云入库申请人不能为空");
        Assert.notNull(hangingOrderBO.getCreateBy(), "吉客云入库制单人不能为空");
        String operator = queryJkyStockInOperator(hangingOrderBO.getCreateBy());
        Assert.notBlank(warehouseNo, "吉客云入库仓库编码不能为空");
        Assert.notBlank(orderBO.getOrderCode(), "吉客云入库关联单据编号不能为空");
        String logisticNo = StrUtil.blankToDefault(tradeOrderBO.getTrackingNumber(), "123");
        Assert.notNull(quantity, "吉客云入库数量不能为空");
        Assert.isTrue(quantity > 0, "吉客云入库数量必须大于0");

        StockCreateAndStockInParam.StockInDetailView detail = new StockCreateAndStockInParam.StockInDetailView()
                .setSkuBarcode(productSkuBO.getSkuCode()).setRelDetailId(tradeOrderBO.getId()).setSkuCount(new BigDecimal(quantity))
                .setSkuPrice(tradeOrderBO.getTradePrice()).setIsCertified(1).setRowRemark(remark);
        if (CollectionUtil.isNotEmpty(snList)) {
            detail.setSerialList(snList.stream().map(sn -> new StockCreateAndStockInParam.Serial().setSerialNo(sn)).collect(Collectors.toList()));
        }
        return new StockCreateAndStockInParam().setVendCode(companyBO.getOutNo()).setApplyDepartCode(JkyTemplate.STOCK_IN_APPLY_DEPART_CODE).setApplyCompanyCode(payerBO.getOutCode())
                .setInWarehouseCode(warehouseNo).setRelDataId(orderBO.getOrderCode()).setApplyUserName(loginUser.getUser().getNickName())
                .setApplyDate(DateUtil.date()).setMemo(remark).setOperator(operator)
                .setLogisticName(tradeOrderBO.getTrackingCompany()).setLogisticNo(logisticNo).setStockInDetailViews(Collections.singletonList(detail));
    }

    private String queryJkyStockInOperator(Long createBy) {
        SysUser sysUser = sysUserFacade.selectUserById(createBy);
        Assert.notNull(sysUser, "吉客云入库制单人不存在");
        return StrUtil.blankToDefault(sysUser.getNickName(), sysUser.getUserName());
    }

    // 已切换至吉客云，原 createStockIn / builderStockIn 方法已删除

    /**
     * 下载导入模板
     */
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("入仓订单导入模板", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), WarehousingImportVO.class).sheet("模板").doWrite(new ArrayList<>());
    }

    /**
     * 校验导入文件，逐行检查数据合法性
     */
    @SuppressWarnings("unchecked")
    public WarehousingImportResult importValidate(MultipartFile file) {
        List<WarehousingImportVO> list;
        try {
            list = (List<WarehousingImportVO>) (List<?>) EasyExcel.read(file.getInputStream())
                    .head(WarehousingImportVO.class).sheet().doReadSync();
        } catch (IOException e) {
            log.error("读取导入文件失败", e);
            throw new ServiceException("读取文件失败");
        }
        List<WarehousingImportRowResult> rowResults = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            rowResults.add(validateRow(list.get(i), i));
        }
        WarehousingImportResult result = new WarehousingImportResult();
        result.setTotalCount(list.size());
        result.setSuccessCount((int) rowResults.stream().filter(WarehousingImportRowResult::getSuccess).count());
        result.setErrorCount((int) rowResults.stream().filter(r -> !r.getSuccess()).count());
        result.setRows(rowResults);
        return result;
    }

    /**
     * 逐行校验并转换
     */
    private WarehousingImportRowResult validateRow(WarehousingImportVO row, int index) {
        Integer accountingPeriod = parseAccountingPeriod(row.getAccountingPeriod());
        WarehousingImportRowResult r = new WarehousingImportRowResult()
                .setRowIndex(index + 1).setSkuCode(row.getSkuCode()).setCompanyName(row.getCompanyName())
                .setQuantity(row.getQuantity()).setPrice(row.getPrice())
                .setAccountingPeriod(accountingPeriod).setPayerName(row.getPayerName())
                .setTrackingNumber(row.getTrackingNumber()).setRemark(row.getRemark());
        List<String> errors = new ArrayList<>();

        if (StrUtil.isBlank(row.getSkuCode())) {
            errors.add("SKU编码不能为空");
        } else {
            ProductSkuBO sku = productSkuFacade.getOne(new ProductSkuQuery().setSkuCode(row.getSkuCode()));
            if (sku == null) {
                errors.add("SKU编码不存在");
            } else {
                r.setProductName(sku.getProductName()).setSpecName(sku.getSpecName());
            }
        }
        if (StrUtil.isBlank(row.getCompanyName())) {
            errors.add("供应商名称不能为空");
        } else if (companyFacade.queryOne(new CompanyQuery().setCompanyName(row.getCompanyName())) == null) {
            errors.add("供应商名称不存在");
        }
        if (StrUtil.isBlank(row.getPayerName())) {
            errors.add("付款主体名称不能为空");
        } else if (CollectionUtil.isEmpty(payerFacade.list(new MasterSubjectBankQuery().setPayName(row.getPayerName()).setActived(MasterSubjectBankConsts.Activated.ACTIVATED.getCode()), null))) {
            errors.add("付款主体名称不存在或已弃用");
        }
        if (row.getQuantity() == null || row.getQuantity() <= 0) {
            errors.add("数量必须大于0");
        }
        if (row.getPrice() == null || row.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("单价不能为空且不能小于0");
        }
        if (accountingPeriod == null || accountingPeriod < 0) {
            errors.add("账期格式不正确，请填写非负整数或T+天数");
        }

        if (errors.isEmpty()) {
            r.setSuccess(true);
        } else {
            r.setSuccess(false);
            r.setErrorMessage(String.join("; ", errors));
        }
        return r;
    }

    /**
     * 导入并创建入仓订单（先校验后导入，校验通过的行整体导入）
     */
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult importFromExcel(MultipartFile file, LoginUser loginUser) {
        List<WarehousingImportVO> list;
        try {
            list = (List<WarehousingImportVO>) (List<?>) EasyExcel.read(file.getInputStream())
                    .head(WarehousingImportVO.class).sheet().doReadSync();
        } catch (IOException e) {
            log.error("读取导入文件失败", e);
            return AjaxResult.error("读取文件失败");
        }
        int successCount = 0;
        for (int i = 0; i < list.size(); i++) {
            WarehousingImportVO row = list.get(i);
            WarehousingSaveParam param = convertToSaveParam(row);
            save(param, loginUser);
            successCount++;
        }
        return AjaxResult.success("导入成功" + successCount + "条");
    }

    /**
     * 将导入行转换为保存参数
     */
    private WarehousingSaveParam convertToSaveParam(WarehousingImportVO row) {
        CompanyBO companyBO = companyFacade.queryOne(new CompanyQuery().setCompanyName(row.getCompanyName()));
        List<MasterSubjectBankBO> payerList = payerFacade.list(new MasterSubjectBankQuery().setPayName(row.getPayerName()).setActived(MasterSubjectBankConsts.Activated.ACTIVATED.getCode()), null);
        MasterSubjectBankBO payerBO = payerList.get(0);
        WarehousingSaveParam param = new WarehousingSaveParam();
        param.setSkuCode(row.getSkuCode());
        param.setQuantity(row.getQuantity());
        param.setPrice(row.getPrice());
        param.setCompanyId(companyBO.getId());
        Integer accountingPeriod = parseAccountingPeriod(row.getAccountingPeriod());
        if (accountingPeriod == null || accountingPeriod < 0) {
            throw new ServiceException("账期格式不正确，请填写非负整数或T+天数");
        }
        param.setAccountingPeriod(accountingPeriod);
        param.setPayerId(payerBO.getId());
        param.setTrackingNumber(StrUtil.trim(row.getTrackingNumber()));
        param.setTrackingCompany(StrUtil.isBlank(row.getTrackingNumber())
                ? LogisticsCode.ZS.getMsg() : LogisticsCode.SHUNFENG.getMsg());
        param.setRemark(row.getRemark());
        return param;
    }

    /**
     * 将导入的账期规范为天数，兼容数字与 T+天数写法。
     */
    private Integer parseAccountingPeriod(String value) {
        String normalizedValue = StrUtil.trim(value);
        if (StrUtil.isBlank(normalizedValue)) {
            return null;
        }
        if (normalizedValue.matches("(?i)t\\+\\d+")) {
            normalizedValue = normalizedValue.substring(normalizedValue.indexOf('+') + 1);
        }
        if (!normalizedValue.matches("\\d+")) {
            return null;
        }
        try {
            return Integer.valueOf(normalizedValue);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
