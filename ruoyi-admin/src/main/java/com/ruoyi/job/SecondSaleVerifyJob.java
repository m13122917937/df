package com.ruoyi.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.order.facade.IImeiFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.model.bo.ImeiBO;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.consts.ImeiConsts;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.param.ImeiParam;
import com.ruoyi.order.model.query.ImeiQuery;
import com.ruoyi.order.model.query.OrderQuery;
import com.taobao.api.ApiException;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAscpLogisticsIdentcodeQueryRequest;
import com.taobao.api.response.AlibabaAscpLogisticsIdentcodeQueryResponse;
import com.taobao.api.response.AlibabaAscpLogisticsIdentcodeQueryResponse.ResultDTO;
import com.taobao.api.response.AlibabaAscpLogisticsIdentcodeQueryResponse.TopIdentCodeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 二销验证状态同步定时任务
 * <p>扫描所有「淘宝」平台、订单状态为「发货中」、子状态为「平台二销验证中」的订单，
 * 调用淘宝 alibaba.ascp.logistics.identcode.query 接口批量查询串码二销状态。</p>
 *
 * @author ruoyi
 */
@Slf4j
@Component("secondSaleVerifyJob")
public class SecondSaleVerifyJob {

    /**
     * 平台名：淘宝（与 OrderBO#platform 库内值一致）
     */
    private static final String PLATFORM_TAOBAO = "淘宝";

    /**
     * 标识码类型：SN
     */
    private static final String IDENT_TYPE_SN = "SN";

    /**
     * 标识码类型：IMEI
     */
    private static final String IDENT_TYPE_IMEI = "IMEI";

    /**
     * 单次查询的最大串码数量（淘宝接口限制按批拉取）
     */
    private static final int BATCH_SIZE = 50;

    /**
     * 订单 category（中文品类名）→ 淘宝 root_cat_id
     */
    private static final Map<String, String> CATEGORY_TO_ROOT_CAT_ID = new HashMap<>();

    /**
     * 订单 brand（中文品牌名/含「/」的中英文）→ 淘宝 brand_id
     * <p>匹配规则：先精确匹配，未命中则按「订单 brand 包含某个映射 key」做兜底匹配。</p>
     */
    private static final Map<String, String> BRAND_TO_BRAND_ID = new HashMap<>();

    static {
        CATEGORY_TO_ROOT_CAT_ID.put("笔记本电脑", "1101");
        CATEGORY_TO_ROOT_CAT_ID.put("笔记本", "1101");
        CATEGORY_TO_ROOT_CAT_ID.put("手机", "1512");
        CATEGORY_TO_ROOT_CAT_ID.put("手机类目", "1512");
        CATEGORY_TO_ROOT_CAT_ID.put("平板电脑", "50019780");
        CATEGORY_TO_ROOT_CAT_ID.put("平板", "50019780");

        BRAND_TO_BRAND_ID.put("Lenovo/联想", "11119");
        BRAND_TO_BRAND_ID.put("联想", "11119");
        BRAND_TO_BRAND_ID.put("Asus/华硕", "11656");
        BRAND_TO_BRAND_ID.put("华硕", "11656");
        BRAND_TO_BRAND_ID.put("Huawei/华为", "11813");
        BRAND_TO_BRAND_ID.put("华为", "11813");
        BRAND_TO_BRAND_ID.put("Hasee/神舟", "21660");
        BRAND_TO_BRAND_ID.put("神舟", "21660");
        BRAND_TO_BRAND_ID.put("MSI/微星", "21999");
        BRAND_TO_BRAND_ID.put("微星", "21999");
        BRAND_TO_BRAND_ID.put("Dell/戴尔", "26683");
        BRAND_TO_BRAND_ID.put("戴尔", "26683");
        BRAND_TO_BRAND_ID.put("Acer/宏碁", "26691");
        BRAND_TO_BRAND_ID.put("宏碁", "26691");
        BRAND_TO_BRAND_ID.put("OPPO", "28247");
        BRAND_TO_BRAND_ID.put("Apple/苹果", "30111");
        BRAND_TO_BRAND_ID.put("苹果", "30111");
        BRAND_TO_BRAND_ID.put("HP/惠普", "31140");
        BRAND_TO_BRAND_ID.put("惠普", "31140");
        BRAND_TO_BRAND_ID.put("vivo", "91621");
        BRAND_TO_BRAND_ID.put("MIUI/小米", "3506680");
        BRAND_TO_BRAND_ID.put("小米", "3506680");
        BRAND_TO_BRAND_ID.put("ThinkPad", "184048021");
        BRAND_TO_BRAND_ID.put("honor/荣耀", "590022244");
        BRAND_TO_BRAND_ID.put("荣耀", "590022244");
        BRAND_TO_BRAND_ID.put("OnePlus/一加", "600184882");
        BRAND_TO_BRAND_ID.put("一加", "600184882");
        BRAND_TO_BRAND_ID.put("MACHENIKE", "616784001");
        BRAND_TO_BRAND_ID.put("机械师", "616784001");
        BRAND_TO_BRAND_ID.put("THUNDEROBOT", "639188075");
        BRAND_TO_BRAND_ID.put("雷神", "639188075");
        BRAND_TO_BRAND_ID.put("MECHREVO/机械革命", "775486237");
        BRAND_TO_BRAND_ID.put("机械革命", "775486237");
        BRAND_TO_BRAND_ID.put("ROG/玩家国度", "1880225553");
        BRAND_TO_BRAND_ID.put("玩家国度", "1880225553");
        BRAND_TO_BRAND_ID.put("iQOO(数码)", "7051840193");
        BRAND_TO_BRAND_ID.put("iQOO", "7051840193");
    }

