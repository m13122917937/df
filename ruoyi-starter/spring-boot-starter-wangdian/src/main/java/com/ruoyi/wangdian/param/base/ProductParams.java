package com.ruoyi.wangdian.param.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.wangdian.param.base.product.GoodsInfo;
import com.ruoyi.wangdian.param.base.product.SpecInfo;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProductParams {

    private GoodsInfo goodsInfo;

    private SpecInfo specInfoList;


}
