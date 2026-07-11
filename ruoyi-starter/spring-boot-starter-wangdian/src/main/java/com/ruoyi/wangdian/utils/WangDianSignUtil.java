package com.ruoyi.wangdian.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.wangdian.param.base.ProviderParams;

import java.util.*;

public class WangDianSignUtil {

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
    public static String generateSign(String sid, String appKey, String appSecret,
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

    /**
     * 重载方法：无分页参数
     */
    public static String generateSign(String sid, String appKey, String appSecret,
                                      String method, Object bodyParams) {
        return generateSign(sid, appKey, appSecret, method, bodyParams, null);
    }

    // ================== 示例用法 ==================

    public static void main(String[] args) {
        String sid = "lltxsb3";
        String appKey = "lltxsb3-yy";
        String appSecret = "9290c78ee0b9a66e2f70bad499868281:1294184460056224683b0f894ceea66f";
        String method = "setting.PurchaseProvider.push";

        ProviderParams build = ProviderParams.builder().provider_no("123").provider_name("123").build();
        // body 参数：数组形式（根据文档示例）
        Object[] bodyParams = new Object[]{
                build
        };

        // 无分页
        String sign = generateSign(sid, appKey, appSecret, method, bodyParams);
        System.out.println("Generated Sign: " + sign);
        // 预期输出（与文档一致）: 20f557aaf9190797c581bbceeb6d5a5c

        // 构建完整请求 URL（不含 body，body 放在 POST 请求体中）
        long timestamp = (System.currentTimeMillis() / 1000) - TIMESTAMP_BASE;
        String salt = appSecret.split(":")[1];
        String url = String.format(
            "http://wdt.wangdian.cn/openapi?key=%s&method=%s&salt=%s&sid=%s&sign=%s&timestamp=%d&v=1.0",
            appKey, method, salt, sid, sign, timestamp
        );
        String json = JacksonUtil.toJson(bodyParams);
        String body = HttpRequest.post(url).body(json).execute().body();
        System.out.println("Request URL: " + url);
        System.out.println("Request json: " + json);
        System.out.println( body );
    }
}