    @Autowired
    private IOrderFacade orderFacade;

    @Autowired
    private IImeiFacade imeiFacade;

    @Autowired
    private TaobaoClient taobaoClient;

    /**
     * 淘宝授权 sessionKey（店铺访问令牌）。
     * 联调期使用配置中的默认值，生产环境应按店铺动态获取。
     */
    @Value("${fy.taobao.default-session-key:}")
    private String defaultSessionKey;

    /**
     * 每 5 分钟由 Quartz 触发执行一次（cron: 0 0/5 * * * ?）
     * <p>命中条件：</p>
     * <ul>
     *   <li>订单 platform = 淘宝</li>
     *   <li>订单 status = {@link com.ruoyi.order.model.consts.OrderConsts.OrderStatus#DELIVERY_ING}</li>
     *   <li>订单 sub_status ∈ {WAIT_SALES, EXPRESS_WAIT_SALES}</li>
     * </ul>
     */
    public void execute() {
        log.info("开始执行二销验证状态同步定时任务");
        List<Integer> subStatusList = Arrays.asList(
                OrderConsts.OrderSubStatus.WAIT_SALES.getCode(),
                OrderConsts.OrderSubStatus.EXPRESS_WAIT_SALES.getCode());
        int page = 1;
        for (; ; page++) {
            PageParamV2 pageParamV2 = new PageParamV2(page, 100);
            PageBO<OrderBO> pageBO = orderFacade.listPage(
                    new OrderQuery()
                            .setPlatform(PLATFORM_TAOBAO)
                            .setStatus(OrderConsts.OrderStatus.DELIVERY_ING.getCode())
                            .setSubStatusList(subStatusList),
                    pageParamV2);
            if (CollectionUtil.isEmpty(pageBO.getData())) {
                break;
            }
            for (OrderBO orderBO : pageBO.getData()) {
                try {
                    verifyOrder(orderBO);
                } catch (Exception e) {
                    log.error("订单二销验证失败，orderCode={}", orderBO.getOrderCode(), e);
                }
            }
        }
        log.info("结束执行二销验证状态同步定时任务");
    }

    /**
     * 单个订单的二销验证：拉取待校验串码，按 SN/IMEI 分组批量查询，按返回结果更新串码状态。
     *
     * @param orderBO 待验证订单
     */
    private void verifyOrder(OrderBO orderBO) {
        List<ImeiBO> imeiBOS = imeiFacade.list(new ImeiQuery()
                .setOrderId(orderBO.getOrderCode())
                .setPlatformImei(ImeiConsts.PlatformImei.WAIT_QUERY.getCode())
                .setActivated(ImeiConsts.Activated.SUCCESS.getCode()));
        if (CollectionUtil.isEmpty(imeiBOS)) {
            return;
        }
        Map<String, ImeiBO> snToImei = new HashMap<>();
        Map<String, ImeiBO> imeiToImei = new HashMap<>();
        for (ImeiBO imeiBO : imeiBOS) {
            if (StrUtil.isNotBlank(imeiBO.getSn())) {
                snToImei.put(imeiBO.getSn(), imeiBO);
            } else if (StrUtil.isNotBlank(imeiBO.getImel())) {
                imeiToImei.put(imeiBO.getImel(), imeiBO);
            }
        }
        queryAndUpdate(orderBO, snToImei, IDENT_TYPE_SN);
        queryAndUpdate(orderBO, imeiToImei, IDENT_TYPE_IMEI);
    }

