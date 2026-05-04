package com.ruoyi.esign.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 查询签署流程详情响应结果
 *
 * @author ruoyi
 */
@Data
public class QueryFlowResponse {

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

    /**
     * 业务自定义流水号
     */
    @JsonProperty("businessId")
    private String businessId;

    /**
     * 签署流程名称
     */
    @JsonProperty("flowName")
    private String flowName;

    /**
     * 签署流程状态
     * 0 - 草稿，1 - 签署中，2 - 签署完成，3 - 签署失败，4 - 已撤销，5 - 已过期
     */
    @JsonProperty("flowStatus")
    private Integer flowStatus;

    /**
     * 流程创建时间，时间戳，毫秒
     */
    @JsonProperty("createTime")
    private Long createTime;

    /**
     * 流程发起时间，时间戳，毫秒
     */
    @JsonProperty("startTime")
    private Long startTime;

    /**
     * 流程结束时间，时间戳，毫秒
     */
    @JsonProperty("endTime")
    private Long endTime;

    /**
     * 流程到期时间，时间戳，毫秒
     */
    @JsonProperty("expireTime")
    private Long expireTime;

    /**
     * 签署完成后的合同文件地址
     */
    @JsonProperty("fileUrl")
    private String fileUrl;

    /**
     * 签署人信息列表
     */
    @JsonProperty("signers")
    private List<SignerInfo> signers;

    /**
     * 签署人信息
     */
    @Data
    public static class SignerInfo {

        /**
         * 签署人ID
         */
        @JsonProperty("signerId")
        private String signerId;

        /**
         * 签署人名称
         */
        @JsonProperty("signerName")
        private String signerName;

        /**
         * 签署人类型，1 - 个人，2 - 企业
         */
        @JsonProperty("signerType")
        private Integer signerType;

        /**
         * 签署状态
         * 0 - 待签署，1 - 签署中，2 - 已签署，3 - 已拒绝，4 - 已超时
         */
        @JsonProperty("signStatus")
        private Integer signStatus;

        /**
         * 签署时间，时间戳，毫秒
         */
        @JsonProperty("signTime")
        private Long signTime;
    }
}
