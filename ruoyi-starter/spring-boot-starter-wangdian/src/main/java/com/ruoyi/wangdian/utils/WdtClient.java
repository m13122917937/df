package com.ruoyi.wangdian.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.wangdian.param.Pager;
import com.ruoyi.wangdian.param.base.ProductParams;
import com.ruoyi.wangdian.param.base.ProviderParams;
import com.ruoyi.wangdian.param.base.product.GoodsInfo;
import com.ruoyi.wangdian.param.base.product.SpecInfo;
import com.ruoyi.wangdian.param.goods.GoodsQueryParams;
import com.ruoyi.wangdian.param.order.TradeQueryParams;
import com.ruoyi.wangdian.param.stock.StockInInfoParam;
import com.ruoyi.wangdian.properties.WdProperties;
import com.ruoyi.wangdian.rep.WDRep;
import com.ruoyi.wangdian.rep.goods.GoodsQueryWithSpecRep;
import com.ruoyi.wangdian.rep.stock.StockInRep;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
@AllArgsConstructor
public class WdtClient {

    private WdProperties wdProperties;

    public void orderList(TradeQueryParams params, Pager pager){
        Map<String, Object> pagerMap = JacksonUtil.beanToMap(pager);
        String response = execute("sales.TradeQuery.queryWithDetail",  params, pagerMap);
        System.out.println( response );
    }

    public void goods(GoodsInfo goodsInfo,SpecInfo specInfoList) throws IOException {
        String response = execute("goods.Goods.push", goodsInfo,List.of(specInfoList), null);
        log.info("创建商品，返回结果：{}", response);
        WDRep wdRep = JacksonUtil.parse(response, WDRep.class);
        if (Objects.isNull(wdRep) || !wdRep.isSuccess()) {
            throw new ServiceException(wdRep.getMessage());
        }

    }

    /**
     * 创建其他入库单
     *
     * @param stockInInfoParam
     * @return
     * @throws IOException
     */
    public String stockInPush(StockInInfoParam stockInInfoParam) throws IOException {
        String response = execute("wms.stockin.Other.createOtherOrder", stockInInfoParam, null);
        log.info("创建其他入库单，返回结果：{}", response);
        StockInRep stock = JacksonUtil.parse(response, StockInRep.class);
        if (Objects.isNull(stock) || !stock.isSuccess()) {
            throw new ServiceException(stock.getMessage());
        }
        return stock.getStockinNo();
    }

    /**
     * 查询店铺信息
     *
     * @param params
     * @return
     * @throws IOException
     */
    public void createProvider(ProviderParams params) throws IOException {
        String response = execute("setting.PurchaseProvider.push", params, null);
        log.info("创建供应商，返回结果：{}", response);
        WDRep wdRep = JacksonUtil.parse(response, WDRep.class);
        if (Objects.isNull(wdRep)) {
            throw new ServiceException("创建供应商响应解析失败");
        }
        if (!wdRep.isSuccess()) {
            throw new ServiceException(wdRep.getMessage());
        }
    }

    // 时间戳基准：2012-01-01 00:00:00 UTC
    private static final long TIMESTAMP_BASE = 1325347200L;

    /**
     * 生成开放平台签名
     *
     * @param sid        卖家账号（由ERP分配）
     * @param appKey     appkey
     * @param appSecret  完整 appsecret，格式：secret:salt
     * @param method     接口方法名，如 wms.stockout.Sales.weighingExt
     * @param bodyParams 业务参数对象（会转为 JSON 数组或对象）
     * @param pager      分页参数，可为 null（包含 page_size, page_no, calc_total）
     * @return 生成的 sign 字符串
     */
    protected static String generateSign(String sid, String appKey, String appSecret,
                                      String method, Object bodyParams, Map<String, Object> pager) {
        if (StrUtil.hasBlank(sid, appKey, appSecret, method)) {
            throw new IllegalArgumentException("必要参数不能为空");
        }

        // 解析 appsecret -> secret 和 salt
        String[] secretParts = appSecret.split(":", 2);
        if (secretParts.length != 2) {
            throw new IllegalArgumentException("appsecret 格式错误，应为 secret:salt");
        }
        String secret = secretParts[0];
        String salt = secretParts[1];

        // 计算时间戳
        long timestamp = (System.currentTimeMillis() / 1000) - TIMESTAMP_BASE;

        // 构建 body 字符串（压缩 JSON，无空格换行）
        String bodyStr = JacksonUtil.toJson(bodyParams);

        // 初始化参数 map（注意：body 是字符串，不是对象）
        Map<String, Object> params = new HashMap<>();
        params.put("sid", sid);
        params.put("key", appKey);
        params.put("salt", salt);
        params.put("method", method);
        params.put("v", "1.0");
        params.put("timestamp", timestamp);
        params.put("body", bodyStr);

        // 添加分页参数（如果存在）
        if (pager != null) {
            for (Map.Entry<String, Object> entry : pager.entrySet()) {
                String key = entry.getKey();
                if ("page_size".equals(key) || "page_no".equals(key) || "calc_total".equals(key)) {
                    params.put(key, entry.getValue());
                }
            }
        }

        // 按 key 字典升序排序
        List<String> sortedKeys = new ArrayList<>(params.keySet());
        Collections.sort(sortedKeys);

        // 拼接字符串：secret + (key1 + value1 + key2 + value2 + ...) + secret
        StringBuilder sb = new StringBuilder(secret);
        for (String key : sortedKeys) {
            Object value = params.get(key);
            // 特别注意：body 的 value 是字符串，但要原样拼接（不加引号）
            // 其他值直接 toString()
            sb.append(key).append(value == null ? "" : value.toString());
        }
        sb.append(secret);
        // MD5 并转小写
        return DigestUtil.md5Hex(sb.toString()).toLowerCase();
    }

