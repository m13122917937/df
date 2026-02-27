package com.ruoyi.wangdian.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 分页参数 (pager 对象)
 */
@Data
public class Pager implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * [选填] 分页大小 (建议大单量卖家设置为 200 以下)
     */
    @JsonProperty("page_size")
    private Integer pageSize;

    /**
     * [选填] 页号 (从 0 开始)
     */
    @JsonProperty("page_no")
    private Integer pageNo;



    /**
     * [选填] 页号 (从 0 开始)
     */
    @JsonProperty("calc_total")
    private Integer calcTotal;
}