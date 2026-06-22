package com.ruoyi.bill.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 合同相关常量
 *
 * @author ruoyi
 * @date 2026-06-15
 */
public interface ContractConsts {

    /**
     * 合同类型
     */
    @Getter
    @AllArgsConstructor
    enum Type {
        PURCHASE(1, "采购合同"),
        FRAMEWORK(2, "框架协议"),
        ;

        private final Integer code;
        private final String message;
    }

    /**
     * 合同状态
     */
    @Getter
    @AllArgsConstructor
    enum Status {
        DRAFT(0, "草稿"),
        SIGNING(1, "签署中"),
        COMPLETED(2, "已完成"),
        REJECTED(3, "已拒签"),
        EXPIRED(4, "已过期"),
        CANCELED(5, "已撤销"),
        ;

        private final Integer code;
        private final String message;
    }
}
