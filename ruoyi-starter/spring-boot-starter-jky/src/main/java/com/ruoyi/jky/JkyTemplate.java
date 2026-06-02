package com.ruoyi.jky;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.jky.model.JkyApiMethod;
import com.ruoyi.jky.model.JkyResponse;
import com.ruoyi.jky.param.goods.GoodsListParam;
import com.ruoyi.jky.param.logistics.LogisticsUpdateParam;
import com.ruoyi.jky.param.order.OrderQueryParam;
import com.ruoyi.jky.param.refund.RefundQueryParam;
import com.ruoyi.jky.param.sn.SnReportParam;
import com.ruoyi.jky.param.stock.StockCreateAndStockInParam;
import com.ruoyi.jky.param.vendor.VendorCreateParam;
import com.ruoyi.jky.properties.JkyProperties;
import com.ruoyi.jky.rep.goods.GoodsListRep;
import com.ruoyi.jky.rep.logistics.LogisticsUpdateRep;
import com.ruoyi.jky.rep.order.OrderQueryRep;
import com.ruoyi.jky.rep.refund.RefundQueryRep;
import com.ruoyi.jky.rep.sn.SnReportRep;
import com.ruoyi.jky.rep.stock.StockCreateAndStockInRep;
import com.ruoyi.jky.rep.vendor.VendorCreateRep;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
public class JkyTemplate {

    private static final String BIZ_CONTENT = "bizcontent";

    private static final String BIZ_DATA = "bizdata";

    private final JkyProperties jkyProperties;

    /**
     * 创建 JKY 模板实例。
     */
    public JkyTemplate(final JkyProperties jkyProperties) {
        this.jkyProperties = jkyProperties;
    }

    /**
     * 创建供应商。
     */
    public JkyResponse<VendorCreateRep> createVendor(final VendorCreateParam param) {
        return execute(JkyApiMethod.VENDOR_CREATE, BIZ_CONTENT, param, new TypeReference<JkyResponse<VendorCreateRep>>() {
        });
    }

    /**
     * 分页查询货品信息。
     */
    public JkyResponse<List<GoodsListRep>> goodsList(final GoodsListParam param) {
        return execute(JkyApiMethod.GOODS_LIST, BIZ_CONTENT, param, new TypeReference<JkyResponse<List<GoodsListRep>>>() {
        });
    }

    /**
     * 查询销售单。
     */
    public JkyResponse<OrderQueryRep> queryOrders(final OrderQueryParam param) {
        return execute(JkyApiMethod.ORDER_QUERY, BIZ_CONTENT, param, new TypeReference<JkyResponse<OrderQueryRep>>() {
        });
    }

    /**
     * 分页查询网店售后单。
     */
    public JkyResponse<RefundQueryRep> listRefunds(final RefundQueryParam param) {
        return execute(JkyApiMethod.REFUND_LIST, BIZ_CONTENT, param, new TypeReference<JkyResponse<RefundQueryRep>>() {
        });
    }

    /**
     * 发货单完成并更新物流信息。
     */
    public JkyResponse<List<LogisticsUpdateRep>> updateLogisticsInfo(final LogisticsUpdateParam param) {
        return execute(JkyApiMethod.LOGISTICS_UPDATE, BIZ_CONTENT, param, new TypeReference<JkyResponse<List<LogisticsUpdateRep>>>() {
        });
    }

    /**
     * 发货单 SN 通知。
     */
    public JkyResponse<SnReportRep> reportSerialNumbers(final SnReportParam param) {
        return execute(JkyApiMethod.SN_REPORT, BIZ_DATA, param, new TypeReference<JkyResponse<SnReportRep>>() {
        });
    }

    /**
     * 创建库存并入库。
     */
    public JkyResponse<StockCreateAndStockInRep> createAndStockIn(final StockCreateAndStockInParam param) {
        return execute(JkyApiMethod.STOCK_CREATE_AND_STOCK_IN, BIZ_CONTENT, param,
                new TypeReference<JkyResponse<StockCreateAndStockInRep>>() {
                });
    }

    private <T> JkyResponse<T> execute(final String method, final String bizParamName, final Object param,
                                       final TypeReference<JkyResponse<T>> typeReference) {
        checkProperties();
        String bizJson = JacksonUtil.toJson(param);
        Map<String, Object> requestParams = buildRequestParams(method, bizParamName, bizJson);
        requestParams.put("sign", sign(requestParams));
        log.info("吉客云请求方法：{}，请求参数：{}", method, bizJson);
        String body = HttpRequest.post(jkyProperties.getBaseUrl())
                .form(requestParams)
                .timeout(jkyProperties.getConnectTimeout())
                .execute()
                .body();
        log.info("吉客云请求方法：{}，返回结果：{}", method, body);
        if (StrUtil.isBlank(body)) {
            throw new ServiceException("吉客云接口返回为空");
        }
        JkyResponse<T> response = JacksonUtil.parse(body, typeReference);
        if (ObjectUtil.isNull(response)) {
            throw new ServiceException("吉客云接口响应解析失败");
        }
        return response;
    }

    private Map<String, Object> buildRequestParams(final String method, final String bizParamName, final String bizJson) {
        Map<String, Object> requestParams = MapUtil.newHashMap(8);
        requestParams.put("method", method);
        requestParams.put("appkey", jkyProperties.getAppKey());
        requestParams.put("version", jkyProperties.getVersion());
        requestParams.put("contenttype", jkyProperties.getContentType());
        requestParams.put("timestamp", DateUtil.now());
        requestParams.put(bizParamName, bizJson);
        if (StrUtil.isNotBlank(jkyProperties.getToken())) {
            requestParams.put("token", jkyProperties.getToken());
        }
        return requestParams;
    }

    private String sign(final Map<String, Object> requestParams) {
        StringBuilder builder = new StringBuilder(jkyProperties.getAppSecret());
        requestParams.entrySet().stream()
                .filter(entry -> !"sign".equals(entry.getKey()))
                .filter(entry -> !"contextid".equals(entry.getKey()))
                .filter(entry -> !"token".equals(entry.getKey()))
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(entry -> builder.append(entry.getKey()).append(entry.getValue()));
        builder.append(jkyProperties.getAppSecret());
        return SecureUtil.md5(builder.toString().toLowerCase());
    }

    private void checkProperties() {
        if (StrUtil.hasBlank(jkyProperties.getBaseUrl(), jkyProperties.getAppKey(), jkyProperties.getAppSecret())) {
            throw new ServiceException("吉客云配置不能为空");
        }
    }

}
