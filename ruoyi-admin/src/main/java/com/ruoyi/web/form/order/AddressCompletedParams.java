package com.ruoyi.web.form.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 待补全地址参数
 */
@Data
@ApiModel("待补全地址")
public class AddressCompletedParams implements Serializable {

    /**
     * 待补全地址参数集合
     */
    @ApiModelProperty("待补全地址参数集合")
    private List<AddressCompletedParam> addressCompletedParams;

    @Data
    @ApiModel("待补全地址参数")
    @Accessors(chain = true)
    public static class AddressCompletedParam implements Serializable {

        @ApiModelProperty("商家单号")
        private String originalOrderId;

        @ApiModelProperty("erpID")
        private String erpOrderId;

        @ApiModelProperty("收件人")
        private String addressee;

        @ApiModelProperty("手机号")
        private String phone;

        @ApiModelProperty("收货地址")
        private String receivingAddress;

    }


}
