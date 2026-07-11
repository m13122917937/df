package com.ruoyi.order.model.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import cn.hutool.core.util.StrUtil;

public interface OrderConsts {


     static final String SHOP_NAME = "无界零售";

     static final String PLATFORM = "无界";

    @Getter
    @AllArgsConstructor
    public enum OrderStyle {

        /**
         * *
         */
        BILLION(0, "零售"),

        PUBLISHER(1, "微派"),

        BILLION_ACTIVATE(1, "线下验机激活 百亿补贴"),

        PDD_ACTIVATE(1, "顺丰包邮 微派服务"),

        CHECK_SN_BEFORE_DELIVERY(0, "发货前校验sn码"),

        SF_FREE_WP_CHECK_SN_BEFORE_DELIVERY(1, "顺丰包邮,微派服务,发货前校验sn码"),

        ;

        private Integer code;
        private String desc;

        public static OrderStyle getByCodeValue(Integer code) {
            for (OrderStyle value : values()) {
                if (value.code.equals(code)) {
                    return value;
                }
            }
            return null;
        }

        public static OrderStyle getByName(String desc) {
            if (StrUtil.isBlank(desc)) {
                return BILLION;
            }
            for (OrderStyle value : values()) {
                if (desc.contains(value.desc)) {
                    return value;
                }
            }
            return null;
        }
    }


    @Getter
    @AllArgsConstructor
    public enum OrderType {

        /**
         * * 采购类型（1:入仓,2:代发）
         */
        O2O(2, "一件代发"),

        PROCUREMENT(1, "入仓"),

        SALES(3, "销售订单");

        private Integer code;
        private String desc;
    }

    @Getter
    @AllArgsConstructor
    public enum OrderStatus {

        NEW(1, "新建中"),

        WAIT(2, "待发布"),

        TRADING(3, "报价中"),

        DELIVERY_ING(4, "发货中"),

        DELIVERY_END(5, "当日发货"),

        TRANSIT(6, "在途"),

        CHASE_ORDER(7, "追单"),

        ERROR(8, "异常订单"),

        RECEIPT(9, "待确定收货"),

        ENDING(10, "已完成"),

        REVOKE(11, "撤销"),

        AFTER_SALES(12, "售后"),

;

        private Integer code;
        private String desc;

        public static OrderStatus getByCode(Integer code) {
            for (OrderStatus value : values()) {
                if (value.code.equals(code)) {
                    return value;
                }
            }
            return null;
        }

    }

    @Getter
    @AllArgsConstructor
    public enum OrderSubStatus {

        NEW(10, "新建中"),

        NEW_REVOKE(11, "撤销订单到新建中"),

        WAIT_TRADING(21, "报价中撤销"),

        WAIT_ERROR(22, "异常订单撤销"),

        WAIT_DESTRO(23, "毁单to待发布"),

        WAIT_TIMEOUT(24, "超时流拍"),

        CHAS_TIMEOUT(25, "追单"),

        WAIT_IMEI(41, "待填写串码"),

        WAIT_SALES(42, "待填写物流信息和平台二销验证中"),

        EXPRESS_WAIT_SALES(43, "物流已经填写，平台二销验证中"),

        WAIT_SALES_ERROR(44, "平台已销售"),

        WAIT_EXPRESS(45, "平台未销售"),

        DELIVERY_ING_BACK(71, "发货中追单供应商未确定"),

        DELIVERY_END_BACK(72, "已发货追单供应商未确定"),

        DELIVERY_ING_BACK_CONF(73, "发货中追单供应商确定"),

        DELIVERY_END_BACK_CONF(74, "已发货追单供应商确定"),

        ERROR_LOGISTICS_CIRCULATION(81, "物流无流转信息"),

        ERROR_LOGISTICS_SIGN(82, "7日未签收异常"),

        ERROR_LOGISTICS_PHONE(83, "手机号后四位错误"),

        ERROR_LOGISTICS_FROM_KNOTTY(86, "快递疑难异常"),

        ERROR_SNED_TIME_KNOTTY(87, "快递疑难异常"),

        REVOKE_NEW(111, "新建订单撤销"),

        REVOKE_TRADING(112, "报价中订单撤销"),

        REVOKE_DELISTING(113, "晚上统一撤销"),

        AFTER_SALES_SEND_TIME(121, "售后待确认"),

        AFTER_SALES_SEND_CONFIRM(122, "采购商自留"),

        AFTER_SALES_SEND_REFUND(123, "商品退回"),


        ;

        private Integer code;
        private String desc;
    }


    @Getter
    @AllArgsConstructor
    public enum AddressStatus {

        NOT_SUPPLEMENTED(1, "未补"),

        SUCCESS(2, "已成功补地址");


        private Integer code;
        private String desc;
    }

    @Getter
    @AllArgsConstructor
    public enum HandleApply {

        NEED(0, "需要处理申请"),

        NO(1, "不需要");


        private Integer code;
        private String desc;
    }

    @AllArgsConstructor
    @Getter
    enum RevokeType{

        REFUND(0,"店铺后台急速退款"),

        CUSTOMER_REFUSE(1,"顾客拒签/拒收"),

        LOGISTICS_NO_TRANSFER(3,"24小时物流无揽收信息"),

        PROVIDER_INTERCEPT(4,"供应商私自拦截"),

        OTHER_CHANNEL_SHIPPING(5,"已经从其他渠道发货"),

        OTHER_CHANNEL_USER(6,"手动追单");

        private Integer code;

        private String desc;

        public static RevokeType getByCode(Integer code) {
            for (RevokeType value : values()) {
                if (value.code.equals(code)) {
                    return value;
                }
            }
            return null;
        }

    }


}
