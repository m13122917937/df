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
import com.ruoyi.jky.param.JkyStockInAndDeliveryParam;
import com.ruoyi.jky.param.goods.GoodsListParam;
import com.ruoyi.jky.param.logistics.LogisticsUpdateParam;
import com.ruoyi.jky.param.order.OrderQueryParam;
import com.ruoyi.jky.param.refund.RefundQueryParam;
import com.ruoyi.jky.param.sn.SnReportParam;
import com.ruoyi.jky.param.stock.StockCreateAndStockInParam;
import com.ruoyi.jky.param.vendor.VendorCreateParam;
import com.ruoyi.jky.param.warehouse.WarehouseListParam;
import com.ruoyi.jky.properties.JkyProperties;
import com.ruoyi.jky.rep.JkyStockInAndDeliveryRep;
import com.ruoyi.jky.rep.goods.GoodsListRep;
import com.ruoyi.jky.rep.logistics.LogisticsUpdateRep;
import com.ruoyi.jky.rep.order.OrderQueryRep;
import com.ruoyi.jky.rep.refund.RefundQueryRep;
import com.ruoyi.jky.rep.sn.SnReportRep;
import com.ruoyi.jky.rep.stock.StockCreateAndStockInRep;
import com.ruoyi.jky.rep.vendor.VendorCreateRep;
import com.ruoyi.jky.rep.warehouse.WarehouseListRep;
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
        if (isDisabled(JkyApiMethod.VENDOR_CREATE)) {
            return new JkyResponse<>();
        }
        return execute(JkyApiMethod.VENDOR_CREATE, BIZ_CONTENT, param, new TypeReference<JkyResponse<VendorCreateRep>>() {
        });
    }

    /**
     * 分页查询货品信息。
     */
    public JkyResponse<List<GoodsListRep>> goodsList(final GoodsListParam param) {
        if (isDisabled(JkyApiMethod.GOODS_LIST)) {
            return new JkyResponse<>();
        }
        return execute(JkyApiMethod.GOODS_LIST, BIZ_CONTENT, param, new TypeReference<JkyResponse<List<GoodsListRep>>>() {
        });
    }

    /**
     * 分页查询仓库信息。
     */
    public JkyResponse<WarehouseListRep> warehouseList(final WarehouseListParam param) {
        if (isDisabled(JkyApiMethod.WAREHOUSE_LIST)) {
            return new JkyResponse<>();
        }
        return execute(JkyApiMethod.WAREHOUSE_LIST, BIZ_CONTENT, param, new TypeReference<JkyResponse<WarehouseListRep>>() {
        });
    }

    /**
     * 查询销售单。
     */
    public JkyResponse<OrderQueryRep> queryOrders(final OrderQueryParam param) {
        if (isDisabled(JkyApiMethod.ORDER_QUERY)) {
            return new JkyResponse<>();
        }
        return execute(JkyApiMethod.ORDER_QUERY, BIZ_CONTENT, param, new TypeReference<JkyResponse<OrderQueryRep>>() {
        });
    }

    /**
     * 分页查询网店售后单。
     */
    public JkyResponse<RefundQueryRep> listRefunds(final RefundQueryParam param) {
        if (isDisabled(JkyApiMethod.REFUND_LIST)) {
            return new JkyResponse<>();
        }
        return execute(JkyApiMethod.REFUND_LIST, BIZ_CONTENT, param, new TypeReference<JkyResponse<RefundQueryRep>>() {
        });
    }

    /**
     * 发货单完成并更新物流信息。
     */
    public JkyResponse<List<LogisticsUpdateRep>> updateLogisticsInfo(final LogisticsUpdateParam param) {
        if (isDisabled(JkyApiMethod.LOGISTICS_UPDATE)) {
            return new JkyResponse<>();
        }
        return execute(JkyApiMethod.LOGISTICS_UPDATE, BIZ_CONTENT, param, new TypeReference<JkyResponse<List<LogisticsUpdateRep>>>() {
        });
    }

    /**
     * 发货单 SN 通知。
     */
    public JkyResponse<SnReportRep> reportSerialNumbers(final SnReportParam param) {
        if (isDisabled(JkyApiMethod.SN_REPORT)) {
            return new JkyResponse<>();
        }
        return execute(JkyApiMethod.SN_REPORT, BIZ_DATA, param, new TypeReference<JkyResponse<SnReportRep>>() {
        });
    }

    /**
     * 创建库存并入库。
     */
    public JkyResponse<StockCreateAndStockInRep> createAndStockIn(final StockCreateAndStockInParam param) {
        if (isDisabled(JkyApiMethod.STOCK_CREATE_AND_STOCK_IN)) {
            return new JkyResponse<>();
        }
        return execute(JkyApiMethod.STOCK_CREATE_AND_STOCK_IN, BIZ_CONTENT, param,
                new TypeReference<JkyResponse<StockCreateAndStockInRep>>() {
                });
    }

    /**
     * 创建库存入库、上报 SN 并更新物流信息。
     */
    public JkyStockInAndDeliveryRep stockInAndDelivery(
            final JkyStockInAndDeliveryParam param) {
        if (isDisabled("stockInAndDelivery")) {
            return new JkyStockInAndDeliveryRep();
        }
        if (ObjectUtil.hasNull(param, param.getStockInParam(), param.getSnReportParam(), param.getLogisticsUpdateParam())) {
            throw new ServiceException("吉客云入库发货参数不能为空");
        }
        JkyResponse<StockCreateAndStockInRep> stockInResponse = createAndStockIn(param.getStockInParam());
        checkStockInResponse(stockInResponse);
        JkyResponse<SnReportRep> snReportResponse = reportSerialNumbers(param.getSnReportParam());
        checkSnReportResponse(snReportResponse);
        JkyResponse<List<LogisticsUpdateRep>> logisticsUpdateResponse = updateLogisticsInfo(param.getLogisticsUpdateParam());
        checkLogisticsUpdateResponse(logisticsUpdateResponse);
        return new JkyStockInAndDeliveryRep()
                .setStockInResponse(stockInResponse)
                .setSnReportResponse(snReportResponse)
                .setLogisticsUpdateResponse(logisticsUpdateResponse);
    }

    private void checkStockInResponse(final JkyResponse<StockCreateAndStockInRep> response) {
        checkResponse(response, "吉客云创建库存入库失败");
        StockCreateAndStockInRep data = response.getResult().getData();
        if (ObjectUtil.isNull(data) || StrUtil.isBlank(data.getStockId())) {
            throw new ServiceException("吉客云创建库存入库返回结果为空");
        }
    }

    private void checkSnReportResponse(final JkyResponse<SnReportRep> response) {
        checkResponse(response, "吉客云发货单 SN 通知失败");
        SnReportRep data = response.getResult().getData();
        if (ObjectUtil.isNull(data) || !Boolean.TRUE.equals(data.getIsSuccess())) {
            throw new ServiceException(StrUtil.blankToDefault(data == null ? null : data.getErrorMsg(), "吉客云发货单 SN 通知失败"));
        }
    }

    private void checkLogisticsUpdateResponse(final JkyResponse<List<LogisticsUpdateRep>> response) {
        checkResponse(response, "吉客云发货单物流更新失败");
        List<LogisticsUpdateRep> data = response.getResult().getData();
        if (ObjectUtil.isEmpty(data)) {
            throw new ServiceException("吉客云发货单物流更新返回结果为空");
        }
        for (LogisticsUpdateRep item : data) {
            if (ObjectUtil.isNull(item) || !Boolean.TRUE.equals(item.getIsSuccess())) {
                throw new ServiceException(StrUtil.blankToDefault(item == null ? null : item.getError(), "吉客云发货单物流更新失败"));
            }
        }
    }

    private <T> void checkResponse(final JkyResponse<T> response, final String message) {
        if (ObjectUtil.isNull(response) || !Integer.valueOf(200).equals(response.getCode()) || ObjectUtil.isNull(response.getResult())) {
            throw new ServiceException(StrUtil.blankToDefault(response == null ? null : response.getMsg(), message));
        }
    }

    private boolean isDisabled(final String method) {
        if (Boolean.TRUE.equals(jkyProperties.getEnabled())) {
            return false;
        }
        log.info("吉客云接口调用已关闭，跳过请求方法：{}", method);
        return true;
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
