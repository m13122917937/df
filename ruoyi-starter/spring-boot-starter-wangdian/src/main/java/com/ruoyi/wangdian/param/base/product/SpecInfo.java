package com.ruoyi.wangdian.param.base.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品规格信息实体类 - SpecInfo
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SpecInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    // 商家编码
    private String spec_no;

    // 规格码
    private String spec_code;

    // 条码
    private String barcode;

    // 规格名称
    private String spec_name;

    // 启用序列号 (0:不启用, 1:序列号, 2:随机序列号)
    private Integer sn_type;

}