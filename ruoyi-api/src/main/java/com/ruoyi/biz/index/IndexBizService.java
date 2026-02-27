package com.ruoyi.biz.index;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.aliyun.openservices.ons.api.Message;
import com.ruoyi.biz.common.IDictDistrictBizService;
import com.ruoyi.biz.company.CompanyCapitalBizService;
import com.ruoyi.biz.mq.MsgClient;
import com.ruoyi.biz.mq.OrderTradeListener;
import com.ruoyi.capital.facade.ICompanyCapitalFacade;
import com.ruoyi.capital.model.consts.CompanyCapitalConsts;
import com.ruoyi.capital.model.param.CompanyCapitalLogParam;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.Arith;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.config.properties.OnsProperties;
import com.ruoyi.consts.ApiRedisKey;
import com.ruoyi.consts.DictConstants;
import com.ruoyi.mapper.index.IndexConvert;
import com.ruoyi.order.facade.IHangingOrderFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.facade.ITradeOrderFacade;
import com.ruoyi.order.model.bo.*;
import com.ruoyi.order.model.consts.HandingOrderConsts;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.consts.TabConsts;
import com.ruoyi.order.model.consts.TradeOrderConsts;
import com.ruoyi.order.model.param.HangingOrderParam;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.param.TradeOrderParam;
import com.ruoyi.order.model.query.HangingOrderQuery;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.order.model.query.OrderTabCountQuery;
import com.ruoyi.order.model.query.TradeOrderQuery;
import com.ruoyi.system.model.bo.DictDistrictBO;
import com.ruoyi.system.model.consts.DictDataConsts;
import com.ruoyi.system.service.ISysDictTypeService;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.facade.IUserFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.bo.UserBO;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.user.model.query.UserQuery;
import com.ruoyi.web.form.index.ProductForm;
import com.ruoyi.web.form.index.TradeForm;
import com.ruoyi.web.vo.index.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Component
public class IndexBizService {

    @Autowired
    ISysDictTypeService dictTypeService;

    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    RedisCache redisCache;

    @Autowired
    ICompanyFacade companyFacade;

    @Autowired
    IUserFacade userFacade;

    @Autowired
    IHangingOrderFacade hangingOrderFacade;

    @Autowired
    ITradeOrderFacade tradeOrderFacade;

    @Autowired
    IDictDistrictBizService dictDistrictBizService;

    @Autowired
    ICompanyCapitalFacade companyCapitalFacade;

    @Autowired
    CompanyCapitalBizService companyCapitalBizService;

    @Autowired
    MsgClient msgClient;

    @Autowired
    OnsProperties onsProperties;


    String dictLabels = null;


    @PostConstruct
    void init() {
        dictLabels = DictUtils.getDictLabels(DictDataConsts.PLATFORM_OTHER_REQUIRE);
    }

    /**
     * 查询首页tab分类
     *
     * @return
     */
    public List<IndexTabVO> tab() {

        List<IndexTabVO> indexTab = redisCache.getCacheObject(ApiRedisKey.INDEX_TAB);
        if (CollectionUtil.isNotEmpty(indexTab)) {
            return indexTab;
        }
        indexTab = new ArrayList<>();
        for (TabConsts.TabType tabType : TabConsts.TabType.values()) {
            IndexTabVO indexTabVO = new IndexTabVO();
            indexTabVO.setDictLabel(tabType.getTabName());
            indexTabVO.setDictValue(tabType.getTabName());
            OrderQuery query = new OrderQuery().setStatusList(List.of(OrderConsts.OrderStatus.TRADING.getCode()))
                    .setBrand(tabType.getBrand()).setCategoryList(tabType.getCategoryList());
            indexTabVO.setNum(orderFacade.count(query));
            indexTab.add(indexTabVO);
        }
        redisCache.setCacheObject(ApiRedisKey.INDEX_TAB, indexTab, 3, TimeUnit.MINUTES);

        return indexTab;
    }


