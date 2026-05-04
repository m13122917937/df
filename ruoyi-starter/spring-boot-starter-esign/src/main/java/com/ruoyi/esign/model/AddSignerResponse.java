package com.ruoyi.esign.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("signers")
    private List<SignerResult> signers;

    /**
     * 签署人结果
     */
    @Data
    public static class SignerResult {

        /**
         * 签署人ID
         */
        @JsonProperty("signerId")
        private String signerId;

        /**
         * 签署链接
         */
        @JsonProperty("signUrl")
        private String signUrl;
    }
}
