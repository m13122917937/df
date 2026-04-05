package com.ruoyi.esign.api;

import com.ruoyi.esign.model.*;

/**
 * e签宝认证授权API接口
 *
 * @author ruoyi
 */
public interface EsignAuthApi {

    /**
     * 获取访问令牌
     *
     * @return 访问令牌响应
     */
    AccessTokenResponse getAccessToken();

    /**
     * 构建个人认证URL
     *
     * @param request 个人认证请求参数
     * @return 认证URL
     */
    String buildPersonalAuthUrl(PersonalAuthRequest request);

    /**
     * 构建机构认证URL
     *
     * @param request 机构认证请求参数
     * @return 认证URL
     */
    String buildOrgAuthUrl(OrgAuthRequest request);

    /**
     * 查询认证信息
     *
     * @param authFlowId 认证流程ID
     * @return 认证信息响应
     */
    AuthInfoResponse getAuthInfo(String authFlowId);

    /**
     * 解析认证回调结果
     *
     * @param callbackJson 回调JSON字符串
     * @return 回调结果
     */
    AuthCallbackResult parseCallbackResult(String callbackJson);
}
