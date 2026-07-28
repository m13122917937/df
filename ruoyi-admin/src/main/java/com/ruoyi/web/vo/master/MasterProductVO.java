package com.ruoyi.web.vo.master;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 商品列表响应。
 */
@Data
public class MasterProductVO {

    /** 主键。 */
    private Long id;

    /** 品牌。 */
    private String brand;

    /** 品类。 */
    private String category;

    /** SPU 编码。 */
    private String spuCode;

    /** SKU 编码。 */
    private String skuCode;

    /** 商品名称。 */
    private String productName;

    /** 规格名称。 */
    private String specName;

    /** 商品条码。 */
    private String barCode;

    /** 串码管理类型。 */
    private Long snType;

    /** 创建时间。 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
