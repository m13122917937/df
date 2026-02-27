package com.ruoyi.kuaidi100.model.strategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 脱敏订单请求参数
 *
 * @author kuaidi100
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DesensitizedOrderRequest {

    /**
     * 平台类型
     */
    private PlatformType platformType;


    /**
     * 打印类型，NON：只下单不打印（默认）； IMAGE:生成图片短链；HTML:生成html短链； CLOUD:使用快递100云打印机打印，使用CLOUD时siid必填；ORDERFIRST:先下单后打印模式，此模式会下单取号，不会立即调用快递100云打印机打印，支持后续按需复打，此模式siid必填
     *
     * -------------------------------------
     * 来源：快递100API开放平台
     * 标题：电子面单接口-快递100API开放平台
     * 链接：https://api.kuaidi100.com/document/dianzimiandanV2
     */
    private String printType;

    /**
     * 平台授权信息
     */
    private PlatformAuthInfo authInfo;

    /**
     * 平台订单ID
     */
    private String thirdOrderId;

    /**
     * 快递公司编码
     */
    private String expressCom;

    /**
     * 收件人加密信息（已加密的数据）
     */
    private RecipientEncryptedInfo recipientInfo;

    /**
     * 寄件人信息
     */
    private SenderInfo senderInfo;

    /**
     * 货物信息
     */
    private CargoInfo cargoInfo;

    /**
     * 包裹重量
     */
    private String weight;

    /**
     * 包裹数量
     */
    private String quantity;

    /**
     * 包裹体积
     */
    private String volume;

    /**
     * 面单模板ID
     */
    private String templateSize;

    /**
     * 回调地址
     */
    private String callbackurl;

    /**
     * 月结卡号
     */
    private String monthCode;

    /**
     * 服务类型
     */
    private String serviceType;

    /**
     * 支付方式
     */
    private String payType;

    /**
     * 快递类型
     */
    private String expType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 收件人加密信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipientEncryptedInfo {
        /**
         * 收件人姓名（加密后）
         */
        private String name;

        /**
         * 收件人电话（加密后）
         */
        private String mobile;

        /**
         * 省份
         */
        private String province;

        /**
         * 城市
         */
        private String city;

        /**
         * 区/县
         */
        private String district;

        /**
         * 街道（快手需要）
         */
        private String street;

        /**
         * 详细地址（加密/脱敏后）
         */
        private String addr;

        /**
         * 收件人加密详细地址（拼多多/抖音/快手需要）
         */
        private String detailAddrEnc;
    }

    /**
     * 寄件人信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SenderInfo {
        /**
         * 寄件人姓名
         */
        private String name;

        /**
         * 寄件人电话
         */
        private String mobile;

        /**
         * 寄件人省份
         */
        private String province;

        /**
         * 寄件人城市
         */
        private String city;

        /**
         * 寄件人区/县
         */
        private String district;

        /**
         * 寄件人详细地址
         */
        private String addr;
    }

    /**
     * 货物信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CargoInfo {
        /**
         * 货物名称
         */
        private String name;

        /**
         * 货物数量
         */
        private String count;

        /**
         * 货物单位
         */
        private String unit;

        /**
         * 货物重量
         */
        private String weight;
    }
}