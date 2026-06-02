package com.ruoyi.esign.api;

import com.ruoyi.esign.model.v3.OrgAuthUrlRequest;
import com.ruoyi.esign.model.v3.OrgIdentityInfoRequest;
import com.ruoyi.esign.model.v3.OrgIdentityInfoResponse;

/**
 * e签宝认证授权API接口
 *
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

    /**
     * 查询机构认证信息
     *
     * @param request 查询参数
     * @return 机构认证信息
     */
    OrgIdentityInfoResponse getOrgIdentityInfo(OrgIdentityInfoRequest request);
}
