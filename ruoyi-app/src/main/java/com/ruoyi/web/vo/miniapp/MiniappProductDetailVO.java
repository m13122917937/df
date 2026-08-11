package com.ruoyi.web.vo.miniapp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 小程序商品详情响应。
 */
@Data
public class MiniappProductDetailVO {
    private Long id;
    private Long categoryId;
    private String productName;
    private String subtitle;
    private String mainImageUrl;
    private List<String> imageUrls;
    private String detailContent;
    private BigDecimal discountRate;
    private BigDecimal discountCapAmount;
    private String saleProvinces;
    private List<MiniappSkuVO> skus;
}
