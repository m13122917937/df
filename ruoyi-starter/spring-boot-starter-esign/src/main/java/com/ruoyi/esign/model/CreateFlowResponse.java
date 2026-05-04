package com.ruoyi.esign.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 创建签署流程响应结果
 *
 * @author ruoyi
 */
@Data
public class CreateFlowResponse {

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

    /**
     * 签署流程ID
     */
    @JsonProperty("flowId")
    private String flowId;
}
