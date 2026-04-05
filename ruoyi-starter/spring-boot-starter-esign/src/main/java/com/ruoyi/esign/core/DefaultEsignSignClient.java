package com.ruoyi.esign.core;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.esign.api.EsignAuthApi;
import com.ruoyi.esign.api.EsignSignApi;
import com.ruoyi.esign.exception.EsignException;
import com.ruoyi.esign.model.*;
import com.ruoyi.esign.properties.EsignProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认e签宝合同签署客户端实现
 *
 * @author ruoyi
 */
public class DefaultEsignSignClient implements EsignSignApi {

    private static final Logger log = LoggerFactory.getLogger(DefaultEsignSignClient.class);

    private final EsignProperties properties;
    private final EsignAuthApi esignAuthApi;

    public DefaultEsignSignClient(EsignProperties properties, EsignAuthApi esignAuthApi) {
        this.properties = properties;
        this.esignAuthApi = esignAuthApi;
    }

    @Override
    public CreateFlowResponse createFlow(CreateFlowRequest request) {
        String accessToken = getAccessToken();
        String url = properties.getCreateFlowUrl();
        String body = JSON.toJSONString(request);

        try {
            HttpResponse response = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(body)
                    .contentType("application/json")
                    .timeout(properties.getReadTimeout().intValue())
                    .execute();

            if (!response.isOk()) {
                throw new EsignException("创建签署流程失败，HTTP状态码：" + response.getStatus());
            }

            String responseBody = response.body();
            CreateFlowResponse flowResponse = JSON.parseObject(responseBody, CreateFlowResponse.class);

            if (!Boolean.TRUE.equals(flowResponse.getSuccess())) {
                log.error("创建e签宝签署流程失败，code:{}, message:{}", flowResponse.getCode(), flowResponse.getMessage());
            }

            return flowResponse;
        } catch (Exception e) {
            log.error("创建e签宝签署流程异常", e);
            throw new EsignException("创建签署流程异常", e);
        }
    }

    @Override
    public AddFileResponse addFile(AddFileRequest request) {
        String accessToken = getAccessToken();
        String url = properties.getAddFileUrl();
        String body = JSON.toJSONString(request);

        try {
            HttpResponse response = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(body)
                    .contentType("application/json")
                    .timeout(properties.getReadTimeout().intValue())
                    .execute();

            if (!response.isOk()) {
                throw new EsignException("添加签署文件失败，HTTP状态码：" + response.getStatus());
            }

            String responseBody = response.body();
            AddFileResponse addFileResponse = JSON.parseObject(responseBody, AddFileResponse.class);

            if (!Boolean.TRUE.equals(addFileResponse.getSuccess())) {
                log.error("添加e签宝签署文件失败，code:{}, message:{}", addFileResponse.getCode(), addFileResponse.getMessage());
            }

            return addFileResponse;
        } catch (Exception e) {
            log.error("添加e签宝签署文件异常", e);
            throw new EsignException("添加签署文件异常", e);
        }
    }

    @Override
    public AddSignerResponse addSigner(AddSignerRequest request) {
        String accessToken = getAccessToken();
        String url = properties.getAddSignerUrl();
        String body = JSON.toJSONString(request);

        try {
            HttpResponse response = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(body)
                    .contentType("application/json")
                    .timeout(properties.getReadTimeout().intValue())
                    .execute();

            if (!response.isOk()) {
                throw new EsignException("添加签署人失败，HTTP状态码：" + response.getStatus());
            }

            String responseBody = response.body();
            AddSignerResponse addSignerResponse = JSON.parseObject(responseBody, AddSignerResponse.class);

            if (!Boolean.TRUE.equals(addSignerResponse.getSuccess())) {
                log.error("添加e签宝签署人失败，code:{}, message:{}", addSignerResponse.getCode(), addSignerResponse.getMessage());
            }

            return addSignerResponse;
        } catch (Exception e) {
            log.error("添加e签宝签署人异常", e);
            throw new EsignException("添加签署人异常", e);
        }
    }

