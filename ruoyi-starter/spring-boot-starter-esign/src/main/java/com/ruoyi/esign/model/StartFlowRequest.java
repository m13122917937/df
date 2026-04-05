package com.ruoyi.esign.model;

import lombok.Data;

/**
 * 发起签署流程请求参数
 *
 * @author ruoyi
 */
@Data
public class StartFlowRequest {

    /**
     * 签署流程ID
     */
    private String flowId;
}
