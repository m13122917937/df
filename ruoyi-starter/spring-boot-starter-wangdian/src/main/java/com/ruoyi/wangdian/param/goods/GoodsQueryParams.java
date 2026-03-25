package com.ruoyi.wangdian.param.goods;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品查询参数（goods.Goods.queryWithSpec）
 *
 * @author ruoyi
 */
@Data
public class GoodsQueryParams implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 创建时间起始，格式：yyyy-MM-dd HH:mm:ss
     */
    @JsonProperty("start_time")
    private String startTime;

    /**
     * 创建时间结束，格式：yyyy-MM-dd HH:mm:ss
     */
    @JsonProperty("end_time")
    private String endTime;
}
