package com.ruoyi.esign.model;

import lombok.Data;

/**
 * 认证信息查询请求
 *
 * @author ruoyi
 */
@Data
public class AuthInfoRequest {

    /**
     * 认证流程id，查询认证信息必须传入
     */
    private String authFlowId;
}
