package com.ruoyi.subsidy.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

/** 国补会员地址写入参数。 */
@Data
@Accessors(chain = true)
public class GbMemberAddressParam {
    private Long id;
    private Long memberId;
    private String receiverName;
    private String receiverPhone;
    private String provinceName;
    private String cityName;
    private String districtName;
    private String detailAddress;
    private Boolean defaultAddress;
}