    /**
     * 查询省下面的市D
     *
     * @param tabName
     * @return
     */
    public List<ProvinceVO> provinceAndCity(String tabName, String productName, String skuName) {
        // 翻译对象
        TabConsts.TabType tabType = TabConsts.TabType.getTabType(tabName);
        //查询缓存
        List<ProvinceVO> provinceVOList = redisCache.getCacheObject(ApiRedisKey.INDEX_PROVINCE + tabName + productName + skuName);
        if (CollectionUtil.isNotEmpty(provinceVOList)) {
            return provinceVOList;
        }
        provinceVOList = new ArrayList<>();
        DateTime dateTime = DateUtil.offsetDay(DateUtil.date(), -15);
        // 查询数据
        List<ProvinceCityCountBO> provinceCityCountBOS = orderFacade.provinceCityCount(new OrderTabCountQuery().setBrand(tabType.getBrand()).setCategoryList(tabType.getCategoryList())
                .setStatusList(List.of(OrderConsts.OrderStatus.TRADING.getCode())).setAddressStatus(OrderConsts.AddressStatus.SUCCESS.getCode()).setGtCreateDateTime(dateTime)
                .setProductName(productName).setSkuName(skuName));
        // 查询所有省
        Map<Long, String> provinceMap = dictDistrictBizService.listProvince().stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
        // 匹配省市
        for (ProvinceCityCountBO provinceCityCountBO : provinceCityCountBOS) {
            Optional<ProvinceVO> provinceVOOptional = provinceVOList.stream().filter(provinceVO -> Objects.equals(provinceCityCountBO.getProvince(), provinceVO.getDistrict())).findFirst();
            // 添加对象
            if (!provinceVOOptional.isPresent()) {
                ProvinceVO provinceVO = new ProvinceVO().setName(provinceMap.get(provinceCityCountBO.getProvince())).setDistrict(provinceCityCountBO.getProvince())
                        .setQuantity(provinceCityCountBO.getCount()).setChild(new ArrayList<>() {{
                            add(new CityVO().setDistrict(provinceCityCountBO.getCity()).setCityName(queryCity(provinceMap, provinceCityCountBO.getProvince(), provinceCityCountBO.getCity())).setQuantity(provinceCityCountBO.getCount()));
                        }});
                provinceVOList.add(provinceVO);
            } else {
                ProvinceVO provinceVO = provinceVOOptional.get();
                provinceVO.getChild().add(new CityVO().setDistrict(provinceCityCountBO.getCity()).setCityName(queryCity(provinceMap, provinceCityCountBO.getProvince(), provinceCityCountBO.getCity())).setQuantity(provinceCityCountBO.getCount()));
                provinceVO.setQuantity(provinceVO.getQuantity() + provinceCityCountBO.getCount());
            }
        }
        provinceVOList = provinceVOList.stream().sorted(Comparator.comparing(ProvinceVO::getQuantity)).collect(Collectors.toList());

        redisCache.setCacheObject(ApiRedisKey.INDEX_PROVINCE + tabName + productName + skuName, provinceVOList, 10, TimeUnit.SECONDS);
        return provinceVOList;
    }

