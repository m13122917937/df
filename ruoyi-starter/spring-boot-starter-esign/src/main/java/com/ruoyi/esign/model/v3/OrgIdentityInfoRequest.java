package com.ruoyi.esign.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 查询机构认证信息请求参数
 *
 * @author ruoyi
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrgIdentityInfoRequest {

    private String orgId;

    private String orgName;

    private String orgIDCardNum;

    private String orgIDCardType;
}
