package com.ruoyi.web.vo.product;

import lombok.Data;

import java.util.Date;

/**
 * 商品SKU视图对象
 *
 * @author ruoyi
 * @date 2025-12-17
 */
@Data

public class ProductSkuVO {


    private Long id;


    private String brand;


    private String category;


    private String spuCode;


    private String skuCode;


    private String productName;


    private String specName;



    private Date createTime;



    private Long snType;


}