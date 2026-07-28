package com.ruoyi.web.vo.master;

import lombok.Data;

/**
 * 商品列表查询请求。
 */
@Data
public class MasterProductQueryRequest {

    /** 品牌。 */
    private String brand;

    /** 品类。 */
    private String category;

    /** 商品名称关键字。 */
    private String productNameLike;

    /** SPU 编码。 */
    private String spuCode;

    /** SKU 编码。 */
    private String skuCode;
}
