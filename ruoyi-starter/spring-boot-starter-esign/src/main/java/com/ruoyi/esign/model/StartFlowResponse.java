package com.ruoyi.esign.model;

import lombok.Data;

/**
 * 发起签署流程响应结果
 *
 * @author ruoyi
 */
@Data
public class StartFlowResponse {

    /**
     * 请求是否成功
     */
    private Boolean success;

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误信息
     */
    private String message;
}
