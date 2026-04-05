package com.ruoyi.esign.model;

import com.alibaba.fastjson2.annotation.JSONField;
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
    @JSONField(name = "flowId")
    private String flowId;
}
