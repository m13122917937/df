package com.ruoyi.subsidy.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/** 国补商品轮播图。 */
@Data
@TableName("gb_product_image")
public class GbProductImage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private String imageUrl;
    private Integer sortOrder;
    private Date createTime;
}
