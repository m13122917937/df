package com.ruoyi.esign.model;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * 添加签署人响应结果
 *
 * @author ruoyi
 */
@Data
public class AddSignerResponse {

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
     * 签署人信息列表
     */
    @JSONField(name = "signers")
    private List<SignerResult> signers;

    /**
     * 签署人结果
     */
    @Data
    public static class SignerResult {

        /**
         * 签署人ID
         */
        @JSONField(name = "signerId")
        private String signerId;

        /**
         * 签署链接
         */
        @JSONField(name = "signUrl")
        private String signUrl;
    }
}
