package com.ruoyi.order.model.consts;

import com.ruoyi.common.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;

public interface ApplyConsts {


    @Getter
    @AllArgsConstructor
    enum ApplyStatus {
        APPLYING(1, "申请中"),
        REFUSE(2, "拒绝"),
        AGREE(3, "同意");

        private final Integer code;
        private final String message;


    }


    /**
     * 申请类型(1立即毁单 2免责毁单 4修改物流单号申请 )
     */
    @Getter
    @AllArgsConstructor
    enum Type {
        AT_ONCE_CANCEL(1, "立即毁单"),
        APPLY_CANCEL(2, "免责毁单"),
        ;

        private final Integer code;
        private final String message;


        public static Type getType(Integer code) {
            for (Type value : values()) {
                if (value.code.equals(code)) {
                    return value;
                }
            }
            throw new ServiceException("申请类型错误");
        }

    }

}
