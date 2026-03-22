package com.ruoyi.web.form.order;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 待补全地址参数
 */
@Data

public class AddressCompletedParams implements Serializable {

    /**
     * 待补全地址参数集合
     */

    private List<AddressCompletedParam> addressCompletedParams;

    @Data

    @Accessors(chain = true)
    public static class AddressCompletedParam implements Serializable {


        private String originalOrderId;


        private String erpOrderId;


        private String addressee;


        private String phone;


        private String receivingAddress;

    }


}
