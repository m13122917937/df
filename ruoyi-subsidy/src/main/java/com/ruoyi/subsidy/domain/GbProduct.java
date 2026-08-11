package com.ruoyi.subsidy.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 国补商城 SPU。
 */
@Data
@Accessors(chain = true)
@TableName("gb_product")
public class GbProduct {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private String productCode;
    private String productName;
    private String subtitle;
    private String mainImageUrl;
    private String detailContent;
    private Integer recommended;
    private Integer sortOrder;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
