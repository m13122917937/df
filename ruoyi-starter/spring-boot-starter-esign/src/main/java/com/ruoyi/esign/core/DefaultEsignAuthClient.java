package com.ruoyi.esign.core;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.esign.api.EsignAuthApi;
import com.ruoyi.esign.exception.EsignException;
import com.ruoyi.esign.model.*;
import com.ruoyi.esign.properties.EsignProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 默认e签宝认证客户端实现
 *
 * @author ruoyi
 */
public class DefaultEsignAuthClient implements EsignAuthApi {

    private static final Logger log = LoggerFactory.getLogger(DefaultEsignAuthClient.class);

    private final EsignProperties properties;

    /**
     * 缓存的accessToken
     */
    private volatile String accessToken;

    /**
     * accessToken过期时间戳
     */
    private volatile long expireTimestamp;

    public DefaultEsignAuthClient(EsignProperties properties) {
        this.properties = properties;
    }

    @Override
    public AccessTokenResponse getAccessToken() {
        // 如果token未过期，直接返回缓存
        if (StrUtil.isNotBlank(accessToken) && System.currentTimeMillis() < expireTimestamp) {
            AccessTokenResponse response = new AccessTokenResponse();
            response.setAccessToken(accessToken);
            response.setSuccess(true);
            return response;
        }

        AccessTokenRequest request = new AccessTokenRequest();
        request.setAppId(properties.getAppId());
        request.setAppSecret(properties.getAppSecret());

        String url = properties.getAccessTokenUrl();
        String body = JSON.toJSONString(request);

        try {
            HttpResponse response = HttpRequest.post(url)
                    .body(body)
                    .contentType("application/json")
                    .timeout(properties.getReadTimeout().intValue())
                    .execute();

            if (!response.isOk()) {
                throw new EsignException("获取accessToken失败，HTTP状态码：" + response.getStatus());
            }

            String responseBody = response.body();
            AccessTokenResponse tokenResponse = JSON.parseObject(responseBody, AccessTokenResponse.class);

            if (Boolean.TRUE.equals(tokenResponse.getSuccess())) {
                // 缓存token，提前5秒过期
                this.accessToken = tokenResponse.getAccessToken();
                this.expireTimestamp = System.currentTimeMillis() + (tokenResponse.getExpiresIn() - 5) * 1000L;
            } else {
                log.error("获取e签宝accessToken失败，code:{}, message:{}", tokenResponse.getCode(), tokenResponse.getMessage());
            }

            return tokenResponse;
        } catch (Exception e) {
            log.error("获取e签宝accessToken异常", e);
            throw new EsignException("获取accessToken异常", e);
        }
    }

    @Override
    public String buildPersonalAuthUrl(PersonalAuthRequest request) {
        // 使用配置中的回调地址，如果请求中没有指定
        if (StrUtil.isBlank(request.getRedirectUri())) {
            request.setRedirectUri(properties.getRedirectUri());
        }

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appId", properties.getAppId());
        params.put("redirectUri", request.getRedirectUri());
        params.put("thirdPartyUserId", request.getThirdPartyUserId());
        params.put("name", request.getName());
        params.put("idCard", request.getIdCard());
        if (StrUtil.isNotBlank(request.getMobile())) {
            params.put("mobile", request.getMobile());
        }
        if (StrUtil.isNotBlank(request.getEmail())) {
            params.put("email", request.getEmail());
        }
        params.put("faceRecognition", request.getFaceRecognition());
        params.put("expireTime", request.getExpireTime());
        if (StrUtil.isNotBlank(request.getState())) {
            params.put("state", request.getState());
        }

        return buildUrl(properties.getPersonalAuthUrl(), params);
    }

    @Override
    public String buildOrgAuthUrl(OrgAuthRequest request) {
        // 使用配置中的回调地址，如果请求中没有指定
        if (StrUtil.isBlank(request.getRedirectUri())) {
            request.setRedirectUri(properties.getRedirectUri());
        }

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appId", properties.getAppId());
        params.put("redirectUri", request.getRedirectUri());
        params.put("thirdPartyUserId", request.getThirdPartyUserId());
        params.put("agentName", request.getAgentName());
        params.put("agentIdCard", request.getAgentIdCard());
        params.put("agentMobile", request.getAgentMobile());
        params.put("orgName", request.getOrgName());
        params.put("orgCertType", request.getOrgCertType());
        params.put("orgCertNo", request.getOrgCertNo());
        if (StrUtil.isNotBlank(request.getLegalRepName())) {
            params.put("legalRepName", request.getLegalRepName());
        }
        if (StrUtil.isNotBlank(request.getLegalRepIdCard())) {
            params.put("legalRepIdCard", request.getLegalRepIdCard());
        }
        params.put("legalRepAuth", request.getLegalRepAuth());
        params.put("expireTime", request.getExpireTime());
        if (StrUtil.isNotBlank(request.getState())) {
            params.put("state", request.getState());
        }

        return buildUrl(properties.getOrganizationalAuthUrl(), params);
    }

    @Override
    public AuthInfoResponse getAuthInfo(String authFlowId) {
        if (StrUtil.isBlank(authFlowId)) {
            throw new IllegalArgumentException("authFlowId不能为空");
        }

        // 获取accessToken
        AccessTokenResponse tokenResponse = getAccessToken();
        if (!Boolean.TRUE.equals(tokenResponse.getSuccess()) || StrUtil.isBlank(tokenResponse.getAccessToken())) {
            throw new EsignException(tokenResponse.getCode(), tokenResponse.getMessage());
        }

        String url = properties.getAuthInfoUrl();
        AuthInfoRequest request = new AuthInfoRequest();
        request.setAuthFlowId(authFlowId);

        String body = JSON.toJSONString(request);

        try {
            HttpResponse response = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + tokenResponse.getAccessToken())
                    .body(body)
                    .contentType("application/json")
                    .timeout(properties.getReadTimeout().intValue())
                    .execute();

            if (!response.isOk()) {
                throw new EsignException("查询认证信息失败，HTTP状态码：" + response.getStatus());
            }

            String responseBody = response.body();
            AuthInfoResponse authInfoResponse = JSON.parseObject(responseBody, AuthInfoResponse.class);

            if (!Boolean.TRUE.equals(authInfoResponse.getSuccess())) {
                log.error("查询e签宝认证信息失败，code:{}, message:{}", authInfoResponse.getCode(), authInfoResponse.getMessage());
            }

            return authInfoResponse;
        } catch (Exception e) {
            log.error("查询e签宝认证信息异常", e);
            throw new EsignException("查询认证信息异常", e);
        }
    }

    @Override
    public AuthCallbackResult parseCallbackResult(String callbackJson) {
        if (StrUtil.isBlank(callbackJson)) {
            throw new IllegalArgumentException("回调参数不能为空");
        }
        return JSON.parseObject(callbackJson, AuthCallbackResult.class);
    }

    /**
     * 构建URL参数
     *
     * @param baseUrl 基础URL
     * @param params  参数Map
     * @return 完整URL
     */
    private String buildUrl(String baseUrl, Map<String, Object> params) {
        StringBuilder sb = new StringBuilder(baseUrl);
        if (!baseUrl.contains("?")) {
            sb.append("?");
        } else {
            sb.append("&");
        }

        boolean first = true;

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (!first) {
                sb.append("&");
            }
            first = false;
            Object value = entry.getValue();
            if (value != null) {
                sb.append(entry.getKey())
                        .append("=")
                        .append(cn.hutool.core.net.URLEncodeUtil.encode(value.toString(), StandardCharsets.UTF_8));
            }
        }

        return sb.toString();
    }
}
