package com.ruoyi.kuaidi100;


import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.kuaidi100.model.ApiInfoConstant;
import com.ruoyi.kuaidi100.model.e.EOrderRequestParam;
import com.ruoyi.kuaidi100.model.e.EOrderResult;
import com.ruoyi.kuaidi100.properties.ExpressProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 快递信息查询.
 */
@Slf4j
public class EExpressClient extends ExpressClient{

    public EExpressClient(ExpressProperties expressProperties) {
        super(expressProperties);
    }

    /**
     * 提交电子面单请求
     *
     * @param param 请求参数主体
     * @return EOrderResult 解析后的响应结果
     */
    public EOrderResult submitOrder(EOrderRequestParam param) {
        // 1. 将业务参数对象转为JSON字符串
        String paramJson = JacksonUtil.toJson(param);

        // 2. 获取当前时间戳 (秒)
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        // 3. 根据V2文档生成签名: MD5(param + timestamp + key + customer)
        String signSource = paramJson + timestamp + expressProperties.getKey() + expressProperties.getSecret();
        String sign = SecureUtil.md5(signSource).toUpperCase();

        // 4. 构建最终的请求体 (Form表单格式)
        Map<String, Object> requestBody = MapUtil.newHashMap(5);
        requestBody.put("param", paramJson);           // 业务参数JSON
        requestBody.put("method", "order");           // 业务类型
        requestBody.put("sign", sign);                 // 签名
        requestBody.put("key", expressProperties.getKey());                 // key
        requestBody.put("t", timestamp);       // 时间戳

        try {
            log.info("【快递100电子面单V2】提交订单调用入参-- request：{}", requestBody );

            // 5. 发送POST请求到API
            String response = HttpUtil.post(ApiInfoConstant.NEW_TEMPLATE_URL, requestBody); // 生产环境

            log.info("【快递100电子面单V2】提交订单返回结果: {}", response);

            if (StrUtil.isBlank(response)) {
                log.error("【快递100电子面单V2】提交订单失败，返回结果为空");
                throw new ServiceException("API返回结果为空");
            }

            // 6. 解析返回结果
            EOrderResult result = JacksonUtil.parse(response, EOrderResult.class);
            if (result == null) {
                log.error("【快递100电子面单V2】提交订单失败，响应数据解析为null: {}", response);
                throw new ServiceException("API响应数据格式错误");
            }

            return result;

        } catch (HttpException e) {
            log.error("【快递100电子面单V2】提交订单网络异常: {}", e.getMessage(), e);
            throw new ServiceException("网络请求失败，请稍后再试");
        } catch (Exception e) {
            log.error("【快递100电子面单V2】提交订单未知异常: {}", e.getMessage(), e);
            throw new ServiceException("系统内部错误");
        }
    }



    /**
     * 取消电子面单请求
     * @param kuaidiCom 快递公司编码 (如: yunda, shentong)
     * @param kuaidiNum 快递单号
     * @param orderId 订单ID (可选，若有则提供，用于精准匹配)
     * @return String API原始返回的JSON字符串
     */
    public String cancelOrder(String kuaidiCom, String kuaidiNum, String orderId) {
        // 1. 构建取消订单的业务参数对象
        Map<String, String> cancelParamMap = MapUtil.newHashMap(3);
        cancelParamMap.put("com", kuaidiCom); // 快递公司编码
        cancelParamMap.put("num", kuaidiNum); // 快递单号
        if (StrUtil.isNotBlank(orderId)) {
            cancelParamMap.put("orderId", orderId); // 订单ID (可选)
        }

        // 2. 将参数对象转为JSON字符串
        String paramJson = JacksonUtil.toJson(cancelParamMap);

        // 3. 获取当前时间戳 (秒)
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        // 4. 根据取消接口文档生成签名: MD5(param + timestamp + key + customer)
        // 签名算法与提交订单完全相同
        String signSource = paramJson + timestamp + expressProperties.getKey() + expressProperties.getCustomer();
        String sign = SecureUtil.md5(signSource).toUpperCase();

        // 5. 构建最终的请求体 (Form表单格式)
        Map<String, Object> requestBody = MapUtil.newHashMap(5);
        requestBody.put("param", paramJson);           // 业务参数JSON
        requestBody.put("method", "cancel");           // 业务类型
        requestBody.put("sign", sign);                 // 签名
        requestBody.put("timestamp", timestamp);       // 时间戳
        requestBody.put("customer", expressProperties.getCustomer()); // 客户授权码

        try {
            log.info("【快递100电子面单取消】调用入参-- param: {}, sign: {}, timestamp: {}", paramJson, sign, timestamp);

            // 6. 发送POST请求到取消接口
            String response = HttpUtil.post(ApiInfoConstant.NEW_TEMPLATE_URL, requestBody); // 生产环境

            log.info("【快递100电子面单取消】返回结果: {}", response);
            return response;

        } catch (HttpException e) {
            log.error("【快递100电子面单取消】网络异常: {}", e.getMessage(), e);
            throw new ServiceException("网络请求失败，请稍后再试");
        } catch (Exception e) {
            log.error("【快递100电子面单取消】未知异常: {}", e.getMessage(), e);
            throw new ServiceException("系统内部错误");
        }
    }

    }
