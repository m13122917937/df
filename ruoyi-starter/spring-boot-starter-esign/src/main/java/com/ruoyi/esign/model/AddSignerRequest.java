package com.ruoyi.esign.model;

import lombok.Data;

import java.util.List;

/**
 * 添加签署人请求参数
 *
 * @author ruoyi
 */
@Data
public class AddSignerRequest {

    /**
     * 签署流程ID
     */
    private String flowId;

    /**
     * 签署人信息列表
     */
    private List<SignerInfo> signers;

    /**
     * 签署人信息
     */
    @Data
    public static class SignerInfo {

        /**
         * 签署人ID，开发者自定义，在一个流程中需要唯一
         */
        private String signerId;

        /**
         * 签署人名称，个人是姓名，企业是企业名称，最大长度不超过50字符
         */
        private String signerName;

        /**
         * 签署人类型，1 - 个人，2 - 企业，默认是1
         */
        private Integer signerType = 1;

        /**
         * 第三方用户ID，开发者平台用户唯一标识，最大长度不超过64字符
         */
        private String thirdPartyUserId;

        /**
         * 证件类型，个人默认身份证
         * 身份证 - CRED_PSN_CH_IDCARD
         */
        private String idType = "CRED_PSN_CH_IDCARD";

        /**
         * 证件号码，个人必填
         */
        private String idNumber;

        /**
         * 手机号，个人必填
         */
        private String mobile;

        /**
         * 邮箱，推送通知用
         */
        private String email;

        /**
         * 是否是发起方，默认false
         */
        private Boolean isInitiator = false;

        /**
         * 签署顺序，数字越小，签署顺序越靠前，默认0 同时签署
         */
        private Integer signOrder = 0;

        /**
         * 跳转链接，签署完成后重定向地址
         */
        private String redirectUrl;

        /**
         * 企业印章ID，企业必填
         */
        private String sealId;

        /**
         * 经办人信息，企业必填
         */
        private Operator operator;

        /**
         * 经办人信息
         */
        @Data
        public static class Operator {

            /**
             * 经办人第三方用户ID
             */
            private String thirdPartyUserId;

            /**
             * 经办人姓名
             */
            private String name;

            /**
             * 经办人证件类型
             */
            private String idType = "CRED_PSN_CH_IDCARD";

            /**
             * 经办人证件号码
             */
            private String idNumber;

            /**
             * 经办人手机号
             */
            private String mobile;

            /**
             * 经办人邮箱
             */
            private String email;
        }
    }
}
