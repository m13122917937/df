package com.ruoyi.subsidy.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 创建国补订单参数。
 */
@Data
@Accessors(chain = true)
public class GbOrderCreateParam {
    private Long memberId;
    private Long skuId;
    private Integer quantity;
    private String receiverName;
    private String receiverPhone;
    private String provinceName;
    private String cityName;
    private String districtName;
    private String detailAddress;
}
