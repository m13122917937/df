package com.ruoyi.esign.api;

import com.ruoyi.esign.model.v3.OrgAuthUrlRequest;

/**
 * e签宝认证授权API接口
 * 只保留：创建机构认证流程（V3版本）
 *
 * @author ruoyi
 */
public interface EsignAuthApi {

    /**
     * 创建机构认证流程（V3版本）
     *
     * @param request 机构认证请求参数
     * @return 认证跳转URL
     */
    String createOrgAuthUrl(OrgAuthUrlRequest request);
}