    /**
     * 按串码类型批量调用淘宝接口并落库。
     *
     * @param orderBO     订单
     * @param codeToImei  串码值 → 串码记录映射
     * @param identType   标识码类型 SN / IMEI
     */
    private void queryAndUpdate(OrderBO orderBO, Map<String, ImeiBO> codeToImei, String identType) {
        if (codeToImei.isEmpty()) {
            return;
        }
        String rootCatId = resolveRootCatId(orderBO.getCategory());
        String brandId = resolveBrandId(orderBO.getBrand());
        if (StrUtil.isBlank(rootCatId) || StrUtil.isBlank(brandId)) {
            log.warn("淘宝二销验证跳过：未匹配到 rootCatId/brandId，orderCode={}, category={}, brand={}",
                    orderBO.getOrderCode(), orderBO.getCategory(), orderBO.getBrand());
            return;
        }
        List<String> codes = new ArrayList<>(codeToImei.keySet());
        for (int from = 0; from < codes.size(); from += BATCH_SIZE) {
            int to = Math.min(from + BATCH_SIZE, codes.size());
            List<String> batch = codes.subList(from, to);
            List<TopIdentCodeDTO> results = queryIdentCode(orderBO, batch, identType, rootCatId, brandId);
            if (CollectionUtil.isEmpty(results)) {
                continue;
            }
            for (TopIdentCodeDTO dto : results) {
                ImeiBO imeiBO = codeToImei.get(dto.getIdentCode());
                if (Objects.isNull(imeiBO) || Objects.isNull(dto.getAvailable())) {
                    continue;
                }
                ImeiConsts.PlatformImei status = Boolean.TRUE.equals(dto.getAvailable())
                        ? ImeiConsts.PlatformImei.NORMAL
                        : ImeiConsts.PlatformImei.RISK;
                log.info("淘宝二销验证：orderCode={}, identType={}, code={}, available={}, reason={}, result={}",
                        orderBO.getOrderCode(), identType, dto.getIdentCode(),
                        dto.getAvailable(), dto.getUnAvailableReason(), status.getName());
                imeiFacade.update(
                        new ImeiParam().setPlatformImei(status.getCode()).setPlatformTime(DateUtil.date()),
                        new ImeiQuery().setId(imeiBO.getId())
                                .setPlatformImei(ImeiConsts.PlatformImei.WAIT_QUERY.getCode()));
            }
        }
    }

    /**
     * 调用 alibaba.ascp.logistics.identcode.query 批量查询串码可销售状态。
     *
     * @param orderBO    订单（用于日志）
     * @param identCodes 待查询串码列表
     * @param identType  标识码类型 SN / IMEI
     * @param rootCatId  淘宝根类目 ID
     * @param brandId    淘宝品牌 ID
     * @return 接口返回的串码状态列表；接口异常返回空列表
     */
    private List<TopIdentCodeDTO> queryIdentCode(OrderBO orderBO, List<String> identCodes, String identType,
                                                 String rootCatId, String brandId) {
        AlibabaAscpLogisticsIdentcodeQueryRequest request = new AlibabaAscpLogisticsIdentcodeQueryRequest();
        request.setIdentCodeList(JacksonUtil.toJson(identCodes));
        request.setIdentType(identType);
        request.setRootCatId(rootCatId);
        request.setBrandId(brandId);
        try {
            AlibabaAscpLogisticsIdentcodeQueryResponse response = taobaoClient.execute(request, defaultSessionKey);
            if (!response.isSuccess()) {
                log.warn("淘宝二销验证接口返回失败，orderCode={}, identType={}, code={}, msg={}",
                        orderBO.getOrderCode(), identType, response.getSubCode(), response.getSubMsg());
                return new ArrayList<>();
            }
            ResultDTO result = response.getResult();
            if (Objects.isNull(result) || CollectionUtil.isEmpty(result.getIdentCodeList())) {
                return new ArrayList<>();
            }
            return result.getIdentCodeList();
        } catch (ApiException e) {
            log.error("调用淘宝二销验证接口异常，orderCode={}, identType={}", orderBO.getOrderCode(), identType, e);
            return new ArrayList<>();
        }
    }

    /**
     * 将订单 category（中文品类名）映射为淘宝 root_cat_id。
     * <p>先精确匹配，再退化为「订单 category 包含某个映射 key」做兜底匹配。</p>
     *
     * @param category 订单品类名
     * @return root_cat_id；未匹配时返回 null
     */
    private String resolveRootCatId(String category) {
        if (StrUtil.isBlank(category)) {
            return null;
        }
        String exact = CATEGORY_TO_ROOT_CAT_ID.get(category);
        if (StrUtil.isNotBlank(exact)) {
            return exact;
        }
        for (Map.Entry<String, String> entry : CATEGORY_TO_ROOT_CAT_ID.entrySet()) {
            if (category.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 将订单 brand（品牌名）映射为淘宝 brand_id。
     * <p>先精确匹配，再退化为「订单 brand 包含某个映射 key」做兜底匹配。</p>
     *
     * @param brand 订单品牌名
     * @return brand_id；未匹配时返回 null
     */
    private String resolveBrandId(String brand) {
        if (StrUtil.isBlank(brand)) {
            return null;
        }
        String exact = BRAND_TO_BRAND_ID.get(brand);
        if (StrUtil.isNotBlank(exact)) {
            return exact;
        }
        for (Map.Entry<String, String> entry : BRAND_TO_BRAND_ID.entrySet()) {
            if (brand.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