    /**
     * 查询城市名称
     *
     * @param dataMap
     * @param province
     * @param city
     * @return
     */
    private String queryCity(Map<Long, String> dataMap, Long province, Long city) {

        String cityName = dataMap.get(city);
        if (Objects.nonNull(cityName)) {
            return cityName;
        }
        List<DictDistrictBO> dictDistrictBOS = dictDistrictBizService.listCity(province);
        dataMap.putAll(dictDistrictBOS.stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict)));
        return dataMap.get(city);
    }

    /**
     * 查询产品
     *
     * @param productForm
     * @return
     */
    public List<IndexProductVO> productList(ProductForm productForm) {
        // 翻译对象
        TabConsts.TabType tabType = TabConsts.TabType.getTabType(productForm.getTabName());
        //查询缓存
        List<IndexProductVO> productVOList = redisCache.getCacheObject(ApiRedisKey.INDEX_PRODUCT + productForm.getTabName()
                + productForm.getProvince() + productForm.getCity() + productForm.getProductName());
        if (CollectionUtil.isNotEmpty(productVOList)) {
            return productVOList;
        }
        DateTime dateTime = DateUtil.offsetDay(DateUtil.date(), -15);
        // 封装查询参数
        OrderTabCountQuery query = IndexConvert.INSTANCE.toOrderTabCountQuery(productForm);
        query.setAddressStatus(OrderConsts.AddressStatus.SUCCESS.getCode());
        query.setStatusList(List.of(OrderConsts.OrderStatus.TRADING.getCode())).setGtCreateDateTime(dateTime);
        query.setBrand(tabType.getBrand()).setCategoryList(tabType.getCategoryList());
        // 查询数据
        List<ProductBO> productBOS = orderFacade.productCount(query);

        productVOList = IndexConvert.INSTANCE.toIndexProductVO(productBOS);
        productVOList = productVOList.stream().sorted(Comparator.comparing(IndexProductVO::getQuantity).reversed()).collect(Collectors.toList());

        redisCache.setCacheObject(ApiRedisKey.INDEX_PRODUCT + productForm.getTabName()
                + productForm.getProvince() + productForm.getCity() + productForm.getProductName(), productVOList, 10, TimeUnit.SECONDS);
        return productVOList;

    }

    /**
     * 查询sku列表
     *
     * @param productForm
     * @return
     */
    public List<IndexSkuVO> skuCount(ProductForm productForm) {
        // 翻译对象
        TabConsts.TabType tabType = TabConsts.TabType.getTabType(productForm.getTabName());
        //查询缓存
        List<IndexSkuVO> skuVOList = redisCache.getCacheObject(ApiRedisKey.INDEX_SKU + productForm.getTabName()
                + productForm.getProvince() + productForm.getCity() + productForm.getProductName() + productForm.getSkuName());
        if (CollectionUtil.isNotEmpty(skuVOList)) {
            return skuVOList;
        }
        DateTime dateTime = DateUtil.offsetDay(DateUtil.date(), -15);
        // 封装查询参数
        OrderTabCountQuery query = IndexConvert.INSTANCE.toOrderTabCountQuery(productForm);
        query.setAddressStatus(OrderConsts.AddressStatus.SUCCESS.getCode());
        query.setStatusList(List.of(OrderConsts.OrderStatus.TRADING.getCode())).setGtCreateDateTime(dateTime);
        query.setBrand(tabType.getBrand()).setCategoryList(tabType.getCategoryList());
        // 查询数据
        List<SkuBO> productBOS = orderFacade.skuCount(query);

        skuVOList = IndexConvert.INSTANCE.toIndexSkuVO(productBOS);
        skuVOList = skuVOList.stream().sorted(Comparator.comparing(IndexSkuVO::getQuantity).reversed()).collect(Collectors.toList());

        redisCache.setCacheObject(ApiRedisKey.INDEX_PRODUCT + productForm.getTabName()
                + productForm.getProvince() + productForm.getCity() + productForm.getProductName() + productForm.getSkuName(), skuVOList, 10, TimeUnit.SECONDS);
        return skuVOList;

    }

    /**
     * 查询sku列表
     *
     * @param productForm query 条件
     * @param pageParamV2 分页参数
     * @return
     */
    public PageBO<IndexSkuListVO> skuList(ProductForm productForm, PageParamV2 pageParamV2) {
        TabConsts.TabType tabType = TabConsts.TabType.getTabType(productForm.getTabName());
        OrderQuery orderQuery = IndexConvert.INSTANCE.toOrderQuery(productForm).setBrand(tabType.getBrand()).setCategoryList(tabType.getCategoryList());
        orderQuery.setStatusList(List.of(OrderConsts.OrderStatus.TRADING.getCode()));
        orderQuery.setAddressStatus(OrderConsts.AddressStatus.SUCCESS.getCode());
        PageBO<OrderBO> orderBOPageBO = orderFacade.listPage(orderQuery, pageParamV2);

        Map<Long, String> provinceMap = dictDistrictBizService.listProvince().stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
        List<IndexSkuListVO> orderWaitVOList = IndexConvert.INSTANCE.toIndexSkuListVOList(orderBOPageBO.getData());
        for (IndexSkuListVO indexSkuListVO : orderWaitVOList) {
            indexSkuListVO.setOtherRequire((queryOtherRequire(indexSkuListVO.getPlatform())));
            // 组装省市
            indexSkuListVO.setProvinceName(provinceMap.get(indexSkuListVO.getProvince()));
            indexSkuListVO.setCityName(queryCity(provinceMap, indexSkuListVO.getProvince(), indexSkuListVO.getCity()));
            //
            HangingOrderBO hangingOrderBO = hangingOrderFacade.getOne(new HangingOrderQuery().setOrderId(indexSkuListVO.getOrderCode()).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
            if (Objects.isNull(hangingOrderBO)) {
                continue;
            }
            // 设置每次报价时间, 履约时效、价格等等
            indexSkuListVO.setHangingOrderId(hangingOrderBO.getId());
            indexSkuListVO.setQuotationInterval(hangingOrderBO.getQuotationInterval()).setDeliveryTime(hangingOrderBO.getDeliveryTime());
            indexSkuListVO.setAccountingPeriod(hangingOrderBO.getAccountingPeriod());
            indexSkuListVO.setPriceHighest(hangingOrderBO.getPriceHighest()).setPriceHign(hangingOrderBO.getPriceHign()).setPriceLow(hangingOrderBO.getPriceLow()).setPriceLowest(hangingOrderBO.getPriceLowest());
            indexSkuListVO.setPriceHighestStatus(hangingOrderBO.getPriceHighestStatus()).setPriceHignStatus(hangingOrderBO.getPriceHignStatus()).setPriceLowStatus(hangingOrderBO.getPriceLowStatus()).setPriceLowestStatus(hangingOrderBO.getPriceLowestStatus());
            // 判断是否需要倒计时
            List<Integer> codeList = List.of(TradeOrderConsts.TradeStatus.EXPIRED.getCode());
            if (codeList.contains(hangingOrderBO.getPriceHighestStatus()) || codeList.contains(hangingOrderBO.getPriceHignStatus()) || codeList.contains(hangingOrderBO.getPriceLowStatus()) || codeList.contains(hangingOrderBO.getPriceLowestStatus())) {
                List<Integer> statusList = List.of(TradeOrderConsts.TradeStatus.SUCCESS.getCode(), TradeOrderConsts.TradeStatus.EXPIRED.getCode());
                TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setHangOrderId(hangingOrderBO.getId()).setOrderId(indexSkuListVO.getOrderCode()).setStatusList(statusList));
                if (Objects.isNull(tradeOrderBO)) {
                    continue;
                }
                indexSkuListVO.setLastCompeteTime(tradeOrderBO.getCreateTime()).setTradePrice(tradeOrderBO.getTradePrice());
            }
        }

        return new PageBO<>(orderWaitVOList, orderBOPageBO.getTotal());
    }

    /**
     *
     * @param platform
     * @return
     */
    private List<String> queryOtherRequire(String platform) {

        if (platform.contains("拼多多")) {
            List<String> pdd = new ArrayList<>(Arrays.asList(this.dictLabels.split(DictUtils.SEPARATOR)));
            pdd.add("拼多多未销售");
            return pdd;
        }
        if (platform.contains("京东")) {
            List<String> jd = new ArrayList<>(Arrays.asList(this.dictLabels.split(DictUtils.SEPARATOR)));
            jd.add("京东未销售");
            return jd;
        }
        if (platform.contains("快手")) {
            List<String> ks = new ArrayList<>(Arrays.asList(this.dictLabels.split(DictUtils.SEPARATOR)));
            ks.add("快手未销售");
            return ks;
        }
        if (platform.contains("抖店")) {
            List<String> dy = new ArrayList<>(Arrays.asList(this.dictLabels.split(DictUtils.SEPARATOR)));
            dy.add("抖店未销售");
            return dy;
        }
        if (platform.contains("淘宝") || platform.contains("天猫")) {
            List<String> tt = new ArrayList<>(Arrays.asList(this.dictLabels.split(DictUtils.SEPARATOR)));
            tt.add("淘天未销售");
            return tt;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 查询sku详情
     *
     * @param orderId
     * @param hangingId
     */
    public IndexOrderInfoVO skuInfo(String orderId, Long hangingId) {

        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(orderId));
        Assert.notNull(orderBO, "订单信息已过期,请刷新列表");
        Assert.equals(OrderConsts.OrderStatus.TRADING.getCode(), orderBO.getStatus(), "订单信息已过期,请刷新列表");

        HangingOrderBO hangingOrderBO = hangingOrderFacade.getOne(new HangingOrderQuery().setId(hangingId));
        Assert.notNull(hangingOrderBO, "订单信息已过期,请刷新列表");
        Assert.equals(HandingOrderConsts.Status.NORMAL.getCode(), hangingOrderBO.getStatus(), "订单信息已过期,请刷新列表");

        IndexOrderInfoVO indexSkuInfoVO = IndexConvert.INSTANCE.toIndexSkuInfoVO(orderBO, hangingOrderBO);
        indexSkuInfoVO.setOtherRequire((queryOtherRequire(orderBO.getPlatform())));
        indexSkuInfoVO.setHangingOrderId(hangingOrderBO.getId());

        Map<Long, String> provinceMap = dictDistrictBizService.listProvince().stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
        indexSkuInfoVO.setProvinceName(provinceMap.get(orderBO.getProvince()));
        indexSkuInfoVO.setCityName(queryCity(provinceMap, orderBO.getProvince(), orderBO.getCity()));

        // // 判断是否需要倒计时
        List<Integer> codeList = List.of(TradeOrderConsts.TradeStatus.EXPIRED.getCode());
        if (codeList.contains(hangingOrderBO.getPriceHighestStatus()) || codeList.contains(hangingOrderBO.getPriceHignStatus()) || codeList.contains(hangingOrderBO.getPriceLowStatus()) || codeList.contains(hangingOrderBO.getPriceLowestStatus())) {
            List<Integer> statusList = List.of(TradeOrderConsts.TradeStatus.SUCCESS.getCode(), TradeOrderConsts.TradeStatus.EXPIRED.getCode());
            TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setHangOrderId(hangingOrderBO.getId()).setOrderId(indexSkuInfoVO.getOrderCode()).setStatusList(statusList));
            if (Objects.isNull(tradeOrderBO)) {
                return indexSkuInfoVO;
            }
            indexSkuInfoVO.setLastCompeteTime(tradeOrderBO.getCreateTime()).setTradePrice(tradeOrderBO.getTradePrice());
        }
        return indexSkuInfoVO;
    }

    /**
     * 抢单
     * TODO 订单状态判断
     * 判断是否有最新的抢单
     *
     * @param tradeForm
     */
    @Transactional
    public void trade(TradeForm tradeForm, LoginUser loginUser) {

        CompanyBO companyBO = companyFacade.queryOne(new CompanyQuery().setId(loginUser.getDeptId()));
        Assert.notNull(companyBO, "企业信息异常,请联系管理员");

        UserBO userBO = userFacade.queryOne(new UserQuery().setUserId(loginUser.getUserId()));
        Assert.notNull(userBO, "企业信息异常,请联系管理员");

        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(tradeForm.getOrderCode()));
        Assert.notNull(orderBO, "订单信息已过期,请刷新列表");
        Assert.equals(OrderConsts.OrderStatus.TRADING.getCode(), orderBO.getStatus(), "订单信息已过期,请刷新列表");
        // 保存trade信息
        HangingOrderBO hangingOrderBO = hangingOrderFacade.getOne(new HangingOrderQuery().setId(tradeForm.getHangingOrderId()));
        Assert.notNull(hangingOrderBO, "订单信息已过期,请刷新列表");
        Assert.equals(HandingOrderConsts.Status.NORMAL.getCode(), hangingOrderBO.getStatus(), "订单信息已过期,请刷新列表");
        // 验证参数
        preCheck(hangingOrderBO, tradeForm);
        // 计算保证金金额
        BigDecimal bigDecimal = companyCapitalBizService.calAmount(orderBO.getQuantity());
        // 判断是否有未成交的trade
        List<TradeOrderBO> tradeOrderBOList = tradeOrderFacade.list(new TradeOrderQuery().setOrderId(tradeForm.getOrderCode()).setHangOrderId(hangingOrderBO.getId()).setStatus(TradeOrderConsts.TradeStatus.EXPIRED.getCode()));
        if (CollectionUtil.isNotEmpty(tradeOrderBOList)) {
            for (TradeOrderBO tradeOrderBO : tradeOrderBOList) {
                tradeOrderFacade.update(new TradeOrderParam().setStatus(TradeOrderConsts.TradeStatus.TAKEN.getCode()), new TradeOrderQuery().setId(tradeOrderBO.getId()));
                //  退保证金
                companyCapitalFacade.unFreeze(new CompanyCapitalLogParam().setCompanyId(tradeOrderBO.getTradeCompanyId()).setUpdateTime(tradeOrderBO.getUpdateTime()).setAddAmount(bigDecimal)
                        .setOrderNo(orderBO.getOrderCode()).setType(CompanyCapitalConsts.LogTypes.ORDER.getCode()).setTradeId(tradeOrderBO.getId()));
            }
        }
        // 保存tarde
        TradeOrderParam tradeOrderParam = new TradeOrderParam().setOrderId(tradeForm.getOrderCode()).setHangOrderId(tradeForm.getHangingOrderId()).setTradePrice(tradeForm.getTradePrice())
                .setTradeUserId(loginUser.getUserId()).setTradeUserName(userBO.getNickName()).setTradeCompanyId(loginUser.getDeptId())
                .setTradeUserPhone(userBO.getPhone()).setAccountingPeriod(hangingOrderBO.getAccountingPeriod()).setBrand(orderBO.getBrand()).setProductName(orderBO.getProductName())
                .setSkuName(orderBO.getSkuName()).setSkuCode(orderBO.getSkuCode()).setProvince(orderBO.getProvince()).setTradeIndex(tradeForm.getTradeIndex())
                .setTradeNickName(companyBO.getNickName()).setQuantity(orderBO.getQuantity());
        // 如果是价4直接成交
        if (Arith.eq(tradeOrderParam.getTradePrice(), hangingOrderBO.getPriceLowest())) {
            tradeOrderParam.setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode());
        } else {
            tradeOrderParam.setStatus(TradeOrderConsts.TradeStatus.EXPIRED.getCode());
        }
        TradeOrderBO tradeOrderBO = tradeOrderFacade.save(tradeOrderParam);
        // 修改hangingOrder
        HangingOrderParam hangingOrderParam = IndexConvert.INSTANCE.tohangingOrderParam(hangingOrderBO);
        hangingOrderParam.setLastCompeteTime(DateUtil.date()).setLastCompeteUser(loginUser.getUserId()).setLastCompeteCompany(loginUser.getDeptId());
        hangingOrderFacade.update(hangingOrderParam, new HangingOrderQuery().setId(hangingOrderBO.getId()));
        // 修改order
        if (Arith.eq(tradeOrderParam.getTradePrice(), hangingOrderBO.getPriceLowest())) {
            orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.DELIVERY_ING.getCode()).setSubStatus(OrderConsts.OrderSubStatus.WAIT_IMEI.getCode()), new OrderQuery().setOrderCode(tradeForm.getOrderCode()));
        }
        //  扣保证金
        companyCapitalFacade.freeze(new CompanyCapitalLogParam().setCompanyId(companyBO.getId()).setCreateTime(DateUtil.date()).setOutAmount(bigDecimal.negate())
                .setOrderNo(orderBO.getOrderCode()).setType(CompanyCapitalConsts.LogTypes.ORDER.getCode()).setTradeId(tradeOrderBO.getId()));
        //  非价4的订单，发送消息
        if (!Arith.eq(tradeOrderParam.getTradePrice(), hangingOrderBO.getPriceLowest())) {
            Message message = new Message( onsProperties.getOrderTopic(), "trade", tradeOrderBO.getId().toString(), Convert.longToBytes(tradeOrderBO.getId()));
            msgClient.sendMsgDeliverTime(message, TimeUnit.MINUTES, (tradeOrderParam.getTradeIndex() - 1) * hangingOrderBO.getQuotationInterval());
        }

    }

    /**
     * 提前参数验证
     *
     * @param hangingOrderBO
     * @param tradeForm
     */
    private void preCheck(HangingOrderBO hangingOrderBO, TradeForm tradeForm) {
        Boolean flag = false;
        if (Arith.eq(tradeForm.getTradePrice(), hangingOrderBO.getPriceHighest())) {
            flag = true;
            if (!Objects.equals(hangingOrderBO.getPriceHighestStatus(), TradeOrderConsts.TradeStatus.CONFIRMED.getCode())) {
                throw new ServiceException("价格错误，请刷新页面重新抢单", HttpStatus.CONFLICT);
            }
            if (!Objects.equals(hangingOrderBO.getPriceHignStatus(), TradeOrderConsts.TradeStatus.CONFIRMED.getCode())) {
                throw new ServiceException("价格错误，请刷新页面重新抢单", HttpStatus.CONFLICT);
            }
            if (!Objects.equals(hangingOrderBO.getPriceLowStatus(), TradeOrderConsts.TradeStatus.CONFIRMED.getCode())) {
                throw new ServiceException("价格错误，请刷新页面重新抢单", HttpStatus.CONFLICT);
            }
            if (!Objects.equals(hangingOrderBO.getPriceLowestStatus(), TradeOrderConsts.TradeStatus.CONFIRMED.getCode())) {
                throw new ServiceException("价格错误，请刷新页面重新抢单", HttpStatus.CONFLICT);
            }
            hangingOrderBO.setPriceHighestStatus(TradeOrderConsts.TradeStatus.EXPIRED.getCode());
        }
        if (Arith.eq(tradeForm.getTradePrice(), hangingOrderBO.getPriceHign())) {
            flag = true;
            if (!Objects.equals(hangingOrderBO.getPriceHignStatus(), TradeOrderConsts.TradeStatus.CONFIRMED.getCode())) {
                throw new ServiceException("价格错误，请刷新页面重新抢单", HttpStatus.CONFLICT);
            }
            if (!Objects.equals(hangingOrderBO.getPriceLowStatus(), TradeOrderConsts.TradeStatus.CONFIRMED.getCode())) {
                throw new ServiceException("价格错误，请刷新页面重新抢单", HttpStatus.CONFLICT);
            }
            if (!Objects.equals(hangingOrderBO.getPriceLowestStatus(), TradeOrderConsts.TradeStatus.CONFIRMED.getCode())) {
                throw new ServiceException("价格错误，请刷新页面重新抢单", HttpStatus.CONFLICT);
            }
            hangingOrderBO.setPriceHighestStatus(TradeOrderConsts.TradeStatus.TAKEN.getCode());
            hangingOrderBO.setPriceHignStatus(TradeOrderConsts.TradeStatus.EXPIRED.getCode());
        }
        if (Arith.eq(tradeForm.getTradePrice(), hangingOrderBO.getPriceLow())) {
            flag = true;
            if (!Objects.equals(hangingOrderBO.getPriceLowStatus(), TradeOrderConsts.TradeStatus.CONFIRMED.getCode())) {
                throw new ServiceException("价格错误，请刷新页面重新抢单", HttpStatus.CONFLICT);
            }
            if (!Objects.equals(hangingOrderBO.getPriceLowestStatus(), TradeOrderConsts.TradeStatus.CONFIRMED.getCode())) {
                throw new ServiceException("价格错误，请刷新页面重新抢单", HttpStatus.CONFLICT);
            }
            hangingOrderBO.setPriceHighestStatus(TradeOrderConsts.TradeStatus.TAKEN.getCode());
            hangingOrderBO.setPriceHignStatus(TradeOrderConsts.TradeStatus.TAKEN.getCode());
            hangingOrderBO.setPriceLowStatus(TradeOrderConsts.TradeStatus.EXPIRED.getCode());
        }
        if (Arith.eq(tradeForm.getTradePrice(), hangingOrderBO.getPriceLowest())) {
            flag = true;
            if (!Objects.equals(hangingOrderBO.getPriceLowestStatus(), TradeOrderConsts.TradeStatus.CONFIRMED.getCode())) {
                throw new ServiceException("价格错误，请刷新页面重新抢单", HttpStatus.CONFLICT);
            }
            hangingOrderBO.setPriceHighestStatus(TradeOrderConsts.TradeStatus.TAKEN.getCode());
            hangingOrderBO.setPriceHignStatus(TradeOrderConsts.TradeStatus.TAKEN.getCode());
            hangingOrderBO.setPriceLowStatus(TradeOrderConsts.TradeStatus.TAKEN.getCode());
            hangingOrderBO.setPriceLowestStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode());
        }
        if (!flag) {
            throw new ServiceException("价格错误，请刷新页面重新抢单", HttpStatus.CONFLICT);
        }
    }


}
