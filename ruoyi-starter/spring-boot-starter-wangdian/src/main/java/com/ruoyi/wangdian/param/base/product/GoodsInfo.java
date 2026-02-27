package com.ruoyi.wangdian.param.base.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品信息实体类 - GoodsInfo
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GoodsInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    // 货品编号
    private String goods_no;

    // 货品名称
    private String goods_name;

    // 分类名称
    private String class_name;

    // 品牌名称
    private String brand_name;

}