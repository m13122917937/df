package com.ruoyi.esign.core;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.esign.api.EsignSignApi;
import com.ruoyi.esign.exception.EsignException;
import com.ruoyi.esign.model.*;
import com.ruoyi.esign.properties.EsignProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认e签宝合同签署客户端实现
 *
 * @author ruoyi
 */
public class DefaultEsignSignClient implements EsignSignApi {

    private static final Logger log = LoggerFactory.getLogger(DefaultEsignSignClient.class);

    private final EsignProperties properties;
    private final ObjectMapper objectMapper;

    public DefaultEsignSignClient(EsignProperties properties) {
        this.properties = properties;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public CreateFlowResponse createFlow(CreateFlowRequest request) {
        String url = properties.getCreateFlowUrl();
        return executePost(url, request, CreateFlowResponse.class);
    }

    @Override
    public AddFileResponse addFile(AddFileRequest request) {
        String url = properties.getAddFileUrl();
        return executePost(url, request, AddFileResponse.class);
    }

    @Override
    public AddSignerResponse addSigner(AddSignerRequest request) {
        String url = properties.getAddSignerUrl();
        return executePost(url, request, AddSignerResponse.class);
    }

    @Override
    public StartFlowResponse startFlow(String flowId) {
        if (StrUtil.isBlank(flowId)) {
            throw new IllegalArgumentException("flowId不能为空");
        }
        String url = properties.getStartFlowUrl();
        StartFlowRequest request = new StartFlowRequest();
        request.setFlowId(flowId);
        return executePost(url, request, StartFlowResponse.class);
    }

    @Override
    public QueryFlowResponse getFlowDetail(String flowId) {
        if (StrUtil.isBlank(flowId)) {
            throw new IllegalArgumentException("flowId不能为空");
        }
        String url = properties.getQueryFlowUrl();
        Map<String, Object> params = new HashMap<>();
        params.put("flowId", flowId);
        return executePost(url, params, QueryFlowResponse.class);
    }

    @Override
    public GetSignUrlResponse getSignUrl(GetSignUrlRequest request) {
        String url = properties.getSignUrl();
        return executePost(url, request, GetSignUrlResponse.class);
    }

    /**
     * 执行POST请求，使用请求签名鉴权方式
     */
    @SuppressWarnings("unchecked")
    private <T> T executePost(String url, Object request, Class<T> responseClass) {
        String body;
        try {
            body = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            log.error("序列化请求失败", e);
            throw new EsignException("序列化请求失败", e);
        }

        long timestamp = System.currentTimeMillis();
        String contentMd5 = generateContentMd5(body);
        String signature = generateSignature(body, timestamp);

        try {
            HttpRequest httpRequest = HttpRequest.post(url)
                    .body(body)
                    .header("Accept", "application/json")
                    .contentType("application/json; charset=UTF-8")
                    .header("Content-MD5", contentMd5)
                    .header("X-Tsign-Open-App-Id", properties.getAppId())
                    .header("X-Tsign-Open-Ca-Timestamp", String.valueOf(timestamp))
                    .header("X-Tsign-Open-Ca-Signature", signature)
                    .timeout(properties.getReadTimeout().intValue());

            HttpResponse response = httpRequest.execute();

            if (!response.isOk()) {
                throw new EsignException("请求失败，HTTP状态码：" + response.getStatus());
            }

            String responseBody = response.body();
            T result = objectMapper.readValue(responseBody, responseClass);

            logResponseError(result);
            return result;
        } catch (EsignException e) {
            throw e;
        } catch (IOException e) {
            log.error("反序列化响应失败", e);
            throw new EsignException("反序列化响应失败", e);
        } catch (Exception e) {
            log.error("e签宝API调用异常", e);
            throw new EsignException("API调用异常", e);
        }
    }

    /**
     * 生成Content-MD5
     */
    private String generateContentMd5(String body) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(body.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("生成Content-MD5失败", e);
            throw new EsignException("生成Content-MD5失败", e);
        }
    }

    /**
     * 生成请求签名
     * <p>
     * 签名规则：
     * 1. 将body参数按字典升序排序拼接（对于JSON body直接使用原文字符串）
     * 2. 拼接格式：body + timestamp + appSecret
     * 3. 使用SHA256加密得到签名
     */
    private String generateSignature(String body, long timestamp) {
        String signStr = body + timestamp + properties.getAppSecret();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(signStr.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("生成签名失败", e);
            throw new EsignException("生成签名失败", e);
        }
    }

    /**
     * 记录响应错误日志
     */
    private void logResponseError(Object result) {
        if (result instanceof CreateFlowResponse && !Boolean.TRUE.equals(((CreateFlowResponse) result).getSuccess())) {
            log.error("创建e签宝签署流程失败，code:{}, message:{}",
                    ((CreateFlowResponse) result).getCode(), ((CreateFlowResponse) result).getMessage());
        } else if (result instanceof AddFileResponse && !Boolean.TRUE.equals(((AddFileResponse) result).getSuccess())) {
            log.error("添加e签宝签署文件失败，code:{}, message:{}",
                    ((AddFileResponse) result).getCode(), ((AddFileResponse) result).getMessage());
        } else if (result instanceof AddSignerResponse && !Boolean.TRUE.equals(((AddSignerResponse) result).getSuccess())) {
            log.error("添加e签宝签署人失败，code:{}, message:{}",
                    ((AddSignerResponse) result).getCode(), ((AddSignerResponse) result).getMessage());
        } else if (result instanceof StartFlowResponse && !Boolean.TRUE.equals(((StartFlowResponse) result).getSuccess())) {
            log.error("发起e签宝签署流程失败，code:{}, message:{}",
                    ((StartFlowResponse) result).getCode(), ((StartFlowResponse) result).getMessage());
        } else if (result instanceof QueryFlowResponse && !Boolean.TRUE.equals(((QueryFlowResponse) result).getSuccess())) {
            log.error("查询e签宝签署流程详情失败，code:{}, message:{}",
                    ((QueryFlowResponse) result).getCode(), ((QueryFlowResponse) result).getMessage());
        } else if (result instanceof GetSignUrlResponse && !Boolean.TRUE.equals(((GetSignUrlResponse) result).getSuccess())) {
            log.error("获取e签宝签署页面URL失败，code:{}, message:{}",
                    ((GetSignUrlResponse) result).getCode(), ((GetSignUrlResponse) result).getMessage());
        }
    }
}