    String execute(String method, Object param, Map<String, Object> pager) {
        Object[] bodyParams = new Object[]{param};
        // 无分页
        String sign = generateSign(wdProperties.getSid(), wdProperties.getAppkey(), wdProperties.getAppsecret(), method, bodyParams,pager);
        long timestamp = (System.currentTimeMillis() / 1000) - TIMESTAMP_BASE;
        String salt = wdProperties.getAppsecret().split(":")[1];
        String url = null;
        if(Objects.isNull(pager)){
            url = String.format("http://wdt.wangdian.cn/openapi?key=%s&method=%s&salt=%s&sid=%s&sign=%s&timestamp=%d&v=1.0", wdProperties.getAppkey(), method, salt, wdProperties.getSid(), sign, timestamp);
        }else {
            url = String.format("http://wdt.wangdian.cn/openapi?key=%s&method=%s&salt=%s&sid=%s&sign=%s&timestamp=%d&v=1.0&page_size=%d&page_no=%d&calc_total=%d",
                    wdProperties.getAppkey(), method, salt, wdProperties.getSid(), sign, timestamp, pager.get("page_size"), pager.get("page_no"), pager.get("calc_total"));
        }
        String json = JacksonUtil.toJson(bodyParams);
        log.info("ERP请求地址：{},请求参数：{}", url, json);
        String body = HttpRequest.post(url).body(json).timeout(wdProperties.getConnectTimeout()).execute().body();
        return body;
    }

    String execute(String method, Object param, Object param2, Map<String, Object> pager ) {
        Object[] bodyParams = new Object[]{param, param2};
        // 无分页
        String sign = generateSign(wdProperties.getSid(), wdProperties.getAppkey(), wdProperties.getAppsecret(), method, bodyParams, pager);
        long timestamp = (System.currentTimeMillis() / 1000) - TIMESTAMP_BASE;
        String salt = wdProperties.getAppsecret().split(":")[1];
        String url = null;
        if(Objects.isNull(pager)){
            url = String.format("http://wdt.wangdian.cn/openapi?key=%s&method=%s&salt=%s&sid=%s&sign=%s&timestamp=%d&v=1.0", wdProperties.getAppkey(), method, salt, wdProperties.getSid(), sign, timestamp);
        }else {
            url = String.format("http://wdt.wangdian.cn/openapi?key=%s&method=%s&salt=%s&sid=%s&sign=%s&timestamp=%d&v=1.0&page_size=%d&page_no=%d&calc_total=%d",
                    wdProperties.getAppkey(), method, salt, wdProperties.getSid(), sign, timestamp, (Integer) pager.get("page_size"), (Integer) pager.get("page_no"), (Integer) pager.get("calc_total"));
        }
        String json = JacksonUtil.toJson(bodyParams);
        log.info("ERP请求地址：{},请求参数：{}", url, json);
        String body = HttpRequest.post(url).body(json).timeout(wdProperties.getConnectTimeout()).execute().body();
        return body;
    }

    /**
     * 查询商品列表（带规格信息）
     *
     * @param params 查询条件
     * @param pager  分页参数
     * @return 查询结果（包含商品列表和规格信息）
     * @throws IOException 网络请求异常
     */
    public GoodsQueryWithSpecRep queryGoodsWithSpec(GoodsQueryParams params, Pager pager) throws IOException {
        Map<String, Object> pagerMap = JacksonUtil.beanToMap(pager);
        String response = execute("goods.Goods.queryWithSpec", params, pagerMap);
        log.info("ERP查询商品，返回结果：{}", response);
        GoodsQueryWithSpecRep result = JacksonUtil.parse(response, GoodsQueryWithSpecRep.class);
        if (Objects.isNull(result) || !result.isSuccess()) {
            throw new ServiceException(result != null ? result.getMessage() : "查询商品失败");
        }
        return result;
    }

}