    @Override
    public StartFlowResponse startFlow(String flowId) {
        if (StrUtil.isBlank(flowId)) {
            throw new IllegalArgumentException("flowId不能为空");
        }

        String accessToken = getAccessToken();
        String url = properties.getStartFlowUrl();
        StartFlowRequest request = new StartFlowRequest();
        request.setFlowId(flowId);
        String body = JSON.toJSONString(request);

        try {
            HttpResponse response = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(body)
                    .contentType("application/json")
                    .timeout(properties.getReadTimeout().intValue())
                    .execute();

            if (!response.isOk()) {
                throw new EsignException("发起签署流程失败，HTTP状态码：" + response.getStatus());
            }

            String responseBody = response.body();
            StartFlowResponse startFlowResponse = JSON.parseObject(responseBody, StartFlowResponse.class);

            if (!Boolean.TRUE.equals(startFlowResponse.getSuccess())) {
                log.error("发起e签宝签署流程失败，code:{}, message:{}", startFlowResponse.getCode(), startFlowResponse.getMessage());
            }

            return startFlowResponse;
        } catch (Exception e) {
            log.error("发起e签宝签署流程异常", e);
            throw new EsignException("发起签署流程异常", e);
        }
    }

    @Override
    public QueryFlowResponse getFlowDetail(String flowId) {
        if (StrUtil.isBlank(flowId)) {
            throw new IllegalArgumentException("flowId不能为空");
        }

        String accessToken = getAccessToken();
        String url = properties.getQueryFlowUrl();
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("flowId", flowId);
        String body = JSON.toJSONString(params);

        try {
            HttpResponse response = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(body)
                    .contentType("application/json")
                    .timeout(properties.getReadTimeout().intValue())
                    .execute();

            if (!response.isOk()) {
                throw new EsignException("查询签署流程详情失败，HTTP状态码：" + response.getStatus());
            }

            String responseBody = response.body();
            QueryFlowResponse queryResponse = JSON.parseObject(responseBody, QueryFlowResponse.class);

            if (!Boolean.TRUE.equals(queryResponse.getSuccess())) {
                log.error("查询e签宝签署流程详情失败，code:{}, message:{}", queryResponse.getCode(), queryResponse.getMessage());
            }

            return queryResponse;
        } catch (Exception e) {
            log.error("查询e签宝签署流程详情异常", e);
            throw new EsignException("查询签署流程详情异常", e);
        }
    }

    @Override
    public GetSignUrlResponse getSignUrl(GetSignUrlRequest request) {
        String accessToken = getAccessToken();
        String url = properties.getSignUrl();
        String body = JSON.toJSONString(request);

        try {
            HttpResponse response = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(body)
                    .contentType("application/json")
                    .timeout(properties.getReadTimeout().intValue())
                    .execute();

            if (!response.isOk()) {
                throw new EsignException("获取签署页面URL失败，HTTP状态码：" + response.getStatus());
            }

            String responseBody = response.body();
            GetSignUrlResponse getSignUrlResponse = JSON.parseObject(responseBody, GetSignUrlResponse.class);

            if (!Boolean.TRUE.equals(getSignUrlResponse.getSuccess())) {
                log.error("获取e签宝签署页面URL失败，code:{}, message:{}", getSignUrlResponse.getCode(), getSignUrlResponse.getMessage());
            }

            return getSignUrlResponse;
        } catch (Exception e) {
            log.error("获取e签宝签署页面URL异常", e);
            throw new EsignException("获取签署页面URL异常", e);
        }
    }

    /**
     * 获取访问令牌
     *
     * @return 访问令牌
     */
    private String getAccessToken() {
        AccessTokenResponse tokenResponse = esignAuthApi.getAccessToken();
        if (!Boolean.TRUE.equals(tokenResponse.getSuccess()) || StrUtil.isBlank(tokenResponse.getAccessToken())) {
            throw new EsignException(tokenResponse.getCode(), tokenResponse.getMessage());
        }
        return tokenResponse.getAccessToken();
    }
}
